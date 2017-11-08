package hemant3370.com.qrreader.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import hemant3370.com.qrreader.R
import net.glxn.qrgen.android.QRCode


class QRGeneraterActivity : AppCompatActivity() {

    var imageView: ImageView? = null
    var textView: TextView? = null
    var bitMap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerater)
        imageView = findViewById(R.id.qr_imageView)
        textView = findViewById(R.id.qr_text)
        handleIntent(intent)
        val shareButton = findViewById<ImageButton>(R.id.share_button)
        shareButton.setOnClickListener{
            
        }
        val saveButton = findViewById<ImageButton>(R.id.save_button)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    fun handleIntent(intent: Intent?){
        val text = intent
                ?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        if (text != null && text.isNotEmpty() && text.isNotBlank()){
            textView?.setText(text)
            bitMap = QRCode.from(text.toString()).bitmap()
            imageView?.setImageBitmap(bitMap)

        }
    }
}
