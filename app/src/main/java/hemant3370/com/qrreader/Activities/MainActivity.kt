package hemant3370.com.qrreader.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.View
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import hemant3370.com.qrreader.R
import hemant3370.com.qrreader.Adapters.PastQRcodeAdapter
import hemant3370.com.qrreader.databinding.ActivityMainBinding
import hemant3370.com.qrreader.Services.ClipboardListenerService
import hemant3370.com.qrreader.Storage.QRDatabase
import hemant3370.com.qrreader.Storage.QRModel
import hemant3370.com.qrreader.Utils.DbWorkerThread
import net.glxn.qrgen.android.QRCode
import java.util.*




class MainActivity : AppCompatActivity() {

    private val RESULT_SCAN: Int = 3370
    private var db: QRDatabase? = null
    private val mUiHandler = Handler()
    private lateinit var mDbWorkerThread: DbWorkerThread
    private var text: String? = null
    private lateinit var binding: ActivityMainBinding
    val listener: ((QRModel) -> Unit) = {
        openDetails(it.text)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        // Edge-to-edge is enforced on recent Android versions; keep content clear of the system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            binding.appBar.setPadding(0, bars.top, 0, 0)
            binding.fab.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = resources.getDimensionPixelSize(R.dimen.fab_margin) + bars.bottom
                rightMargin = resources.getDimensionPixelSize(R.dimen.fab_margin) + bars.right
            }
            binding.content.pastRecyclerView.setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.list_bottom_padding) + bars.bottom)
            insets
        }
        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()
        db = QRDatabase.getInstance(this)

        if (!ourKeyboardEnabled()) {
            val simpleAlert = AlertDialog.Builder(this@MainActivity).create()
            simpleAlert.setTitle("Permission")
            simpleAlert.setMessage("Do you want QR Scanning built into your keyboard? If Yes, Android will warn you about critical Information Leaks. Relax, Our's is a QR Scanner and not a generic keyboard. Plus this a offline App.")

            simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", { dialogInterface, i ->
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            })
            simpleAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "No", { dialogInterface, i ->
                simpleAlert.dismiss()
            })
            simpleAlert.show()
        }
        startService(Intent(this, ClipboardListenerService::class.java))
        binding.content.pastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val task = Runnable {
            val list =
                    db?.daoAccess()?.fetchAllData()
            mUiHandler.post({
                showHistory(list!!)
            })
        }
        mDbWorkerThread.postTask(task)

        binding.fab.setOnClickListener { _ ->
            if (isCameraDisabled()){
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        RESULT_SCAN)
            }
            else{

                openScanner()
            }
        }
    }
    private fun showHistory(list: List<QRModel>) {
        binding.content.pastRecyclerView.adapter = PastQRcodeAdapter(list, listener)
        binding.content.emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }
    fun openScanner(){
        val scanner = Intent(this, ScannerActivity::class.java)
        startActivityForResult(scanner, RESULT_SCAN)
    }
    fun openDetails(text: String){
        val intent = Intent(this, QRGeneraterActivity::class.java)
        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, text)
        startActivity(intent)
    }
    fun storeInHistory(text: String?){
        if (text != null) {
            this.text = text
            if (Build.VERSION.SDK_INT >= 29 || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Thread(Runnable {
                    db?.daoAccess()?.insertOnlySingleRecord(QRModel(text, QRGeneraterActivity.getImageUri(this, QRCode.from(text).bitmap(), text).toString(), Date().toString()))
                    val list = db?.daoAccess()?.fetchAllData()
                    runOnUiThread {
                        showHistory(list!!)
                    }
                }).start()

            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        RESULT_SCAN)
            }
        }
    }
    fun ourKeyboardEnabled(): Boolean{
        val packageLocal = packageName
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        try {
            val mInputMethodProperties = imm.getEnabledInputMethodList()
            return mInputMethodProperties.map { it.packageName }.contains(packageLocal)
        }
        catch (exception: Exception){
            Toast.makeText(this,exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        return false
    }
    fun isCameraDisabled(): Boolean {
        return ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> consume { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
            else -> { // Note the block
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }
    inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_SCAN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                val result = data?.getStringExtra("result")
                    storeInHistory(result)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RESULT_SCAN -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if ((permissions.contains(Manifest.permission.CAMERA))) {
                        openScanner()
                    }
                    if ((permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE))){
                        storeInHistory(text)
                    }
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onDestroy() {
        mDbWorkerThread.quit()
        super.onDestroy()
    }
}
