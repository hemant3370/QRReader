package hemant3370.com.qrreader.Activities

import android.Manifest
import androidx.room.Room
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import hemant3370.com.qrreader.R
import hemant3370.com.qrreader.Storage.QRDatabase
import hemant3370.com.qrreader.Storage.QRModel
import hemant3370.com.qrreader.Utils.Utils
import net.glxn.qrgen.android.QRCode
import java.util.*


class QRGeneraterActivity : AppCompatActivity() {

    var imageView: ImageView? = null
    var textView: TextView? = null
    var bitMap: Bitmap? = null
    var text: String = ""
    private lateinit var websiteButton: View
    internal lateinit var db: QRDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerater)
        db = Room.databaseBuilder(applicationContext,
                QRDatabase::class.java, "qr.db").fallbackToDestructiveMigration().build()
        websiteButton = findViewById(R.id.website_button)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        imageView = findViewById(R.id.qr_imageView)
        textView = findViewById(R.id.qr_text)
        handleIntent(intent)
        val uri = QRGeneraterActivity.getImageUri(this, bitMap!!, text)
        Thread(Runnable {
            db.daoAccess().insertOnlySingleRecord(QRModel(text,uri.toString(), Date().toString()))
        }).start()
        val shareButton = findViewById<View>(R.id.share_button)
        shareButton.setOnClickListener{
            shareQR()
        }
        val saveButton = findViewById<View>(R.id.save_button)
        saveButton.setOnClickListener{
            if (isStoragePermissionGranted() && bitMap != null) {
                val i = Intent()
                i.action = android.content.Intent.ACTION_VIEW
                i.setDataAndType(uri, "image/jpeg")
                startActivity(i)
            }
        }
        websiteButton.setOnClickListener(
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
            if (Build.VERSION.SDK_INT < 29) {
                val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, text, null)
                return Uri.parse(path)
            }
            // Scoped storage: insert through MediaStore, no storage permission needed
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "QR_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.TITLE, text)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QRReader")
            }
            val resolver = inContext.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
            resolver.openOutputStream(uri)!!.use { inImage.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            return uri
        }
    }


    fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT in 23..28) {
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
                websiteButton.visibility = View.GONE
            }
            bitMap = QRCode.from(text.toString()).bitmap()
            imageView?.setImageBitmap(bitMap)

        }
    }
}
