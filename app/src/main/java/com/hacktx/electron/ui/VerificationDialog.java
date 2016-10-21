package com.hacktx.electron.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hacktx.electron.R;
import com.hacktx.electron.model.Attendee;
import com.hacktx.electron.model.ServerError;
import com.hacktx.electron.network.ElectronClient;
import com.hacktx.electron.network.ElectronService;
import com.hacktx.electron.utils.PreferencesUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VerificationDialog extends Dialog {

    private String email;
    private Attendee attendee;

    private LinearLayout titleContainer, successContainer, issueContainer;
    private TextView dialogTitle, issueMessage, textViewName, textViewSchool, textViewEmail, textViewAge, textViewCheckedIn, textViewConfirmed;
    private Button checkInButton;

    public VerificationDialog(String email, Context context) {
        super(context);

        setupWindowParameters();

        this.email = email;

        titleContainer = (LinearLayout) findViewById(R.id.dialogTitleContainer);
        successContainer = (LinearLayout) findViewById(R.id.verifyDialogSuccessContainer);
        issueContainer = (LinearLayout) findViewById(R.id.verifyDialogIssueContainer);

        dialogTitle = (TextView) findViewById(R.id.dialogTitle);
        issueMessage = (TextView) findViewById(R.id.verifyDialogIssueText);
        textViewName = (TextView) findViewById(R.id.verifyDialogFullName);
        textViewSchool = (TextView) findViewById(R.id.verifyDialogSchool);
        textViewEmail = (TextView) findViewById(R.id.verifyDialogEmail);
        textViewAge = (TextView) findViewById(R.id.verifyDialogAge);
        textViewCheckedIn = (TextView) findViewById(R.id.verifyDialogCheckedIn);
        textViewConfirmed = (TextView) findViewById(R.id.verifyDialogConfirmed);

        checkInButton = (Button) findViewById(R.id.verifyDialogCheckIn);
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
        electronService.getRegistrationData(PreferencesUtils.getVolunteerEmail(getContext()), email, new Callback<Attendee>() {
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
                ServerError nucleusError = (ServerError) error.getBodyAs(ServerError.class);
                findViewById(R.id.verifyDialogProgressBarContainer).setVisibility(View.GONE);

                titleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
                dialogTitle.setText(R.string.dialog_verify_error);
                if(nucleusError != null) {
                    issueMessage.setText(nucleusError.getError());
                } else {
                    issueMessage.setText(R.string.dialog_verify_error_connection);
                }
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

    /**
     * Setup dialog size and other <code>LayoutParams</code>.
     */
    private void setupWindowParameters() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_verify);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    /**
     * Verify attendee information and highlight issues as needed.
     */
    private void verifyRegistrationInformation() {
        int issueCount = 0;
        boolean blocked = false;

        if(attendee.getAge() < 18) {
            issueMessage.setText(R.string.dialog_verify_underage);
            textViewSchool.setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
            textViewAge.setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
            issueCount++;
        }

        if(!attendee.isConfirmed()) {
            issueMessage.setText(R.string.dialog_verify_not_confirmed);
            textViewConfirmed.setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
            issueCount++;
            blocked = true;
        }

        if(attendee.isCheckedIn()) {
            issueMessage.setText(R.string.dialog_verify_checked_in);
            textViewCheckedIn.setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
            issueCount++;
            blocked = true;
        }

        if(issueCount > 0) {
            if(issueCount > 1) {
                issueMessage.setText(R.string.dialog_verify_multiple_issues);
            }

            titleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
            successContainer.setVisibility(View.GONE);
            issueContainer.setVisibility(View.VISIBLE);
            checkInButton.setEnabled(!blocked);
        } else {
            successContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Fill each dialog <code>TextView</code> with attendee information.
     */
    private void setupTextViews() {
        textViewName.setText(attendee.getName());
        textViewSchool.setText(attendee.getSchool());
        textViewEmail.setText(attendee.getEmail());
        textViewAge.setText(Integer.toString(attendee.getAge()));
        textViewCheckedIn.setText(attendee.isCheckedIn() ? R.string.dialog_verify_check_in_status_true : R.string.dialog_verify_check_in_status_false);
        textViewConfirmed.setText(attendee.isConfirmed() ? R.string.dialog_verify_confirmed_status_true : R.string.dialog_verify_confirmed_status_false);
    }
}
