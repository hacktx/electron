package com.hacktx.electron.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.hacktx.electron.R;
import com.hacktx.electron.activities.WelcomeActivity;
import com.hacktx.electron.utils.PreferencesUtils;

public class PreferencesFragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        final PreferenceScreen logout = (PreferenceScreen) findPreference(getString(R.string.prefs_account_logout));
        logout.setSummary(getString(R.string.fragment_preferences_volunteer_id, PreferencesUtils.getVolunteerId(getActivity())));
        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PreferencesUtils.setVolunteerId(getActivity(), "");
                PreferencesUtils.setFirstLaunch(getActivity(), true);
                getActivity().startActivity(new Intent(getActivity(), WelcomeActivity.class));
                getActivity().finish();
                return true;
            }
        });

        final PreferenceScreen about = (PreferenceScreen) findPreference(getString(R.string.prefs_about));
        String version;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            version = "???";
        }
        about.setSummary(getString(R.string.fragment_preferences_about_version, version));

        final PreferenceScreen licenses = (PreferenceScreen) findPreference(getString(R.string.prefs_licenses));
        licenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Dialog licenseDialog = new Dialog(getActivity());
                licenseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                licenseDialog.setContentView(R.layout.dialog_licenses);
                WindowManager.LayoutParams licenseParams = licenseDialog.getWindow().getAttributes();
                licenseParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                licenseDialog.getWindow().setAttributes(licenseParams);
                licenseDialog.show();

                WebView licenseWebView = (WebView) licenseDialog.findViewById(R.id.licenseWebView);
                licenseWebView.loadUrl("file:///android_asset/open_source_licenses.html");
                return true;
            }
        });
    }
}