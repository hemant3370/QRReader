package hemant3370.com.qrreader.Scanner


import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import hemant3370.com.qrreader.R




/**
 * A simple [Fragment] subclass.
 */
class ScannerFragment : BottomSheetDialogFragment(), QRCodeReaderView.OnQRCodeReadListener {

    private var isTorchOn = false
    private var inputView: QRCodeReaderView? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater?.inflate(R.layout.fragment_scanner, container, false)
        inputView = rootView?.findViewById<View>(R.id.qrdecoderview) as QRCodeReaderView
        inputView?.setOnQRCodeReadListener(this)
        val flashButton = rootView.findViewById<ImageView>(R.id.close_button)
        flashButton?.setOnClickListener {
            isTorchOn = !isTorchOn
            inputView?.setTorchEnabled(isTorchOn)
        }
// Use this function to enable/disable decoding
        inputView?.setQRDecodingEnabled(true)

        // Use this function to change the autofocus interval (default is 5 secs)
        inputView?.setAutofocusInterval(2000L)

        // Use this function to enable/disable Torch

        inputView?.setBackCamera()
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            inputView?.startCamera()
        } else {

        }
        return rootView
    }
    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QR Code", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context,"Text Copied to Clipboard",Toast.LENGTH_SHORT).show()
    }
}
