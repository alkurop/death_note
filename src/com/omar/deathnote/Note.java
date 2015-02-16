package com.omar.deathnote;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.omar.deathnote.dialogs.AddAudioDialog;
import com.omar.deathnote.dialogs.AddPicDialog;
import com.omar.deathnote.dialogs.DialogOnDelete;
import com.omar.deathnote.dialogs.DialogOnDelete.DeleteDialog;
import com.omar.deathnote.fragments.AudioFragment;
import com.omar.deathnote.fragments.AudioFragment.NextAudio;
import com.omar.deathnote.fragments.DefaultFragment;
import com.omar.deathnote.fragments.LinkFragment;
import com.omar.deathnote.fragments.NoteFragment;
import com.omar.deathnote.fragments.PicFragment;
import com.omar.deathnote.utility.OnDeleteFragment;
import com.omar.deathnote.utility.SaveNote;

public class Note extends Activity implements SaveNote, NextAudio,
		OnNavigationListener, OnDeleteFragment, AddPicDialog.PicDialogListener,
		AddAudioDialog.AudioDialogListener, LoaderCallbacks<Cursor>,
		DeleteDialog {

	static class MyCursorLoader extends CursorLoader {

		DB db;

		public MyCursorLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;

			db.open();

			cursor = db.getAllNoteData(id);

			return cursor;
		}

	}
	/* private static final int RESULT_LOAD_IMAGE = 33; */
	static long id;
	int style;
	int img;
	String timedate;
	DB db;
	// < --- SELECT --- >
	SimpleAdapter selectAdapter;
	ArrayList<Map<String, Object>> dataSelect;
	Map<String, Object> m;
	Select sel;
	LinearLayout noteList;
	int fragCount;
	String fragId;
	FileManager sc;
	Fragment TempFragment;
	InputMethodManager imm;
	// </ --- SELECT --- >
	public static final String BROADCAST_DESTROY = "com.omar.deathnote.fragments.audiofragment.destroy";

	Intent destroy = new Intent(BROADCAST_DESTROY);
	// <Note Elements>

	FragmentTransaction fTrans;
	TreeMap<String, String> fragList;

	TreeMap<String, String> Temp;

	LinearLayout mainLayout;
	// </ note elements>
	int result;
	String xml;

	Cursor cursor;

	String fragToDel;
	boolean audioShuffle;

	boolean audioRepeat;

	public void closeStopAllAudio() {

		for (Map.Entry<String, String> entry : fragList.entrySet()) {

			String fragId = entry.getKey();
			String type = entry.getValue();

			switch (type) {

			case "AudioFragment":

				TempFragment = (AudioFragment) getFragmentManager()
						.findFragmentByTag(fragId);
				if (((AudioFragment) TempFragment).musicPlaying) {
					((AudioFragment) TempFragment).stopAudio();
				} else if (((AudioFragment) TempFragment).recording) {
					((AudioFragment) TempFragment).recStop();
					/* Log.d("rec===>","stop"); */
				}

				break;

			}

		}
	}

	public void createFragment(String cont1, String cont2, String type) {

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		fTrans = getFragmentManager().beginTransaction();
	 

		fragId = Integer.toString(fragCount);
		fragCount++;
		Temp = new TreeMap<String, String>();
		if (cont1 != null)
			Temp.put("cont1", cont1);
		if (cont2 != null)
			Temp.put("cont2", cont2);

		switch (type) {

		case "DefaultFragment":
			TempFragment = new DefaultFragment();
			fTrans.add(R.id.noteList, ((DefaultFragment) TempFragment), fragId);
			fragList.put(fragId, "DefaultFragment");

			if (Temp != null)
				((DefaultFragment) TempFragment).loadContent(Temp);
			((DefaultFragment) TempFragment).loadFragId(fragId);

			break;

		case "NoteFragment":
			TempFragment = new NoteFragment();
			fTrans.add(R.id.noteList, ((NoteFragment) TempFragment), fragId);
			fragList.put(fragId, "NoteFragment");

			if (Temp != null)
				((NoteFragment) TempFragment).loadContent(Temp);
			((NoteFragment) TempFragment).loadFragId(fragId);

			break;
		case "LinkFragment":
			TempFragment = new LinkFragment();
			fTrans.add(R.id.noteList, ((LinkFragment) TempFragment), fragId);
			fragList.put(fragId, "LinkFragment");

			if (Temp != null)
				((LinkFragment) TempFragment).loadContent(Temp);
			((LinkFragment) TempFragment).loadFragId(fragId);

			break;
		case "PicFragment":
			TempFragment = new PicFragment();
			fTrans.add(R.id.noteList, ((PicFragment) TempFragment), fragId);
			fragList.put(fragId, "PicFragment");

			if (Temp != null)
				((PicFragment) TempFragment).loadContent(Temp);
			((PicFragment) TempFragment).loadFragId(fragId);
			((PicFragment) TempFragment).loadNoteId(String.valueOf(id));
			break;
		case "AudioFragment":

			TempFragment = new AudioFragment();
			fTrans.add(R.id.noteList, ((AudioFragment) TempFragment), fragId);
			fragList.put(fragId, "AudioFragment");

			if (Temp != null)
				((AudioFragment) TempFragment).loadContent(Temp);
			((AudioFragment) TempFragment).loadFragId(fragId);
			/*
			 * ((AudioFragment) TempFragment).loadShuffleRepeat(audioShuffle,
			 * audioShuffle);
			 */
			((AudioFragment) TempFragment).loadNoteId(String.valueOf(id));
			break;
		}

		fTrans.commit();
/*
		Log.d(type, fragId);*/

	}

	@Override
	public void del(String dialogId, String s) {
		switch (dialogId) {
		case "delItem":
			switch (s) {
			case "Yes":
				takiDelete(fragToDel);
			}
			break;
		}

	}

	@Override
	public void delete(String s, boolean dialog) {
		fragToDel = s;
		if (dialog) {
			DialogFragment delDialog = new DialogOnDelete();

			delDialog.show(getFragmentManager(), "dialog");
		} else {
			takiDelete(fragToDel);
		}
	}

	@Override
	public void finish() {
		/* sendBroadcast(destroy); */
		Intent destroyMusic = new Intent(AudioFragment.BROADCAST_PAUSESONG);
		destroyMusic.putExtra("flag", "destroy");
		sendBroadcast(destroyMusic);

		Intent intent = new Intent();
		intent.putExtras(save());
		setResult(result, intent);

		super.finish();
	}

	public void initShareIntent() {
		Log.d("sharing", "sharing");
		String subject = null;
		String text = null;
		StringBuilder stringBuilder = new StringBuilder();
		ArrayList<Uri> uris = new ArrayList<Uri>();
		ArrayList<Uri> urisSpecial = new ArrayList<Uri>();
		db = new DB(this);
		db.open();

		cursor = db.getAllNoteData(id);
		if (cursor != null) {

			while (cursor.moveToNext()) {
				Log.d("sharing", "fetchin from db");
				String type = cursor.getString(cursor
						.getColumnIndex(db.COLUMN_TYPE));
				File fileIn;
				Uri u;
				switch (type) {
				case "DefaultFragment":
					subject = cursor.getString(cursor
							.getColumnIndex(db.COLUMN_CONT1));

					break;

				case "NoteFragment":

					stringBuilder.append("\n"
							+ "\n"
							+ cursor.getString(cursor
									.getColumnIndex(db.COLUMN_CONT1)));

					break;
				case "LinkFragment":

					stringBuilder.append("http://"
							+ cursor.getString(cursor
									.getColumnIndex(db.COLUMN_CONT1)) + "\n"
							+ "\n");

					break;
				case "PicFragment":
					fileIn = new File(cursor.getString(cursor
							.getColumnIndex(db.COLUMN_CONT1)));
					fileIn.setReadable(true, false);
					u = Uri.fromFile(fileIn);
					uris.add(u);

					break;

				case "AudioFragment":
					fileIn = new File(cursor.getString(cursor
							.getColumnIndex(db.COLUMN_CONT1)));
					fileIn.setReadable(true, false);
					u = Uri.fromFile(fileIn);
					urisSpecial.add(u);
					break;

				}

			}

		}
		text = stringBuilder.toString();
		cursor.close();
		Log.d("sharing", "closing db");
		db.close();

		Resources resources = getResources();

		Intent emailIntent = new Intent();
		emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putParcelableArrayListExtra(
				android.content.Intent.EXTRA_STREAM, uris);
		emailIntent.setType("message/rfc822");

		PackageManager pm = getPackageManager();
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setType("*/*");

		Intent openInChooser = Intent.createChooser(emailIntent, "Select");

		List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);

		List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
		String packageName = null;
		ResolveInfo ri;

		for (int i = 0; i < resInfo.size(); i++) {

			// Extract the label, append it, and repackage it in a LabeledIntent
			ri = resInfo.get(i);
			Log.d("share type ---->>>", ri.activityInfo.packageName);
			packageName = ri.activityInfo.packageName;
			if (packageName.contains("android.email")) {
				emailIntent.setPackage(packageName);
			} else if (packageName.contains("twitter")
					|| packageName.contains("facebook.orca")
					/* || packageName.contains("facebook.katana") */
					|| packageName.contains("skype")
					|| packageName.contains("instagram")
					|| packageName.contains("viber")
					|| packageName.contains("dropbox")
					|| packageName.contains("android.gm")) {

				Intent intent = new Intent();
				intent.setComponent(new ComponentName(packageName,
						ri.activityInfo.name));
				intent.setAction(Intent.ACTION_SEND_MULTIPLE);
				intent.setType("text/plain");
				if (packageName.contains("android.gm")) {
					intent.putExtra(Intent.EXTRA_TEXT, text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					uris.addAll(urisSpecial);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, uris);
					intent.setType("message/rfc822");

				}
				if (packageName.contains("instagram")) {
					intent.setAction(Intent.ACTION_SEND);
					if (uris.size() > 0) {
						intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
					}
					intent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					intent.setType("image/*");

				}
				if (packageName.contains("dropbox")) {
					intent.putExtra(Intent.EXTRA_TEXT, text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					uris.addAll(urisSpecial);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, uris);
					intent.setType("message/rfc822");

				}

				if (packageName.contains("skype")) {
					intent.setAction(Intent.ACTION_SEND_MULTIPLE);
					intent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					uris.addAll(urisSpecial);
					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, uris);

					intent.setType("message/rfc822");

				}

				if (packageName.contains("viber")) {
					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					intent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + text);
					uris.addAll(urisSpecial);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, uris);
					intent.setType("message/rfc822");

				}
				if (packageName.contains("facebook.orca")) {
					intent.putExtra(Intent.EXTRA_TEXT, subject + text + "\n");

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					uris.addAll(urisSpecial);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, uris);
					intent.setType("text/pain");

				}
				if (packageName.contains("facebook.katana")) {
					intent.putExtra(Intent.EXTRA_TEXT, subject + text + "\n");

					intent.setAction(Intent.ACTION_SEND_MULTIPLE);
					/*
					 * intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					 * uris.addAll(urisSpecial);
					 * 
					 * intent.putParcelableArrayListExtra(
					 * android.content.Intent.EXTRA_STREAM, uris);
					 */
					intent.setType("text/pain");
					return;

				}
				if (packageName.contains("twitter")) {
					intent.setAction(Intent.ACTION_SEND);
					if (uris.size() > 0) {
						intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
					}
					intent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + text);
					intent.putExtra(Intent.EXTRA_SUBJECT, subject);

					intent.setType("text/pain");

				}
				intentList.add(new LabeledIntent(intent, packageName, ri
						.loadLabel(pm), ri.icon));

			}
		}

		// convert intentList to array
		LabeledIntent[] extraIntents = intentList
				.toArray(new LabeledIntent[intentList.size()]);

		openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
		startActivity(openInChooser);
	}

	public void initShareIntent33() {
		Log.d("sharing", "starting");
		String subject = null;
		String text = null;
		StringBuilder stringBuilder = new StringBuilder();
		ArrayList<Uri> uris = new ArrayList<Uri>();
		ArrayList<Uri> urisSpecial = new ArrayList<Uri>();
		db = new DB(this);
		db.open();

		cursor = db.getAllNoteData(id);
		if (cursor != null) {

			while (cursor.moveToNext()) {
				Log.d("sharing", "fetchin from db");
				String type = cursor.getString(cursor
						.getColumnIndex(db.COLUMN_TYPE));
				File fileIn;
				Uri u;
				switch (type) {
				case "DefaultFragment":
					subject = cursor.getString(cursor
							.getColumnIndex(db.COLUMN_CONT1));

					break;

				case "NoteFragment":

					stringBuilder.append("\n"
							+ "\n"
							+ cursor.getString(cursor
									.getColumnIndex(db.COLUMN_CONT1)));

					break;
				case "LinkFragment":

					stringBuilder.append("http://"
							+ cursor.getString(cursor
									.getColumnIndex(db.COLUMN_CONT1)) + "\n"
							+ "\n");

					break;
				case "PicFragment":
					fileIn = new File(cursor.getString(cursor
							.getColumnIndex(db.COLUMN_CONT1)));
					fileIn.setReadable(true, false);
					u = Uri.fromFile(fileIn);
					uris.add(u);

					break;

				case "AudioFragment":
					fileIn = new File(cursor.getString(cursor
							.getColumnIndex(db.COLUMN_CONT1)));
					fileIn.setReadable(true, false);
					u = Uri.fromFile(fileIn);
					urisSpecial.add(u);
					break;

				}

			}

		}
		text = stringBuilder.toString();
		cursor.close();
		Log.d("sharing", "closing db");
		db.close();

		Resources resources = getResources();

		Intent emailIntent = new Intent();
		emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putParcelableArrayListExtra(
				android.content.Intent.EXTRA_STREAM, uris);
		emailIntent.setType("message/rfc822");

		PackageManager pm = getPackageManager();
		Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		sendIntent.setType("text/plain");

		Intent openInChooser = Intent.createChooser(emailIntent, "Select");

		List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);

		List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
		String packageName = null;
		ResolveInfo ri;

		for (int i = 0; i < resInfo.size(); i++) {

			// Extract the label, append it, and repackage it in a LabeledIntent
			ri = resInfo.get(i);
			Log.d("share type ---->>>", ri.activityInfo.packageName);
			packageName = ri.activityInfo.packageName;
			if (packageName.contains("android.email")) {
				emailIntent.setPackage(packageName);
			} else if (packageName.contains("twitter")
					|| packageName.contains("facebook")
					|| packageName.contains("mms")
					|| packageName.contains("android.gm")) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(packageName,
						ri.activityInfo.name));
				intent.setAction(Intent.ACTION_SEND_MULTIPLE);
				intent.setType("text/plain");
				if (packageName.contains("android.gm")) {

					intent.putExtra(Intent.EXTRA_TEXT, text);

					intent.putExtra(Intent.EXTRA_SUBJECT, subject);
					uris.addAll(urisSpecial);

					intent.putParcelableArrayListExtra(
							android.content.Intent.EXTRA_STREAM, uris);
					intent.setType("message/rfc822");

				}

				intentList.add(new LabeledIntent(intent, packageName, ri
						.loadLabel(pm), ri.icon));
			}
		}

		// convert intentList to array
		LabeledIntent[] extraIntents = intentList
				.toArray(new LabeledIntent[intentList.size()]);

		openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
		startActivity(openInChooser);
	}

	public void naviSelect() {
		sel = new Select();
		dataSelect = new ArrayList<Map<String, Object>>();
		for (int i = 1; i < sel.select_images.length; i++) {

			m = new HashMap<String, Object>();
			m.put(sel.ATTRIBUTE_NAME_TEXT,
					getResources().getString(sel.select_names[i]));
			m.put(sel.ATTRIBUTE_NAME_STYLE, sel.select_images[i]);
			dataSelect.add(m);
		}

		String[] fromSel = { sel.ATTRIBUTE_NAME_TEXT, sel.ATTRIBUTE_NAME_STYLE };
		int[] toSel = { R.id.itemName, R.id.itemImg };
		selectAdapter = new SimpleAdapter(this, dataSelect, R.layout.select,
				fromSel, toSel);
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		selectAdapter.setDropDownViewResource(R.layout.select);
		getActionBar().show();
		bar.setListNavigationCallbacks(selectAdapter, this);
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void next(String cur, String flag) {
		/* xSave(); */
		stopAllAudioButCurrent("");
		playNextAudio(cur, flag);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.note);

		result = RESULT_OK;
		super.onCreate(savedInstanceState);
		/* int fragCount = 0; */
		int style = 1;
		fragList = new TreeMap<String, String>();
		Temp = new TreeMap<String, String>();
		String[] keys = null;
		String[] values = null;
		sc = new FileManager();
		db = new DB(this);

		naviSelect();

		LayoutInflater li = getLayoutInflater();
		getActionBar().setDisplayShowTitleEnabled(false);
		li.inflate(R.layout.note, noteList, true);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (savedInstanceState == null) {

			audioShuffle = false;
			audioRepeat = false;
			Bundle extras = getIntent().getExtras();

			if (extras.getLong("id") != 0) {

				id = extras.getLong("id");
				/* Log.d("id", String.valueOf(id)); */
				/*
				 * style = extras.getInt("style"); Log.d("style",
				 * String.valueOf(style)); img = sel.select_images[style];
				 * getActionBar().setSelectedNavigationItem(style - 1);
				 */

				db.open();
				cursor = db.fetchRec(id);
				style = cursor.getInt(cursor.getColumnIndex(db.COLUMN_STYLE));
				getActionBar().setSelectedNavigationItem(style - 1);

				/*
				 * Log.d("db", "close");
				 */

				cursor = db.getAllNoteData(id);
				if (cursor != null) {

					getLoaderManager().initLoader(0, null, this);
					while (cursor.moveToNext()) {

						/*
						 * Log.d("cont1", cursor.getString(cursor
						 * .getColumnIndex(db.COLUMN_CONT1)));
						 */
						createFragment(cursor.getString(cursor
								.getColumnIndex(db.COLUMN_CONT1)),

								cursor.getString(cursor
										.getColumnIndex(db.COLUMN_CONT2)),

								cursor.getString(cursor
										.getColumnIndex(db.COLUMN_TYPE)));

					}
					/*
					 * Log.d("flag====>", "flag 11");
					 */
					cursor.close();
					db.close();
				} else {
					/*
					 * Log.d("flag====>", "flag 12");
					 */
					cursor.close();
					db.close();

					createFragment("", null, "DefaultFragment");
					createFragment(" ", null, "NoteFragment");

				}

			} else {
				/* Log.d("flag====>", "flag 13"); */
				style = extras.getInt("style");
				/* Log.d("style", String.valueOf(style)); */

				/* img = sel.select_images[style]; */
				getActionBar().setSelectedNavigationItem(style - 1);

				db.open();

				db.addRec(style, "");
				cursor = db.fetchLast();

				id = cursor.getInt(cursor.getColumnIndex(DB.COLUMN_ID));

				db.close();
				cursor.close();

				createFragment(extras.getString("title"), null,
						"DefaultFragment");
				createFragment(" ", null, "NoteFragment");

			}

		} else {

			fragCount = savedInstanceState.getInt("fragCount");
			id = savedInstanceState.getLong("id");
			keys = savedInstanceState.getStringArray("fragList_keys");
			values = savedInstanceState.getStringArray("fragList_values");
			style = savedInstanceState.getInt("style");
			/* Log.d("style", String.valueOf(style)); */
			audioShuffle = savedInstanceState.getBoolean("audioShuffle");
			audioRepeat = savedInstanceState.getBoolean("audioRepeat");

			fragList = new TreeMap<String, String>();
			/* Log.d("keys", String.valueOf(keys.length)); */
			/*
			 * Log.d("values", String.valueOf(values.length));
			 */
			for (int k = 0; k < keys.length; k++) {

				fragList.put(keys[k], values[k]);
				/*
				 * Log.d("keys", keys[k]); Log.d("values", values[k]);
				 * 
				 * Log.d("fraglist counter", String.valueOf(k));
				 */
			}
			getActionBar().setSelectedNavigationItem(style - 1);
		}
		mainLayout = (LinearLayout) findViewById(R.id.fNote);

		if (getResources().getConfiguration().orientation == 1) {
			/*Log.d("setting style ===========>>>>> ", String.valueOf(style));*/
			mainLayout.setBackgroundResource(sel.note_bg_images[style - 1]);
		} else {
		/*	Log.d("setting style ===========>>>>> ", String.valueOf(style));*/
			mainLayout.setBackgroundResource(sel.note_bg_images_1[style - 1]);
		}
		/*Log.d("myClass >>>>>>", String.valueOf(this.getClass()));*/
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new MyCursorLoader(this, db);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.note, menu);
		/*
		 * MenuItem item = menu.findItem(R.id.action_share);
		 * mShareActionProvider = (ShareActionProvider)
		 * item.getActionProvider();
		 * mShareActionProvider.setShareIntent(initShareIntent2());
		 */

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onDialogClickAudioBrowse(DialogFragment dialog) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory())));
		} else {
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory())));
		}
		createFragment(null, null, "AudioFragment");
	}

	@Override
	public void onDialogClickAudioRecord(DialogFragment dialog) {
		createFragment("rec", null, "AudioFragment");

	}

	@Override
	public void onDialogClickBrowsePic(DialogFragment dialog) {

		createFragment(null, null, "PicFragment");

	}

	@Override
	public void onDialogClickCameraPic(DialogFragment dialog) {
		createFragment(null, "cam", "PicFragment");
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		db.close();
	}

	@Override
	public boolean onNavigationItemSelected(int pos, long id) {
		style = (pos + 1);
	/*	Log.d("navigaiton item sel ===========>>>>>>>>>", " sel ");*/
		if (getResources().getConfiguration().orientation == 1) {
			mainLayout.setBackgroundResource(sel.note_bg_images[style - 1]);
		} else {
			mainLayout.setBackgroundResource(sel.note_bg_images_1[style - 1]);
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		/*
		 * case R.id.action_settings: break;
		 */

		case R.id.action_share:
			xSave();
			initShareIntent();
			break;

		case R.id.save:

			finish();

			break;

		case android.R.id.home:

			finish();

			break;

		case R.id.btnRepeat:
			/*
			 * Toast.makeText(this, "I was clicked!",
			 * Toast.LENGTH_SHORT).show();
			 */
			break;

		case R.id.addPic:
			xSave();
			AddPicDialog pic = new AddPicDialog();

			pic.show(getFragmentManager(), "NoticeDialogFragment");

			break;

		case R.id.addLink:
			xSave();
			createFragment(null, null, "LinkFragment");

			break;

		case R.id.addText:
			xSave();
			createFragment(null, null, "NoteFragment");

			break;
		case R.id.addAudio:
			xSave();

			AddAudioDialog aud = new AddAudioDialog();

			aud.show(getFragmentManager(), "NoticeDialogFragment");

			break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		/* xSave(); */
		super.onPause();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		outState.putInt("style", style);
		outState.putInt("fragCount", fragCount);
		outState.putLong("id", id);

		outState.putBoolean("audioShuffle", audioShuffle);
		outState.putBoolean("audioRepeat", audioRepeat);

		String[] keys = new String[fragList.size()];
		String[] values = new String[fragList.size()];
		int k = 0;
		for (Map.Entry<String, String> entry : fragList.entrySet()) {

			keys[k] = entry.getKey();
			/* Log.d("entry.getKey()", entry.getKey()); */
			values[k] = entry.getValue();
			/* Log.d("entry.getValue()", entry.getValue()); */
			k++;
		}

		outState.putStringArray("fragList_keys", keys);
		outState.putStringArray("fragList_values", values);

	}

	public void playNextAudio(String cur, String flag) {

		String id = "";
		ArrayList<String> keys = new ArrayList<String>();
		int l = 0;
		int k = 0;
		for (Map.Entry<String, String> entry : fragList.entrySet()) {
			if (entry.getValue() == "AudioFragment") {

				keys.add(entry.getKey());
				/* Log.d("entry.getKey()", entry.getKey()); */
				if (entry.getKey() == cur) {
					l = k;
					/* Log.d("l", String.valueOf(l)); */
				}
				k++;
				/* Log.d("keys", entry.getKey()); */

			}

		}
		/*
		 * Log.d("l", String.valueOf(l)); Log.d("flag", flag);
		 * Log.d("keyLength", String.valueOf(keys.size()));
		 */
		switch (flag) {
		case "next":
			if (keys.size() > l + 1) {

				/* Log.d("keys", "l+1"); */
				id = keys.get(l + 1);
			} else {
				/* Log.d("keys", "l=0"); */
				id = keys.get(0);
			}
			break;
		case "prev":
			if (l != 0) {
				/* Log.d("keys", "l-0"); */
				id = keys.get(l - 1);
			} else {
				/* Log.d("keys", "length-0"); */
				id = keys.get(keys.size() - 1);
			}
			break;
		case "replay":
			id = cur;
			break;
		case "shuffle":
			id = keys.get(MainActivity.randInt(0, keys.size() - 1));
			break;

		}
		TempFragment = (AudioFragment) getFragmentManager().findFragmentByTag(
				id);
		((AudioFragment) TempFragment).playAudio();

	}

	@Override
	public void refreshUi(boolean audioRepeat, boolean audioShuffle,
			boolean paused, int audionumber) {
		/* Log.d("refreshing ui", "refreshing ui"); */

		int k = 0;
		for (Map.Entry<String, String> entry : fragList.entrySet()) {
			if (entry.getValue() == "AudioFragment") {

				TempFragment = (AudioFragment) getFragmentManager()
						.findFragmentByTag(entry.getKey());
				((AudioFragment) TempFragment).refreshStopAudio();
				if (k == audionumber) {
					((AudioFragment) TempFragment).refreshPlayAudio(paused);
					/*
					 * Log.d("refreshing ui =======>", "refreshPlayAudio " +
					 * String.valueOf(k));
					 */
				}
				k++;

			}

		}

	}

	@Override
	public void repeat(boolean repeat) {
		audioRepeat = repeat;
		updateShuffleRepeat();

	}

	public Bundle save() {

		Fragment TempFragment = getFragmentManager().findFragmentByTag("0");
		Temp = ((DefaultFragment) TempFragment).saveContent();

		Bundle bundle = new Bundle();

		bundle.putInt("style", style);
		/* Log.d("bundle id", String.valueOf(style)); */

		xSave();
		return bundle;

	}

	@Override
	public void saveRun() {
		Log.d("Save ==>", "running");
		xSave();
	}

	@Override
	public void shuffle(boolean shuffle) {
		audioShuffle = shuffle;
		updateShuffleRepeat();

	}

	@Override
	public void stopAllAudio(String cur) {
		stopAllAudioButCurrent(cur);
		/* xSave(); */

	}

	public void stopAllAudioButCurrent(String s) {

		for (Map.Entry<String, String> entry : fragList.entrySet()) {

			String fragId = entry.getKey();
			String type = entry.getValue();

			switch (type) {

			case "AudioFragment":
				if (!fragId.equalsIgnoreCase(s)) {
					TempFragment = (AudioFragment) getFragmentManager()
							.findFragmentByTag(fragId);
					((AudioFragment) TempFragment).specialStopAudio();
				}
				break;

			}

		}
	}

	public void takiDelete(String s) { 
		TempFragment = getFragmentManager().findFragmentByTag(s);
		fTrans = getFragmentManager().beginTransaction();
	 
		fTrans.hide(TempFragment);
		try {
			TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fTrans.remove(TempFragment);
		fTrans.commit();

		if (fragList.get(s) == "PicFragment") {
			sc.delImages(s, id);

		}
		TempFragment = null;

		fragList.remove(s);
		xSave();
	}

	public void updateShuffleRepeat() {

		for (Map.Entry<String, String> entry : fragList.entrySet()) {

			String fragId = entry.getKey();
			String type = entry.getValue();

			switch (type) {

			case "AudioFragment":

				TempFragment = (AudioFragment) getFragmentManager()
						.findFragmentByTag(fragId);
				((AudioFragment) TempFragment).loadShuffleRepeat(audioShuffle,
						audioRepeat);

				break;

			}

		}

	}

	public void xSave() {
		db.open();
		db.beginTransaction();
		try {

			db.deleteNoteTable(id);
			db.createNoteTable(id);
			for (Map.Entry<String, String> entry : fragList.entrySet()) {

				String fragId = entry.getKey();
				String type = entry.getValue();
				String cont1 = "";
				String cont2 = "";

				switch (type) {
				case "DefaultFragment":
					/*
					 * Log.d("saver", "fragid:  " + fragId); Log.d("saver",
					 * "fragType:  " + type);
					 */
					TempFragment = (DefaultFragment) getFragmentManager()
							.findFragmentByTag(fragId);

					Temp = ((DefaultFragment) TempFragment).saveContent();

					if (!Temp.get("cont1").equalsIgnoreCase("")) {
						cont1 = Temp.get("cont1");
					} else {
						cont1 = "No Title";
					}
					db.editRec(id, style, cont1);

					break;
				case "PicFragment":

					TempFragment = (PicFragment) getFragmentManager()
							.findFragmentByTag(fragId);
					Temp = ((PicFragment) TempFragment).saveContent();

					cont1 = Temp.get("cont1");

					break;
				case "NoteFragment":
					/*
					 * Log.d("saver", "fragid:  " + fragId); Log.d("saver",
					 * "fragType:  " + type);
					 */
					TempFragment = (NoteFragment) getFragmentManager()
							.findFragmentByTag(fragId);
					Temp = ((NoteFragment) TempFragment).saveContent();

					if (!Temp.get("cont1").equalsIgnoreCase("")) {
						cont1 = Temp.get("cont1");
					} else {
						cont1 = "No Content";
					}

					break;

				case "LinkFragment":
					/*
					 * Log.d("saver", "fragid:  " + fragId); Log.d("saver",
					 * "fragType:  " + type);
					 */
					TempFragment = (LinkFragment) getFragmentManager()
							.findFragmentByTag(fragId);
					Temp = ((LinkFragment) TempFragment).saveContent();

					if (!Temp.get("cont1").equalsIgnoreCase("")) {
						cont1 = Temp.get("cont1");
					} else {
						cont1 = "No Link";
					}

					break;
				case "AudioFragment":
					/*
					 * Log.d("saver", "fragid:  " + fragId); Log.d("saver",
					 * "fragType:  " + type);
					 */
					TempFragment = (AudioFragment) getFragmentManager()
							.findFragmentByTag(fragId);
					Temp = ((AudioFragment) TempFragment).saveContent();

					if (!Temp.get("cont1").equalsIgnoreCase("")) {
						cont1 = Temp.get("cont1");
					} else {
						cont1 = "No Audio";
					}
					if (!Temp.get("cont2").equalsIgnoreCase("")) {
						cont2 = Temp.get("cont2");
					} else {
						cont2 = "No Audio";
					}
					break;

				}

				db.addFragment(id, type, cont1, cont2);

			}

			TempFragment = null;
			Temp = null;
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		db.close();
	}

}
