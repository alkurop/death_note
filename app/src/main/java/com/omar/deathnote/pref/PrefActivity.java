package com.omar.deathnote.pref;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class PrefActivity extends PreferenceActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new AboutPrefFragment())
				.commit();
//		getActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			finish();

			break;
		}
		return false;
	}

}
