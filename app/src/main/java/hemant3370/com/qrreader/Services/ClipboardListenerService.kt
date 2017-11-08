package hemant3370.com.qrreader.Services

import android.app.Service
import android.content.ClipboardManager
import android.content.Intent
import android.os.IBinder
import android.widget.Toast


/**
 * Created by hemantsingh on 08/11/17.
 */
class ClipboardListenerService: Service() {
    private val TAG = "ClipboardManager"
    private var mClipboardManager: ClipboardManager? = null

    override fun onCreate() {
        super.onCreate()
        // TODO: Show an ongoing notification when this service is running.
        mClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        mClipboardManager!!.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mClipboardManager != null) {
            mClipboardManager!!.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

     private val mOnPrimaryClipChangedListener = object : ClipboardManager.OnPrimaryClipChangedListener {
        override fun onPrimaryClipChanged() {
            val clip = mClipboardManager!!.getPrimaryClip()
            Toast.makeText(this@ClipboardListenerService,clip.getItemAt(0).text,Toast.LENGTH_SHORT).show()
        }
    }

}