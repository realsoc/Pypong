package com.realsoc.pipong;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;

import com.realsoc.pipong.utils.DataUtils;
import com.realsoc.pipong.utils.NetworkUtils;

/**
 * Created by Hugo on 18/01/2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = "SettingsFragment";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences defaultSharedPref;
    private SharedPreferences appSharedPref;
    private DataUtils dataUtils;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
        dataUtils = DataUtils.getInstance(getActivity());
        defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        appSharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                //IP PREFERENCE CHANGED
                if(getActivity()!=null) {
                    if (key.equals(getActivity().getString(R.string.SERVER_IP))) {
                        String newIp = prefs.getString(key, "0.0.0.0");
                        if (!Patterns.IP_ADDRESS.matcher(newIp).matches() && !newIp.matches(NetworkUtils.ADDRESS_REGEX)) {
                            //Toast.makeText(getActivity(), getString(R.string.ip_not_valid), Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putString(key, appSharedPref.getString(key, "0.0.0.0")).apply();
                        } else {
                            String oldIP = appSharedPref.getString(getActivity().getString(R.string.SERVER_IP), "");
                            if (!oldIP.equals(newIp) && !oldIP.equals("")) {
                                unSubscribe(appSharedPref);
                                appSharedPref.edit().putBoolean(getActivity().getString(R.string.UNSUBSCRIBE), true)
                                        .putString(getActivity().getString(R.string.OLD_SERVER_IP), oldIP)
                                        .putBoolean(getActivity().getString(R.string.HAS_SUBSCRIBED), false)
                                        .putLong(getActivity().getString(R.string.TIME_START), 0)
                                        .apply();
                            }
                            //Toast.makeText(getActivity(), getString(R.string.ip_valid), Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor edit = appSharedPref.edit();
                            edit.putString(key, newIp).
                                    putBoolean(getActivity().getString(R.string.IP_SET), true).apply();
                        }
                    } else if (key.equals(getActivity().getString(R.string.IS_ONLINE))) {
                        appSharedPref.edit().putBoolean(key, prefs.getBoolean(key, false)).apply();
                    }
                }else{
                    Log.d(LOG_TAG," ACTIVITY DOES NOT EXIST IN SETTINGS FRAG");
                }
            }
        };
        Preference ip = findPreference(getActivity().getString(R.string.SERVER_IP));
        ip.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(appSharedPref.getBoolean(getActivity().getString(R.string.IP_SET),false))
                    ((EditTextPreference)preference).setText("");
                return true;
            }
        });
        defaultSharedPref.registerOnSharedPreferenceChangeListener(listener);
    }

    //RESET ALL STATE
    //TODO : DEBUG ONLY,
    private void reset() {
        if(getActivity()!=null){
            defaultSharedPref.edit().putString(getString(R.string.SERVER_IP),"0.0.0.0")
                    .putBoolean(getString(R.string.IS_ONLINE),false).apply();
            appSharedPref.edit().putBoolean(getString(R.string.HAS_SUBSCRIBED),false)
                    .putBoolean(getString(R.string.IS_ONLINE),false)
                    .putString(getString(R.string.SERVER_IP),"0.0.0.0")
                    .putString(getString(R.string.HASH),"")
                    .putBoolean(getString(R.string.FIRST_LAUNCHED),true)
                    .putLong(getString(R.string.LAST_UPDATE),0).apply();
            dataUtils.dropCounts();
            dataUtils.dropPlayers();
            dataUtils.dropGames();
            DataUtils.reset(getActivity());
        }
    }
    private void unSubscribe(SharedPreferences appSharedPref){
        if(getActivity() != null){
            String oldIp = appSharedPref.getString(getActivity().getString(R.string.SERVER_IP),"");
            DataUtils dataUtils = DataUtils.getInstance(getActivity());
            boolean tempOnline = appSharedPref.getBoolean(getActivity().getString(R.string.IS_ONLINE),false);
            appSharedPref.edit().putBoolean(getActivity().getString(R.string.IS_ONLINE),false).commit();
            appSharedPref.edit().putBoolean(getActivity().getString(R.string.UNSUBSCRIBE),true)
                    .putString(getActivity().getString(R.string.OLD_SERVER_IP),oldIp)
                    .putBoolean(getActivity().getString(R.string.HAS_SUBSCRIBED),false)
                    .putLong(getActivity().getString(R.string.LAST_UPDATE),0)
                    .apply();
            dataUtils.turnEverythingOffline();
            appSharedPref.edit().putBoolean(getActivity().getString(R.string.IS_ONLINE),tempOnline).apply();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference exercisesPref = findPreference(key);
        if(key.equals(getString(R.string.SERVER_IP)))
            exercisesPref.setSummary(sharedPreferences.getString(key, ""));
    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

}
