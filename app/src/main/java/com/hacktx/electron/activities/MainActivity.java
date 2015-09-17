package com.hacktx.electron.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hacktx.electron.R;
import com.hacktx.electron.ui.CameraSourcePreview;
import com.hacktx.electron.ui.VerificationDialog;
import com.hacktx.electron.utils.PreferencesUtils;
import com.hacktx.electron.vision.BarcodeTrackerFactory;
import com.hacktx.electron.vision.GraphicOverlay;
import com.hacktx.electron.vision.VisionCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private CameraSource mCameraSource;
    private boolean scanning;

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

        checkPermissions();

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(resultCode == ConnectionResult.SUCCESS) {
            initBarcodeDetector();
        } else if (resultCode == ConnectionResult.SERVICE_MISSING ||
                resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                resultCode == ConnectionResult.SERVICE_DISABLED) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCameraSource != null) {
            mCameraSource.release();
        }
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
            case R.id.action_help:
                showHelpDialog();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            List<String> permissions = new ArrayList<>();
            if(hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }

            if(!permissions.isEmpty()) {
                PreferencesUtils.setVolunteerId(MainActivity.this, "");
                PreferencesUtils.setFirstLaunch(MainActivity.this, true);
                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                finish();
            }
        }
    }

    private void startCameraSource() {
        try {
            mPreview.start(mCameraSource, mGraphicOverlay);
            scanning = true;
        } catch (Exception e) {
            showCameraErrorDialog();
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    private void checkIfShowWelcomeActivity() {
        if (PreferencesUtils.getFirstLaunch(this) || PreferencesUtils.getVolunteerId(this).isEmpty()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
    }

    private void initBarcodeDetector() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, new VisionCallback() {
            @Override
            public void onFound(final Barcode barcode) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(scanning && barcode.format == Barcode.QR_CODE) {
                            scanning = false;
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(100);
                            showConfirmationDialog(barcode.rawValue);
                        }
                    }
                });
            }
        });
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        mCameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        if(!barcodeDetector.isOperational()) {
            showDetectorErrorDialog();
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
        final VerificationDialog dialog = new VerificationDialog(email, this);
        dialog.show();
        dialog.findViewById(R.id.verifyDialogDismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.dialog_verify_snackbar_dismissed, dialog.getAttendee().getName()), Snackbar.LENGTH_SHORT).show();
            }
        });
        dialog.findViewById(R.id.verifyDialogCheckIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.dialog_verify_snackbar_checked_in, dialog.getAttendee().getName()), Snackbar.LENGTH_SHORT).show();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                scanning = true;
            }
        });

        dialog.show();
    }

    private void showHelpDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        dialog.show();

        dialog.findViewById(R.id.helpDialogOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showDetectorErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.dialog_detector_error_title);
        builder.setMessage(R.string.dialog_detector_error_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showCameraErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.dialog_camera_error_title);
        builder.setMessage(R.string.dialog_camera_error_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}