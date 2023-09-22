package com.minbar.tafhimulquran.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.minbar.tafhimulquran.R;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.main_toolbar_setting);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        SharedPreferences f159sp;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Load_setting();
        }
        private void Load_setting() {


            this.f159sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());


            ListPreference bayaanList = (ListPreference) findPreference("bayaan");
            String bstring = this.f159sp.getString("bayaan", (String) null);
            if ("on".equals(bstring)) {
                bayaanList.setSummary(bayaanList.getEntry());
            } else if ("off".equals(bstring)) {
                bayaanList.setSummary(bayaanList.getEntry());
            }
            bayaanList.setOnPreferenceChangeListener((preference, obj) -> {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                return true;
            });



/*
            ListPreference theme = (ListPreference) findPreference("THEME_PREFERENCE");
            String stringTheme = this.f159sp.getString("THEME_PREFERENCE", (String) null);
            if ("THEME_WHITE_BLUE".equals(stringTheme)) {
                theme.setSummary(theme.getEntry());
            } else if ("THEME_DARK_ORANGE".equals(stringTheme)) {
                assert theme != null;
                theme.setSummary(theme.getEntry());
            } else if ("THEME_DARK_TURQUOISE".equals(stringTheme)) {
                theme.setSummary(theme.getEntry());
            }


            theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


 */


            /*
            ((CheckBoxPreference) findPreference("NIGHT")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    if (((Boolean) obj).booleanValue()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                }
            });

             */




            ListPreference listPreference = (ListPreference) findPreference("ARABICFONT");
            String string = this.f159sp.getString("ARABICFONT", (String) null);
            if ("12".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("14".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("16".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("18".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("20".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("22".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("24".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("26".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("28".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("30".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("32".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            } else if ("34".equals(string)) {
                listPreference.setSummary(listPreference.getEntry());
            }
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


            ListPreference listPreference2 = (ListPreference) findPreference("ProFONT");
            String string2 = this.f159sp.getString("ProFONT", (String) null);
            if ("12".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("14".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("16".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("18".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("20".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("22".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("24".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("26".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("28".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            } else if ("30".equals(string2)) {
                listPreference2.setSummary(listPreference2.getEntry());
            }
            listPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


            /*
            ListPreference listPreference3 = (ListPreference) findPreference("TransFONT");
            String string3 = this.f159sp.getString("TransFONT", (String) null);
            if ("12".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("14".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("16".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("18".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("20".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("22".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("24".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("26".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("28".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            } else if ("30".equals(string3)) {
                listPreference3.setSummary(listPreference3.getEntry());
            }
            listPreference3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });

             */
            ListPreference listPreference4 = (ListPreference) findPreference("font_arabi");
            String string4 = this.f159sp.getString("font_arabi", (String) null);
            if ("noorehuda".equals(string4)) {
                listPreference4.setSummary(listPreference4.getEntry());
            } else if ("noorehira".equals(string4)) {
                listPreference4.setSummary(listPreference4.getEntry());
            } else if ("me_quran".equals(string4)) {
                listPreference4.setSummary(listPreference4.getEntry());
            } else if ("scheherazade".equals(string4)) {
                listPreference4.setSummary(listPreference4.getEntry());
            }
            listPreference4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });
            ListPreference listPreference5 = (ListPreference) findPreference("font_bangla");
            String string5 = this.f159sp.getString("font_bangla", (String) null);
            if ("lipi".equals(string5)) {
                listPreference5.setSummary(listPreference5.getEntry());
            } else if ("charu".equals(string5)) {
                listPreference5.setSummary(listPreference5.getEntry());
            } else if ("kal".equals(string5)) {
                listPreference5.setSummary(listPreference5.getEntry());
            }
            listPreference5.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });

            /*
            ListPreference listPreference6 = (ListPreference) findPreference("AnotherFONT");
            String string6 = this.f159sp.getString("AnotherFONT", (String) null);
            if ("12".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("14".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("16".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("18".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("20".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("22".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("24".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("26".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("28".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            } else if ("30".equals(string6)) {
                listPreference6.setSummary(listPreference6.getEntry());
            }
            listPreference6.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


             */



            ListPreference listPreference7 = (ListPreference) findPreference("tafheem");
            String string7 = this.f159sp.getString("tafheem", (String) null);
            if ("on".equals(string7)) {
                listPreference7.setSummary(listPreference7.getEntry());
            } else if ("off".equals(string7)) {
                listPreference7.setSummary(listPreference7.getEntry());
            }
            listPreference7.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });


            ListPreference listPreference8 = (ListPreference) findPreference("taisirul");
            String string8 = this.f159sp.getString("taisirul", (String) null);
            if ("on".equals(string8)) {
                listPreference8.setSummary(listPreference8.getEntry());
            } else if ("off".equals(string8)) {
                listPreference8.setSummary(listPreference8.getEntry());
            }
            listPreference8.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });



            ListPreference listtika = (ListPreference) findPreference("tika");
            String stringlisttika = this.f159sp.getString("tika", (String) null);
            if ("on".equals(stringlisttika)) {
                listtika.setSummary(listtika.getEntry());
            } else if ("off".equals(stringlisttika)) {
                listtika.setSummary(listtika.getEntry());
            }
            listtika.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });





            ListPreference audioScolling = (ListPreference) findPreference("audioHide");
            String stringaudio = this.f159sp.getString("audioHide", (String) null);
            if ("on".equals(stringaudio)) {
                audioScolling.setSummary(audioScolling.getEntry());
            } else if ("off".equals(stringaudio)) {
                audioScolling.setSummary(audioScolling.getEntry());
            }
            audioScolling.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });



            ListPreference scollingHide = (ListPreference) findPreference("scollingHide");
            String stringscolling = this.f159sp.getString("scollingHide", (String) null);
            if ("on".equals(stringscolling)) {
                scollingHide.setSummary(scollingHide.getEntry());
            } else if ("off".equals(stringscolling)) {
                scollingHide.setSummary(scollingHide.getEntry());
            }
            scollingHide.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });






            ListPreference banglaOnubadhList = (ListPreference) findPreference("banglaOnubadh");
            String strinBangla = this.f159sp.getString("banglaOnubadh", (String) null);
            if ("on".equals(strinBangla)) {
                banglaOnubadhList.setSummary(banglaOnubadhList.getEntry());
            } else if ("off".equals(strinBangla)) {
                banglaOnubadhList.setSummary(banglaOnubadhList.getEntry());
            }
            banglaOnubadhList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });





            /*
            ListPreference listTajwid = (ListPreference) findPreference("Tajwid");
            String stringTajwid = this.f159sp.getString("Tajwid", (String) null);
            if ("on".equals(stringTajwid)) {
                listTajwid.setSummary(listTajwid.getEntry());
            } else if ("off".equals(stringTajwid)) {
                listTajwid.setSummary(listTajwid.getEntry());
            }
            listTajwid.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue((String) obj)]);
                    return true;
                }
            });




             */



        }




        public void onResume() {
            Load_setting();
            super.onResume();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}