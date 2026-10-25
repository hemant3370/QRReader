package hemant3370.com.qrreader.Keyboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.inputmethodservice.InputMethodService
import androidx.core.content.ContextCompat
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import hemant3370.com.qrreader.Activities.MainActivity
import hemant3370.com.qrreader.R

/**
 * Created by hemantsingh on 08/11/17.
 */
class QRInputIME : InputMethodService(), QRCodeReaderView.OnQRCodeReadListener {

    private var inputView: QRCodeReaderView? = null
    private var isTorchOn = false

    override fun onDestroy() {
        inputView?.stopCamera()
        super.onDestroy()
    }

    override fun onBindInput() {
        super.onBindInput()
        inputView?.stopCamera()
    }

    override fun onUnbindInput() {
        inputView?.stopCamera()
        super.onUnbindInput()
    }

    override fun onCreateInputView(): View {

        val rootView = LayoutInflater.from(ContextThemeWrapper(this, R.style.AppTheme)).inflate(R.layout.qr_scanner, null)
        // Keyboard-sized panel; grow by the nav bar height so the buttons stay above it
        val panelHeight = resources.getDimensionPixelSize(R.dimen.keyboard_height)
        rootView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, panelHeight)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(nav.left, 0, nav.right, nav.bottom)
            v.layoutParams = v.layoutParams.apply { height = panelHeight + nav.bottom }
            insets
        }
        inputView = rootView.findViewById(R.id.qrdecoderview)
        inputView?.setOnQRCodeReadListener(this)
        val flashButton = rootView.findViewById<ImageView>(R.id.close_button)
        flashButton.setOnClickListener {
            isTorchOn = !isTorchOn
            inputView?.setTorchEnabled(isTorchOn)
        }

        // Same behaviour as the system keyboards: tap cycles to the next keyboard,
        // long-press opens the keyboard picker.
        val switchButton = rootView.findViewById<ImageView>(R.id.switch_button)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        switchButton.setOnClickListener {
            val token = window.window?.attributes?.token
            if (token == null || !imm.switchToNextInputMethod(token, false)) {
                imm.showInputMethodPicker()
            }
        }
        switchButton.setOnLongClickListener {
            imm.showInputMethodPicker()
            true
        }

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
            val myActivity = Intent(this, MainActivity::class.java)
            myActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(myActivity)
        }
        return rootView
    }

    override fun onQRCodeRead(text: String, points: Array<PointF>) {

        val ic = currentInputConnection
        ic.commitText(text, 1)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        this.hideWindow()
    }

}