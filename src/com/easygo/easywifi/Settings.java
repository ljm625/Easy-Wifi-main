package com.easygo.easywifi;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by TrixZ on 2014/12/13.
 */
public class Settings extends PreferenceFragment {
    CheckBoxPreference auto_login;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载第一个布局
        addPreferencesFromResource(R.xml.settings);

    }
}