package com.hacktx.electron.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hacktx.electron.R;
import com.hacktx.electron.utils.PreferencesUtils;
import com.hacktx.electron.vision.BarcodeTrackerFactory;
import com.hacktx.electron.vision.VisionCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private BarcodeDetector barcodeDetector;
    private CameraSource mCameraSource;
    private boolean scanning, cameraRunning;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        checkIfShowWelcomeActivity();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            getSupportActionBar().setTitle(R.string.app_name);
        }

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(null, new VisionCallback() {
            @Override
            public void onFound(final Barcode barcode) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(scanning) {
                            scanning = false;
                            showConfirmationDialog(barcode.rawValue);
                        }
                    }
                });
            }
        });
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if(!barcodeDetector.isOperational()) {
            Snackbar.make(findViewById(android.R.id.content), R.string.activity_main_detector_nonoperational, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(camera != null) {
            camera.startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(camera != null) {
            camera.stopPreview();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_enter_email:
                showEmailDialog();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkIfShowWelcomeActivity() {
        if (PreferencesUtils.getFirstLaunch(this) || PreferencesUtils.getVolunteerId(this).isEmpty()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
    }

    private void showEmailDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_email);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        final EditText emailEditText = (EditText) dialog.findViewById(R.id.emailDialogEditText);
        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialog.dismiss();
                    showConfirmationDialog(emailEditText.getText().toString());
                }
                return true;
            }
        });

        dialog.findViewById(R.id.emailDialogCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.emailDialogOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showConfirmationDialog(emailEditText.getText().toString());
            }
        });
    }

    private void showConfirmationDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.dialog_verify_title);
        builder.setMessage(email);
        builder.setPositiveButton(R.string.dialog_verify_approve, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Notify Nucleus
                dialog.dismiss();
                scanning = true;
            }
        });
        builder.setNegativeButton(R.string.dialog_verify_deny, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                scanning = true;
            }
        });
        builder.show();
    }

    private void startCamera() {
        if(cameraRunning) {
            return;
        }

        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(findViewById(android.R.id.content), R.string.activity_main_camera_failed, Snackbar.LENGTH_INDEFINITE).show();
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                setCameraFrameCallback();
                camera.startPreview();
                cameraRunning = true;
                scanning = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopCamera() {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            cameraRunning = false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(camera != null) {
            camera.setDisplayOrientation(90);
            setCameraFrameCallback();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCamera();
    }

    private void setCameraFrameCallback() {
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;

                YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

                byte[] bytes = out.toByteArray();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                SparseArray<Barcode> barcodes = barcodeDetector.detect(new Frame.Builder().setBitmap(bitmap).build());
                for(int i = 0; i < barcodes.size(); i++) {
                    Barcode barcode = barcodes.get(barcodes.keyAt(i));
                    System.out.println("BARCODE!: " + barcode.rawValue);
                    if(scanning) {
                        scanning = false;
                        System.out.println("DIALOG");
                        showConfirmationDialog(barcode.rawValue);
                    }
                }
            }
        });
    }
}