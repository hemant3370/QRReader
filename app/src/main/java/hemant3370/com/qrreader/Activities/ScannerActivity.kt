package hemant3370.com.qrreader.Activities

import android.Manifest
import android.app.Activity
import android.arch.persistence.room.Room
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import hemant3370.com.qrreader.R
import hemant3370.com.qrreader.Storage.QRDatabase


class ScannerActivity : AppCompatActivity(), QRCodeReaderView.OnQRCodeReadListener {


    private var inputView: QRCodeReaderView? = null
    private var isTorchOn = false
    internal lateinit var db: QRDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        db = Room.databaseBuilder(applicationContext,
                QRDatabase::class.java, "qr.db").fallbackToDestructiveMigration().build()
        supportActionBar?.setHomeButtonEnabled(true)
        inputView = findViewById(R.id.qrdecoderview)
        inputView?.setOnQRCodeReadListener(this)
        val flashButton = findViewById<ImageView>(R.id.close_button)
        flashButton.setOnClickListener {
            isTorchOn = !isTorchOn
            inputView?.setTorchEnabled(isTorchOn)
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
        val switchButton = findViewById<ImageView>(R.id.switch_button)
        switchButton.visibility = View.GONE
        // Use this function to enable/disable decoding
        inputView?.setQRDecodingEnabled(true)

        // Use this function to change the autofocus interval (default is 5 secs)
        inputView?.setAutofocusInterval(2000L)

        // Use this function to enable/disable Torch

        inputView?.setBackCamera()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            inputView?.startCamera()
        } else {

        }
    }
    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QR Code", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this,"Text Copied to Clipboard", Toast.LENGTH_SHORT).show()
        val returnIntent = Intent()
        returnIntent.putExtra("result", text)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }


}
