package com.omar.deathnote;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
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
import com.omar.deathnote.utility.FragContent;
import com.omar.deathnote.utility.FragmentCreator;
import com.omar.deathnote.utility.OnDeleteFragment;
import com.omar.deathnote.utility.SaveNote;




@SuppressWarnings({"deprecation" , "incomplete-switch"})

public class NoteActivity extends Activity implements SaveNote, NextAudio,
		OnNavigationListener, OnDeleteFragment, AddPicDialog.PicDialogListener,
		AddAudioDialog.AudioDialogListener, LoaderCallbacks<Cursor>,
		DeleteDialog {

	private static long id;
	private static int style;

	private SimpleAdapter selectAdapter;
	private ArrayList<Map<String, Object>> dataSelect;
	private Map<String, Object> m;
 
	private LinearLayout noteList;
	private int fragCount;

	private FileManager sc;
	private Fragment tempFragment;

	private FragmentCreator fCreator;

	public static final String BROADCAST_DESTROY = "com.omar.deathnote.fragments.audiofragment.destroy";
	private FragmentTransaction fTrans;
	private   TreeMap<String, String> fragList;

	private LinearLayout mainLayout;
	private int result;
	private String fragToDel;
	private boolean audioShuffle;
	private boolean audioRepeat;
	private static LoaderManager loaderManager;
	private static FragmentManager fm;

	private static final int SAVE_NOTE = 0;
	private static final int SHARE_NOTE = 1;
	private static final int LOAD_NOTE = 2;
	private static final int EDIT_REC_TITLE = 3;
	private static final int LOAD_STYLE = 4;
	private static final int ADD_NEW_NOTE = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.note);
		getActionBar().setDisplayShowTitleEnabled(false);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

 
		sc = new FileManager(getApplicationContext());
		fragList = new TreeMap<String, String>();
		String[] keys = null;
		String[] values = null;

		mainLayout = (LinearLayout) findViewById(R.id.fNote);

		result = RESULT_OK;
	 

		naviSelect();

		LayoutInflater li = getLayoutInflater();
		li.inflate(R.layout.note, noteList, true);
		
		
		loaderManager = getLoaderManager();
		fm = getFragmentManager();
				
	
		if (savedInstanceState == null) {

			audioShuffle = false;
			audioRepeat = false;
			Bundle extras = getIntent().getExtras();
			fragCount = 0;

			if (extras.getLong("id") != 0) {
				

				id = extras.getLong("id");
				loaderManager.restartLoader(LOAD_STYLE, null, this);
				loaderManager.restartLoader(LOAD_NOTE, null, this);

			} else {
			/*	Log.d("LoadingNote","loadig note");*/
				style = extras.getInt("style");
				getActionBar().setSelectedNavigationItem(style - 1);

				loaderManager.restartLoader(ADD_NEW_NOTE, null, this);
				
				/*Log.d("creating DF","creating DF: " + extras.getString("title") + ", " + Select.Frags.DefaultFragment);
				*/
				createFragment(extras.getString("title"), " ",
						Select.Frags.DefaultFragment);
				
		/*		Log.d("creating NF","creating NF");*/
				
				createFragment(" ", " ", Select.Frags.NoteFragment);

			}

		} else {

			fragCount = savedInstanceState.getInt("fragCount");
			id = savedInstanceState.getLong("id");
			keys = savedInstanceState.getStringArray("fragList_keys");
			values = savedInstanceState.getStringArray("fragList_values");
			style = savedInstanceState.getInt("style");
			audioShuffle = savedInstanceState.getBoolean("audioShuffle");
			audioRepeat = savedInstanceState.getBoolean("audioRepeat");

			fragList = new TreeMap<String, String>();
			for (int k = 0; k < keys.length; k++) {

				fragList.put(keys[k], values[k]);

			}
			setBackGround();
			getActionBar().setSelectedNavigationItem(style - 1);
		}

	}

	@Override
	protected void onDestroy() {

		Intent destroyMusic = new Intent(AudioFragment.BROADCAST_PAUSESONG);
		destroyMusic.putExtra("flag", "destroy");
		sendBroadcast(destroyMusic);

		Intent intent = new Intent();
		intent.putExtras(save());
		setResult(result, intent);

		super.onDestroy();
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
			values[k] = entry.getValue();
			k++;
		}

		outState.putStringArray("fragList_keys", keys);
		outState.putStringArray("fragList_values", values);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.note, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onNavigationItemSelected(int pos, long id) {
		style = (pos + 1);
		setBackGround();

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_share:
			xSave();
			loaderManager.restartLoader(SHARE_NOTE, null, this);
			
			break;

		case R.id.save:

			finish();

			break;

		case android.R.id.home:

			finish();

			break;

		case R.id.btnRepeat:

			break;

		case R.id.addPic:
			xSave();
			AddPicDialog pic = new AddPicDialog();

			pic.show(getFragmentManager(),
					Select.Frags.NoticeDialogFragment.name());

			break;

		case R.id.addLink:
			xSave();
			createFragment(null, null, Select.Frags.LinkFragment);

			break;

		case R.id.addText:
			xSave();
			createFragment(null, null, Select.Frags.NoteFragment);

			break;
		case R.id.addAudio:
			xSave();

			AddAudioDialog aud = new AddAudioDialog();

			aud.show(getFragmentManager(),
					Select.Frags.NoticeDialogFragment.name());

			break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		xSave();
		super.onPause();

	}

	public void createFragment(String cont1, String cont2, Select.Frags type) {
	/*	Log.d("type", type.name());*/
		
		
		fCreator = new FragmentCreator(fm);
		fragList = fCreator.createFragment(cont1, cont2, type, fragCount, id,
				fragList);
		fragCount++;

	}

	public void xSave() {
		
		Bundle saveBundle = new Bundle();
		ArrayList<FragContent> fragsArrayList = new ArrayList<FragContent>();
		
		if(fragList != null)
		/*	Log.d("fraglist length", String.valueOf(fragList.size()));*/
		

		for (Map.Entry<String, String> entry : fragList.entrySet()) {
			 TreeMap<String, String> temp = new TreeMap<String, String>();

			String fragId = entry.getKey();
			String type = entry.getValue();
			/*Log.d("type", type);*/
			String cont1 = "";
			String cont2 = "";
			Select.Frags[] frags = Select.Frags.values();
			Select.Frags eType = null;

			for (Select.Frags frag : frags) {
				
				if (type.equalsIgnoreCase(frag.name())) {
					eType = frag;
				/*	Log.d("frags", eType.name());*/
				}
			}

			 switch (eType) {

			case DefaultFragment:

				tempFragment = (DefaultFragment) getFragmentManager()
						.findFragmentByTag(fragId);

				temp = ((DefaultFragment) tempFragment).saveContent();

				if (temp.get(Select.Flags.Cont1.name()) != null) {
					cont1 = temp.get(Select.Flags.Cont1.name());
				} else {
					cont1 = "No Title";
				}

				Bundle titleBundle = new Bundle();
				titleBundle.putString(Select.Flags.Cont1.name(), cont1);

				loaderManager.restartLoader(EDIT_REC_TITLE, titleBundle,
						this);

				break;
			case PicFragment:

				tempFragment = (PicFragment) getFragmentManager()
						.findFragmentByTag(fragId);
				temp = ((PicFragment) tempFragment).saveContent();

				cont1 = temp.get(Select.Flags.Cont1.name());
				if(cont1 !=null);
				 

				break;
			case NoteFragment:

				tempFragment = (NoteFragment) getFragmentManager()
						.findFragmentByTag(fragId);
				temp = ((NoteFragment) tempFragment).saveContent();

				if (!temp.get(Select.Flags.Cont1.name()).equalsIgnoreCase("")) {
					cont1 = temp.get(Select.Flags.Cont1.name());
				} else {
					cont1 = "No Content";
				}

				break;

			case LinkFragment:

				tempFragment = (LinkFragment) getFragmentManager()
						.findFragmentByTag(fragId);
				temp = ((LinkFragment) tempFragment).saveContent();

				if (!temp.get(Select.Flags.Cont1.name()).equalsIgnoreCase("")) {
					cont1 = temp.get(Select.Flags.Cont1.name());
				} else {
					cont1 = "No Link";
				}

				break;
			case AudioFragment:

				tempFragment = (AudioFragment) getFragmentManager()
						.findFragmentByTag(fragId);
				temp = ((AudioFragment) tempFragment).saveContent();

				if (!temp.get(Select.Flags.Cont1.name()).equalsIgnoreCase("")) {
					cont1 = temp.get(Select.Flags.Cont1.name());
				} else {
					cont1 = "No Audio";
				}
				if (!temp.get(Select.Flags.Cont2.name()).equalsIgnoreCase("")) {
					cont2 = temp.get(Select.Flags.Cont2.name());
				} else {
					cont2 = "No Audio";
				}
				break;
	 
				
			default:
				throw new IllegalArgumentException("illigal fragment type");

			}
			 
			 
			 
			 
			FragContent fragContent = new FragContent(type, cont1, cont2);
			fragsArrayList.add(fragContent); 

		}

		
		
/*		Log.d("save list size",String.valueOf(fragsArrayList.size()));*/
		
		 saveBundle.putParcelableArrayList("fragsArrayList", fragsArrayList);
		loaderManager.restartLoader(SAVE_NOTE, saveBundle, this);

		tempFragment = null;
	 
 
	}

	public Bundle save() {
 
		Bundle bundle = new Bundle();
		bundle.putInt("style", style);

	/*	xSave();*/
		return bundle;

	}

	@Override
	public void saveRun() {

		xSave();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		DB db = new DB(this);
		db.open();

		switch (id) {

		case LOAD_NOTE:

			return new NoteLoader(this, db);

		case EDIT_REC_TITLE:
			return new EditNoteTitleLoader(this, db, bundle);

		case LOAD_STYLE:
			return new StyleLoader(this, db);

		case ADD_NEW_NOTE:
			return new NewNoteLoader(this, db);

		case SAVE_NOTE:
			return new SaveNoteLoader(this, db, bundle);

		case SHARE_NOTE:
			return new NoteLoader(this, db);

		default:
			return null;

		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int loaderId = loader.getId();
		switch (loaderId) {
		case ADD_NEW_NOTE:
			id = cursor.getInt(cursor.getColumnIndex(DB.COLUMN_ID));
			
			break;

		case LOAD_NOTE:
			if (cursor != null) {
			

				while (cursor.moveToNext()) {
					String type = cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_TYPE));
					String cont1 = cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_CONT1));
					String cont2 = cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_CONT2));
				
					Select.Frags eType = setFragType(type);
				/*	Log.d ("loading saved note fragment:", eType.name());
					Log.d ("loading saved note f cont1:", ":" + cont1);
					Log.d ("loading saved note f cotnt2:",":" + cont2 );*/
					
					createFragment(cont1,
							cont2, eType);

				}
			} else {
		/*		Log.d ("loading saved note  ", "cursor == null");*/
				createFragment("", null, Select.Frags.DefaultFragment);
				createFragment(" ", null, Select.Frags.NoteFragment);
			}

			break;

		case LOAD_STYLE:
			style = cursor.getInt(cursor.getColumnIndex(DB.COLUMN_STYLE));
			getActionBar().setSelectedNavigationItem(style - 1);
			break;

		case SHARE_NOTE:
			SharingTask sTask = new SharingTask();
			sTask.execute(cursor);
			
			

			break;
		default:
			break;

		}

	}

	@Override
	public void del( ) {
		 
				confirmDelete(fragToDel);
			 
		 

	}

	@Override
	public void delete(String s, boolean dialog) {
		fragToDel = s;
		if (dialog) {
			DialogFragment delDialog = new DialogOnDelete();
			delDialog.show(getFragmentManager(), Select.Flags.Dialog.name());
		} else {
			confirmDelete(fragToDel);
		}
	}

	public void confirmDelete(String s) {
		tempFragment = getFragmentManager().findFragmentByTag(s);
		fTrans = getFragmentManager().beginTransaction();
		fTrans.hide(tempFragment);
		fTrans.remove(tempFragment);
		fTrans.commit();

		if (fragList.get(s) == Select.Frags.PicFragment.name()) {
			String imName = String.valueOf(id) + "_" + s;

			sc.new DeleteFile().execute(imName);

		}
		tempFragment = null;

		fragList.remove(s);
		xSave();
	}
 
	public void naviSelect() {

		dataSelect = new ArrayList<Map<String, Object>>();
		for (int i = 1; i < Select.select_images.length; i++) {

			m = new HashMap<String, Object>();
			m.put(Select.ATTRIBUTE_NAME_TEXT,
					getResources().getString(Select.select_names[i]));
			m.put(Select.ATTRIBUTE_NAME_STYLE, Select.select_images[i]);
			dataSelect.add(m);
		}

		String[] fromSel = { Select.ATTRIBUTE_NAME_TEXT,
				Select.ATTRIBUTE_NAME_STYLE };
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

	public Select.Frags setFragType(String type) {

		Select.Frags[] frags = Select.Frags.values();
		Select.Frags eType = null;

		for (Select.Frags frag : frags) {
			if (type.equalsIgnoreCase(frag.name())) {
				eType = frag;
			}
		}

		return eType;
	}

	public void setBackGround() {

		if (getResources().getConfiguration().orientation == 1) {
			mainLayout.setBackgroundResource(Select.note_bg_images[style - 1]);
		} else {
			mainLayout
					.setBackgroundResource(Select.note_bg_images_1[style - 1]);
		}
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
		createFragment(null, null, Select.Frags.AudioFragment);
	}

	@Override
	public void onDialogClickAudioRecord(DialogFragment dialog) {
		createFragment("rec", null, Select.Frags.AudioFragment);

	}

	@Override
	public void onDialogClickBrowsePic(DialogFragment dialog) {

		createFragment(null, null, Select.Frags.PicFragment);

	}

	@Override
	public void onDialogClickCameraPic(DialogFragment dialog) {
		createFragment(null, "cam", Select.Frags.PicFragment);
	}

	public void playNextAudio(String cur, String flag) {

		String id = "";
		ArrayList<String> keys = new ArrayList<String>();
		int l = 0;
		int k = 0;
		for (Map.Entry<String, String> entry : fragList.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(
					Select.Frags.AudioFragment.name())) {

				keys.add(entry.getKey());

				if (entry.getKey() == cur) {
					l = k;

				}
				k++;

			}

		}

		switch (flag) {
		case "next":
			if (keys.size() > l + 1) {

				id = keys.get(l + 1);
			} else {

				id = keys.get(0);
			}
			break;
		case "prev":
			if (l != 0) {

				id = keys.get(l - 1);
			} else {

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
		tempFragment = (AudioFragment) getFragmentManager().findFragmentByTag(
				id);
		((AudioFragment) tempFragment).playAudio();

	}

	@Override
	public void refreshUi(boolean audioRepeat, boolean audioShuffle,
			boolean paused, int audionumber) {

		int k = 0;
		for (Map.Entry<String, String> entry : fragList.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(
					Select.Frags.AudioFragment.name())) {

				tempFragment = (AudioFragment) getFragmentManager()
						.findFragmentByTag(entry.getKey());
				((AudioFragment) tempFragment).refreshStopAudio();
				if (k == audionumber) {
					((AudioFragment) tempFragment).refreshPlayAudio(paused);

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

	@Override
	public void next(String cur, String flag) {

		stopAllAudioButCurrent("");
		playNextAudio(cur, flag);

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
			Select.Frags[] frags = Select.Frags.values();
			Select.Frags eType = null;

			for (Select.Frags frag : frags) {
				if (type.equalsIgnoreCase(frag.name())) {
					eType = frag;
				}
			}

			switch (eType) {

			case AudioFragment:
				if (!fragId.equalsIgnoreCase(s)) {
					tempFragment = (AudioFragment) getFragmentManager()
							.findFragmentByTag(fragId);
					((AudioFragment) tempFragment).specialStopAudio();
				}
				break;

			}

		}
	}

	public void closeStopAllAudio() {

		for (Map.Entry<String, String> entry : fragList.entrySet()) {

			String fragId = entry.getKey();
			String type = entry.getValue();

			Select.Frags[] frags = Select.Frags.values();
			Select.Frags eType = null;

			for (Select.Frags frag : frags) {
				if (type.equalsIgnoreCase(frag.name())) {
					eType = frag;
				}
			}

			switch (eType) {

			case AudioFragment:

				tempFragment = (AudioFragment) getFragmentManager()
						.findFragmentByTag(fragId);
				if (((AudioFragment) tempFragment).musicPlaying) {
					((AudioFragment) tempFragment).stopAudio();
				} else if (((AudioFragment) tempFragment).recording) {
					((AudioFragment) tempFragment).recStop();

				}

				break;

			}

		}
	}
	
	public void updateShuffleRepeat() {

		for (Map.Entry<String, String> entry : fragList.entrySet()) {

			String fragId = entry.getKey();
			String type = entry.getValue();
			Select.Frags[] frags = Select.Frags.values();
			Select.Frags eType = null;

			for (Select.Frags frag : frags) {
				if (type.equalsIgnoreCase(frag.name())) {
					eType = frag;
				}
			}

			switch (eType) {

			case AudioFragment:

				tempFragment = (AudioFragment) getFragmentManager()
						.findFragmentByTag(fragId);
				((AudioFragment) tempFragment).loadShuffleRepeat(audioShuffle,
						audioRepeat);

				break;

			}

		}

	}

	public Bundle formSharingContent(Cursor cursor) {
		Bundle bundle = new Bundle();
		String subject = null;
		String text = null;
		StringBuilder stringBuilder = new StringBuilder();
		ArrayList<Uri> uris = new ArrayList<Uri>();
		ArrayList<Uri> urisSpecial = new ArrayList<Uri>();
		if (cursor != null) {
	
			

			while (cursor.moveToNext()) {
			/*	Log.d("sharing", "fetchin from db");*/
				String type = cursor.getString(cursor
						.getColumnIndex(DB.COLUMN_TYPE));
				File fileIn;
				Uri u;

				Select.Frags eType = setFragType(type);

				switch (eType) {
				case DefaultFragment:
					subject = cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_CONT1));

					break;

				case NoteFragment:

					stringBuilder.append("\n"
							+ "\n"
							+ cursor.getString(cursor
									.getColumnIndex(DB.COLUMN_CONT1)));

					break;
				case LinkFragment:

					stringBuilder.append("http://"
							+ cursor.getString(cursor
									.getColumnIndex(DB.COLUMN_CONT1)) + "\n"
							+ "\n");

					break;
				case PicFragment:
					fileIn = new File(cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_CONT1)));
					fileIn.setReadable(true, false);
					u = Uri.fromFile(fileIn);
					uris.add(u);

					break;

				case AudioFragment:
					fileIn = new File(cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_CONT1)));
					fileIn.setReadable(true, false);
					u = Uri.fromFile(fileIn);
					urisSpecial.add(u);
					break;
				default:
					throw new IllegalArgumentException(
							"This type of fragment not found : " + type);

				}

			}

			text = stringBuilder.toString();

		}
		bundle.putString("subject", subject);
		bundle.putString("text", text);
		bundle.putParcelableArrayList("uris", uris);
		bundle.putParcelableArrayList("urisSpecial", urisSpecial);
		
		
		return bundle;

	}
	
	public void initShareIntent(Bundle bundle) {
		/*Log.d("sharing", "sharing");*/
		String subject = bundle.getString("subject");
		String text = bundle.getString("text");
		 
		ArrayList<Uri> uris = bundle.getParcelableArrayList( "uris");
		ArrayList<Uri> urisSpecial =  bundle.getParcelableArrayList( "urisSpecial");
		 

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

			ri = resInfo.get(i);
/*			Log.d("share type ---->>>", ri.activityInfo.packageName);*/
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
 
		LabeledIntent[] extraIntents = intentList
				.toArray(new LabeledIntent[intentList.size()]);

		openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
		startActivity(openInChooser);
	}
	
	
	
	private class SharingTask extends AsyncTask<Cursor, Void, Bundle>{

		@Override
		protected Bundle doInBackground(Cursor... params) {
		 
			return formSharingContent(params[0]);
		}

		@Override
		protected void onPostExecute(Bundle result) {
			 initShareIntent( result) ;
		}
		
		
	
	
	
	}
	
	private static class NoteLoader extends CursorLoader {
		DB db;

		public NoteLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;
			cursor = db.getAllNoteData(id);
			return cursor;
		}

	}

	private static class StyleLoader extends CursorLoader {
		DB db;

		public StyleLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;
			cursor = db.fetchRec(id);
			return cursor;
		}

	}

	private static class NewNoteLoader extends CursorLoader {
		DB db;

		public NewNoteLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;
			db.addRec(style, "");
			cursor = db.fetchLast();
			return cursor;
		}

	}

	private static class EditNoteTitleLoader extends CursorLoader {
		DB db;
		Bundle bundle;

		public EditNoteTitleLoader(Context context, DB db, Bundle bundle) {
			super(context);
			this.db = db;
			this.bundle = bundle;
		}

		@Override
		public Cursor loadInBackground() {

			db.editRec(id, style, bundle.getString(Select.Flags.Cont1.name()));

			return null;
		}

	}

	private static class SaveNoteLoader extends CursorLoader {
		DB db;
				ArrayList<FragContent> fragsArrayList;

		public SaveNoteLoader(Context context, DB db, Bundle bundle) {
			super(context);
			this.db = db;
			this.fragsArrayList = bundle.getParcelableArrayList("fragsArrayList");
			
 
			
		}

		@Override
		public Cursor loadInBackground() {
			 
				db.deleteNoteTable(id);
				db.createNoteTable(id);
				for (FragContent listItem : fragsArrayList) {

					db.addFragment(id, listItem.getType(), listItem.getCont1(),
							listItem.getCont2());
					
				 
					
				}
		 

			return null;
		}
	}

}
