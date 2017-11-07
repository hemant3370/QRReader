package hemant3370.com.qrreader;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.inputmethodservice.InputMethodService;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

/**
 * Created by hemantsingh on 07/11/17.
 */

public class QRInputIME extends InputMethodService implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView inputView;
    private boolean isTorchOn = false;
    private static final int RESULT_PICK_CONTACT = 3370;

    @Override
    public void onDestroy() {
        inputView.stopCamera();
        super.onDestroy();
    }

    @Override public View onCreateInputView() {

         final View rootView  =
                getLayoutInflater().inflate(R.layout.qr_scanner, null);
         ImageView flashButton = rootView.findViewById(R.id.close_button);
        flashButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 isTorchOn = !isTorchOn;
                 inputView.setTorchEnabled(isTorchOn);
             }
         });

        ImageView switchButton = rootView.findViewById(R.id.switch_button);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                if (imeManager != null) {
                  if (!imeManager.switchToLastInputMethod(getWindow().getWindow().getAttributes().token)) {
                        imeManager.switchToNextInputMethod(getWindow().getWindow().getAttributes().token, false);
                  }
                }
            }
        });
         inputView = (QRCodeReaderView) rootView.findViewById(R.id.qrdecoderview);
                 inputView.setOnQRCodeReadListener(this);
        // Use this function to enable/disable decoding
        inputView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        inputView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch

        inputView.setBackCamera();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            inputView.startCamera();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Camera permission required for scanning QR Code.");
            // Create the AlertDialog object and return it
            builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent myActivity = new Intent(QRInputIME.this, MainActivity.class);
                    startActivity(myActivity);
                }
            });
            builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    QRInputIME.this.hideWindow();
                }
            });
            builder.create().show();
        }
        return rootView;
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

            InputConnection ic = getCurrentInputConnection();
            ic.commitText(text, 1);

            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            this.hideWindow();


    }

}