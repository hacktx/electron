package com.hacktx.electron.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hacktx.electron.R;
import com.hacktx.electron.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity {

    private EditText volunteerIdEditText;
    private boolean hasPermissions = false;

    final private int REQUEST_PERMISSIONS = 1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_welcome);

        volunteerIdEditText = (EditText) findViewById(R.id.welcomeActivityEditText);

        checkPermissions();
        setupVolunteerIdForm();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
            List<String> permissions = new ArrayList<>();
            if(hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }

            if(!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSIONS);
            } else {
                hasPermissions = true;
            }
        } else {
            hasPermissions = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean denied = false;
        if(requestCode == REQUEST_PERMISSIONS) {
            for(int i = 0; i < permissions.length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    denied = true;
                }
            }
        }

        hasPermissions = !denied;

        if(denied) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.dialog_perms_title);
            builder.setMessage(R.string.dialog_perms_text);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

            findViewById(R.id.welcomeActivityPermsErrorContainer).setVisibility(View.VISIBLE);
            findViewById(R.id.welcomeActivitySave).setEnabled(false);
        }
    }

    private void setupVolunteerIdForm() {
        volunteerIdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && hasPermissions) {
                    storeVolunteerId();
                }
                return true;
            }
        });

        findViewById(R.id.welcomeActivitySave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeVolunteerId();
            }
        });
    }

    private void storeVolunteerId() {
        String volunteerId = volunteerIdEditText.getText().toString();
        if (!volunteerId.isEmpty()) {
            PreferencesUtils.setVolunteerEmail(WelcomeActivity.this, volunteerId);
            PreferencesUtils.setFirstLaunch(WelcomeActivity.this, false);
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.activity_welcome_bad_id, Snackbar.LENGTH_SHORT).show();
        }
    }
}