package com.hacktx.electron.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hacktx.electron.R;

public class PreferencesUtils {

    /**
     * Quickly get default <code>SharedPreferences</code> for a given <code>Context</code>.
     *
     * @param context Context by which to get default <code>SharedPreferences</code>
     * @return SharedPreferences instance
     */
    public static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get if the application has never run before.
     *
     * @param context Context by which to retrieve data
     * @return <code>boolean</code> representing if the application has never run before
     */
    public static boolean getFirstLaunch(Context context) {
        return getPrefs(context).getBoolean(context.getString(R.string.prefs_first_launch), true);
    }

    /**
     * Set if the application has never run before.
     *
     * @param context Context by which to retrieve data
     * @param firstLaunch If the application has never run before
     */
    public static void setFirstLaunch(Context context, boolean firstLaunch) {
        getPrefs(context).edit().putBoolean(context.getString(R.string.prefs_first_launch), firstLaunch).apply();
    }

    /**
     * Get the stored volunteer email.
     *
     * @param context Context by which to retrieve data
     * @return <code>String</code> representing the volunteer email
     */
    public static String getVolunteerEmail(Context context) {
        return getPrefs(context).getString(context.getString(R.string.prefs_volunteer_email), "");
    }

    /**
     * Set the volunteer email.
     *
     * @param context Context by which to retrieve data
     * @param volunteerEmail Volunteer email to store
     */
    public static void setVolunteerEmail(Context context, String volunteerEmail) {
        getPrefs(context).edit().putString(context.getString(R.string.prefs_volunteer_email), volunteerEmail).apply();
    }

}
