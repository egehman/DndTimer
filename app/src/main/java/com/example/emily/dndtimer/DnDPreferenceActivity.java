package com.example.emily.dndtimer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DnDPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
