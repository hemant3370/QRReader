package hemant3370.com.qrreader

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import hemant3370.com.qrreader.Scanner.ScannerFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val RESULT_PICK_CONTACT: Int = 3370

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    RESULT_PICK_CONTACT)

        }
        if (!ourKeyboardEnabled()){
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
        fab.setOnClickListener { view ->
            val fragment = ScannerFragment()
            fragment.show(supportFragmentManager, "Scanner")
        }
    }
    fun ourKeyboardEnabled(): Boolean{
        val packageLocal = packageName
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        try {
            var mInputMethodProperties = imm.getEnabledInputMethodList()
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RESULT_PICK_CONTACT -> {

                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
}
