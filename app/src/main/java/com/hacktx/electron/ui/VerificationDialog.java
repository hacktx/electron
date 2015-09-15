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
import com.hacktx.electron.network.ElectronClient;
import com.hacktx.electron.network.ElectronService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VerificationDialog extends Dialog {

    private String email;
    private Attendee attendee;
    int errorCount = 0;

    private LinearLayout titleContainer, successContainer, issueContainer;
    private TextView issueMessage, textViewName, textViewEmail, textViewAge, textViewWaiver, textViewCheckedIn;

    public VerificationDialog(String email, Context context) {
        super(context);

        setupWindowParameters();

        this.email = email;

        titleContainer = (LinearLayout) findViewById(R.id.dialogTitleContainer);
        successContainer = (LinearLayout) findViewById(R.id.verifyDialogSuccessContainer);
        issueContainer = (LinearLayout) findViewById(R.id.verifyDialogIssueContainer);

        issueMessage = (TextView) findViewById(R.id.verifyDialogIssueText);
        textViewName = (TextView) findViewById(R.id.verifyDialogFullName);
        textViewEmail = (TextView) findViewById(R.id.verifyDialogEmail);
        textViewAge = (TextView) findViewById(R.id.verifyDialogAge);
        textViewWaiver = (TextView) findViewById(R.id.verifyDialogWaiver);
        textViewCheckedIn = (TextView) findViewById(R.id.verifyDialogCheckedIn);
    }

    @Override
    public void show() {
        super.show();
        getAttendeeInfo();
    }

    public Attendee getAttendee() {
        return attendee;
    }

    private void getAttendeeInfo() {
        ElectronService electronService = ElectronClient.getInstance().getApiService();
        electronService.getRegistrationData(email, new Callback<Attendee>() {
            @Override
            public void success(Attendee attendee, Response response) {
                VerificationDialog.this.attendee = attendee;

                verifyRegistrationInformation();
                setupTextViews();

                findViewById(R.id.verifyDialogProgressBarContainer).setVisibility(View.GONE);
                titleContainer.setVisibility(View.VISIBLE);
                findViewById(R.id.verifyDialogInfoContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.verifyDialogButtonsContainer).setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                findViewById(R.id.verifyDialogProgressBarContainer).setVisibility(View.GONE);

                titleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
                issueMessage.setText(R.string.dialog_verify_fetch_error);
                successContainer.setVisibility(View.GONE);
                titleContainer.setVisibility(View.VISIBLE);
                issueContainer.setVisibility(View.VISIBLE);
                findViewById(R.id.verifyDialogButtonsErrorContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.verifyDialogErrorOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
            }
        });
    }

    private void setupWindowParameters() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_verify);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    private void verifyRegistrationInformation() {
        if(attendee.getAge() < 18) {
            issueMessage.setText(R.string.dialog_verify_underage);
            errorCount++;
        }

        if(!attendee.isWaiverSigned()) {
            issueMessage.setText(R.string.dialog_verify_no_waiver);
            errorCount++;
        }

        if(!attendee.isCheckedIn()) {
            issueMessage.setText(R.string.dialog_verify_checked_in);
            errorCount++;
        }

        if(errorCount > 0) {
            if(errorCount > 1) {
                issueMessage.setText(R.string.dialog_verify_multiple_issues);
            }

            titleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
            successContainer.setVisibility(View.GONE);
            issueContainer.setVisibility(View.VISIBLE);
        } else {
            successContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setupTextViews() {
        textViewName.setText(attendee.getName());
        textViewEmail.setText(attendee.getEmail());
        textViewAge.setText(Integer.toString(attendee.getAge()));
        textViewWaiver.setText(attendee.isWaiverSigned() ? R.string.dialog_verify_waiver_status_true : R.string.dialog_verify_waiver_status_false);
        textViewCheckedIn.setText(attendee.isCheckedIn() ? R.string.dialog_verify_check_in_status_true : R.string.dialog_verify_check_in_status_true);
    }
}
