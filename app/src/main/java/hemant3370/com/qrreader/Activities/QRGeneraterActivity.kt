package hemant3370.com.qrreader.Activities

import android.Manifest
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import hemant3370.com.qrreader.R
import hemant3370.com.qrreader.Storage.QRDatabase
import hemant3370.com.qrreader.Storage.QRModel
import hemant3370.com.qrreader.Utils.Utils
import kotlinx.android.synthetic.main.activity_qrgenerater.*
import net.glxn.qrgen.android.QRCode
import java.io.ByteArrayOutputStream
import java.util.*


class QRGeneraterActivity : AppCompatActivity() {

    var imageView: ImageView? = null
    var textView: TextView? = null
    var bitMap: Bitmap? = null
    var text: String = ""
    internal lateinit var db: QRDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerater)
        db = Room.databaseBuilder(applicationContext,
                QRDatabase::class.java, "qr.db").fallbackToDestructiveMigration().build()
        imageView = findViewById(R.id.qr_imageView)
        textView = findViewById(R.id.qr_text)
        handleIntent(intent)
        val uri = QRGeneraterActivity.getImageUri(this, bitMap!!, text)
        Thread(Runnable {
            db.daoAccess().insertOnlySingleRecord(QRModel(text,uri.toString(), Date().toString()))
        }).start()
        val shareButton = findViewById<ImageButton>(R.id.share_button)
        shareButton.setOnClickListener{
            shareQR()
        }
        val saveButton = findViewById<ImageButton>(R.id.save_button)
        saveButton.setOnClickListener{
            if (isStoragePermissionGranted() && bitMap != null) {
                val i = Intent()
                i.action = android.content.Intent.ACTION_VIEW
                i.setDataAndType(uri, "image/jpeg")
                startActivity(i)
            }
        }
        website_button.setOnClickListener(
                {
                   Utils.openIfUrl(text, this)
                }
        )
    }
    fun shareQR(){
        if (isStoragePermissionGranted() && bitMap != null) {
            val bitmapUri = getImageUri(this, bitMap!!, text)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
            startActivity(Intent.createChooser(intent, "Share QR Code"))
        }
    }

    companion object {
        fun getImageUri(inContext: Context, inImage: Bitmap, text: String): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, text, null)
            return Uri.parse(path)
        }
    }


    fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Please try now.",Toast.LENGTH_SHORT).show()
        }
    }
    fun handleIntent(intent: Intent?){
        val text = intent
                ?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        if (text != null && text.isNotEmpty() && text.isNotBlank()){
            this.text = text.toString()
            textView?.setText(text)
            if (!Utils.checkForURL(text.toString())){
                website_button.visibility = View.GONE
            }
            bitMap = QRCode.from(text.toString()).bitmap()
            imageView?.setImageBitmap(bitMap)

        }
    }
}
