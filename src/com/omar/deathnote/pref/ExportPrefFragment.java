package com.omar.deathnote.pref;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.omar.deathnote.R;

public class ExportPrefFragment extends PreferenceFragment {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
 
    addPreferencesFromResource(R.xml.export_pref);

  }

}