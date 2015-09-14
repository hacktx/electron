package com.hacktx.electron.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hacktx.electron.R;
import com.hacktx.electron.model.Attendee;

public class VerificationDialog extends Dialog {

    private Attendee attendee;

    public VerificationDialog(Attendee attendee, Context context) {
        super(context);
        this.attendee = attendee;

        setupWindowParameters();
        verifyRegistrationInformation();
        setupTextViews();
    }

    private void setupWindowParameters() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_verify);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    private void verifyRegistrationInformation() {
        LinearLayout titleContainer = (LinearLayout) findViewById(R.id.dialogTitleContainer);
        LinearLayout successContainer = (LinearLayout) findViewById(R.id.verifyDialogSuccessContainer);
        LinearLayout errorContainer = (LinearLayout) findViewById(R.id.verifyDialogErrorContainer);
        TextView errorMessage = (TextView) findViewById(R.id.verifyDialogErrorText);
        if(attendee.getAge() < 18) {
            titleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
            errorMessage.setText(R.string.dialog_verify_underage);
            successContainer.setVisibility(View.GONE);
            errorContainer.setVisibility(View.VISIBLE);
        }

        if(!attendee.isWaiverSigned()) {
            titleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
            errorMessage.setText(R.string.dialog_verify_no_waiver);
            successContainer.setVisibility(View.GONE);
            errorContainer.setVisibility(View.VISIBLE);
        }

        if(!attendee.isCheckedIn()) {
            titleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
            errorMessage.setText(R.string.dialog_verify_checked_in);
            successContainer.setVisibility(View.GONE);
            errorContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setupTextViews() {
        ((TextView) findViewById(R.id.verifyDialogFullName)).setText(attendee.getName());
        ((TextView) findViewById(R.id.verifyDialogEmail)).setText(attendee.getEmail());
        ((TextView) findViewById(R.id.verifyDialogAge)).setText(Integer.toString(attendee.getAge()));
        ((TextView) findViewById(R.id.verifyDialogWaiver)).setText(attendee.isWaiverSigned() ? R.string.dialog_verify_waiver_status_true : R.string.dialog_verify_waiver_status_false);
        ((TextView) findViewById(R.id.verifyDialogCheckedIn)).setText(attendee.isCheckedIn() ? R.string.dialog_verify_check_in_status_true : R.string.dialog_verify_waiver_status_true);
    }
}
