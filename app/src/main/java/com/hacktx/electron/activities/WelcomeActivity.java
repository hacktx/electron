package com.hacktx.electron.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hacktx.electron.R;
import com.hacktx.electron.utils.PreferencesUtils;

public class WelcomeActivity extends BaseActivity {

    private EditText volunteerIdEditText;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_welcome);

        volunteerIdEditText = (EditText) findViewById(R.id.welcomeActivityEditText);

        setupVolunteerIdForm();
    }

    private void setupVolunteerIdForm() {
        volunteerIdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
            PreferencesUtils.setVolunteerId(WelcomeActivity.this, volunteerId);
            PreferencesUtils.setFirstLaunch(WelcomeActivity.this, false);
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.activity_welcome_bad_id, Snackbar.LENGTH_SHORT).show();
        }
    }
}