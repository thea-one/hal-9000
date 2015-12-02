package one.thea.nightynight;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by JA on 2015-11-29.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.layout.fragment_settings);
    }
}
