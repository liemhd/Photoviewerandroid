package duyliem.photoviewer.NavigationView;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import duyliem.photoviewer.R;

public class Settings extends AppCompatActivity {

    ImageView imvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        imvBack = findViewById(R.id.imvBack);

        imvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.settings, new MySettingsFragment()).commit();

    }


    public static class MySettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor editor= sp.edit();


//            bindSummaryValue(findPreference("key_display"));

            ListPreference preference = (ListPreference) findPreference("key_display");
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String stringValue = newValue.toString();
                    Intent intent = new Intent();
                    intent.putExtra("display", stringValue);
//                    editor.commit();
                    intent.setAction("detect_display");
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    Toast.makeText(getActivity(), stringValue, Toast.LENGTH_SHORT).show();

                    if (preference instanceof ListPreference) {
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);
                        preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                    }
                    return true;
                }
            });
        }

        private static void bindSummaryValue(Preference preference) {
            preference.setOnPreferenceChangeListener(listener);
            listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                    .getString(preference.getKey(), ""));


        }

        private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
//                Intent intent = new Intent();
//                intent.putExtra("display", stringValue);
//                intent.setAction("detect_display");
//                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
//                Log.d("ooo", stringValue);

                if (preference instanceof ListPreference) {
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);
                    preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                }

                return true;
            }
        };

    }

}


