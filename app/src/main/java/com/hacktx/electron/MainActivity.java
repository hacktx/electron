package com.hacktx.electron;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
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
            case R.id.action_enter_email: showEmailDialog(); return true;
            case R.id.action_settings: return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleResult(Result rawResult) {
        showConfirmationDialog(rawResult.getText());
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
                mScannerView.startCamera();
            }
        });
        builder.setNegativeButton(R.string.dialog_verify_deny, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.startCamera();
            }
        });
        builder.show();
    }
}