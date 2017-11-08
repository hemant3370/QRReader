package hemant3370.com.qrreader.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import hemant3370.com.qrreader.R
import hemant3370.com.qrreader.Services.ClipboardListenerService
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern




class MainActivity : AppCompatActivity() {

    private val RESULT_SCAN: Int = 3370
    val URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    RESULT_SCAN)

        }
        if (!ourKeyboardEnabled()){
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
        startService(Intent(this, ClipboardListenerService::class.java))
        fab.setOnClickListener { view ->
            val scanner = Intent(this, ScannerActivity::class.java)
            startActivityForResult(scanner, RESULT_SCAN)
        }
    }
    fun checkForURL(text: String?) : Boolean {
        val p = Pattern.compile(URL_REGEX)
        val m = p.matcher(text)//replace with string to compare
        return m.find()
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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_SCAN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                val result = data?.getStringExtra("result")
                if (result != null && checkForURL(result)) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(result)
                    startActivity(i)
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RESULT_SCAN -> {
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
}
