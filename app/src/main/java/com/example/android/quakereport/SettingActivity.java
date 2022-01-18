package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SettingActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

    }

    /**
     * 对于我们的用户而言，通过单击 最小震级偏好来查看其当前设置 并不是一种美妙的体验。
     * 更好的方法是打开 Setting Activity 即可 查看偏好名称下方的偏好值， 如果对其进行更改，可以看到摘要将立即更新。
     * 这对于偏好更改后应用能够立即知晓非常有用， 特别是偏好更改应对 UI 产生某些可见影响。
     * <p>
     * 要实现该功能，当偏好发生更改时，PreferenceFragment 可以实现 OnPreferenceChangeListener 接口以获取通知。
     * 然后，当用户更改单个偏好 并进行保存时，将使用已更改偏好的关键字来调用 onPreferenceChange() 方法。
     * 请注意，此方法 将返回布尔值，可防止通过返回 false 来 更改建议的偏好设置。
     */
    public static class EarthquakePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_main);

            /**
             * 在启动设置活动后， 仍需要更新偏好摘要。
             * 如果给定偏好的键，可以 使用 PreferenceFragment 的 findPreference() 方法来获取偏好 对象，
             * 然后使用 bindPreferenceSummaryToValue() 帮助程序方法来设置偏好。
             */
            Preference minMag = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMag);

            Preference orderby = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderby);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            Log.i(LOG_TAG, "TEST: bindPreferenceSummaryToValue() called. " + "Preference: " + preference);

            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            String preferenceString = preferences.getString(preference.getKey(), "");

            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            Log.i(LOG_TAG, "TEST: onPreferenceChange() called");

            String stringValue = value.toString();
            if(preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int preIndex = listPreference.findIndexOfValue(stringValue);
                if (preIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[preIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}
