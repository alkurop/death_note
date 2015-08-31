package com.omar.deathnote.notes.ui;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.omar.deathnote.main.ui.MainActivity_old;
import com.omar.deathnote.utils.FileManager;
import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.omar.deathnote.dialogs.AddAudioDialog;
import com.omar.deathnote.dialogs.AddPicDialog;
import com.omar.deathnote.dialogs.DialogOnDelete;
import com.omar.deathnote.dialogs.DialogOnDelete.DeleteDialog;
import com.omar.deathnote.fragments.AudioFragment;
import com.omar.deathnote.fragments.AudioFragment.NextAudio;
import com.omar.deathnote.utility.FragmentCreator;
import com.omar.deathnote.utility.FragmentSaver;
import com.omar.deathnote.utility.LoaderCallback;
import com.omar.deathnote.utility.OnDeleteFragment;
import com.omar.deathnote.utility.SaveNote;
import com.omar.deathnote.utility.SharingModule;

@SuppressWarnings({ "deprecation", "incomplete-switch" })
public class NoteActivity extends Activity implements INoteView, OnNavigationListener {

	
	
	
	private static long id;
	private static int style;
	private static TreeMap<String, String> fragList;
	private static int fragCount;

	private int result;
	private String fragToDel;
	private boolean audioShuffle;
	private boolean audioRepeat;

	// my listeners

	private static SaveNote saveNote;
	private static NextAudio nextAudio;
	private static OnDeleteFragment onDeleteFragment;
	private static AddPicDialog.PicDialogListener picDialogListener;
	private static AddAudioDialog.AudioDialogListener audioDialogListener;
	private static DeleteDialog deleteDialog;

	private static LoaderManager loaderManager;
	private static FragmentManager fm;
	private static ActionBar actionBar;
	private static LoaderCallback callbacks;
	private SimpleAdapter selectAdapter;
	private ArrayList<Map<String, Object>> dataSelect;
	private Map<String, Object> m;

	private LinearLayout noteList;
	private FileManager sc;
	private Fragment tempFragment;
	private FragmentTransaction fTrans;
	private LinearLayout mainLayout;

 
	public static SaveNote getSaveNote() {
		return saveNote;
	}

	public static NextAudio getNextAudio() {
		return nextAudio;
	}

	public static DeleteDialog getDeleteDialog() {
		return deleteDialog;
	}

	public static OnDeleteFragment getOnDeleteFragment() {
		return onDeleteFragment;
	}

	public static AddPicDialog.PicDialogListener getPicDialogListener() {
		return picDialogListener;
	}

	public static AddAudioDialog.AudioDialogListener getAudioDialogListener() {
		return audioDialogListener;
	}

	public static long getId() {
		return id;
	}

	public static void setId(long id) {
		NoteActivity.id = id;
	}

	public static int getStyle() {
		return style;
	}

	public static void setStyle(int style) {
		NoteActivity.style = style;
		actionBar.setSelectedNavigationItem(style - 1);
	}

	// Activity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getActionBar();
		setContentView(R.layout.note);
		actionBar.setDisplayShowTitleEnabled(false);
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
		callbacks = LoaderCallback.getInstance(this);

		if (savedInstanceState == null) {

			audioShuffle = false;
			audioRepeat = false;
			Bundle extras = getIntent().getExtras();
			fragCount = 0;

			if (extras.getLong(Constants.ID) != 0) {

				id = extras.getLong(Constants.ID);

				loaderManager.restartLoader(LoaderCallback.LOAD_NOTE, null,
						callbacks);
				loaderManager.restartLoader(LoaderCallback.LOAD_STYLE, null,
						callbacks);

			} else {
				 
				style = extras.getInt(Constants.STYLE);
				actionBar.setSelectedNavigationItem(style - 1);

				loaderManager.restartLoader(LoaderCallback.ADD_NEW_NOTE, null,
						callbacks);

				createFragment(extras.getString(Constants.TITLE), Constants.SPACE,
						Constants.Frags.DefaultFragment);

				createFragment(Constants.SPACE, Constants.SPACE, Constants.Frags.NoteFragment);

			}

		} else {

			fragCount = savedInstanceState.getInt(Constants.FRAGMENT_COUNTER);
			id = savedInstanceState.getLong(Constants.ID);
			keys = savedInstanceState.getStringArray(Constants.FRAGMENT_LIST_KEYS);
			values = savedInstanceState.getStringArray(Constants.FRAGMENT_LIST_VALUES);
			style = savedInstanceState.getInt(Constants.STYLE);
			audioShuffle = savedInstanceState.getBoolean(Constants.AUDIO_SHUFFLE);
			audioRepeat = savedInstanceState.getBoolean(Constants.AUDIO_REPEAT);

			fragList = new TreeMap<String, String>();
			for (int k = 0; k < keys.length; k++) {

				fragList.put(keys[k], values[k]);

			}
			setBackGround();
			actionBar.setSelectedNavigationItem(style - 1);
		}
		setupMyListeners() ;

	}

	@Override
	protected void onDestroy() {

	 

		Intent intent = new Intent();
		intent.putExtras(save());
		setResult(result, intent);

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		outState.putInt(Constants.STYLE, style);
		outState.putInt(Constants.FRAGMENT_COUNTER, fragCount);
		outState.putLong(Constants.ID, id);
		outState.putBoolean(Constants.AUDIO_SHUFFLE, audioShuffle);
		outState.putBoolean(Constants.AUDIO_REPEAT, audioRepeat);

		String[] keys = new String[fragList.size()];
		String[] values = new String[fragList.size()];
		int k = 0;
		for (Map.Entry<String, String> entry : fragList.entrySet()) {

			keys[k] = entry.getKey();
			values[k] = entry.getValue();
			k++;
		}

		outState.putStringArray(Constants.FRAGMENT_LIST_KEYS, keys);
		outState.putStringArray(Constants.FRAGMENT_LIST_VALUES, values);

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
			SharingModule sM = new SharingModule(fm, this, fragList);
			sM.share();

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

			pic.show(fm, Constants.Frags.NoticeDialogFragment.name());

			break;

		case R.id.addLink:
			xSave();
			createFragment(null, null, Constants.Frags.LinkFragment);

			break;

		case R.id.addText:
			xSave();
			createFragment(null, null, Constants.Frags.NoteFragment);

			break;
		case R.id.addAudio:
			xSave();

			AddAudioDialog aud = new AddAudioDialog();

			aud.show(fm, Constants.Frags.NoticeDialogFragment.name());

			break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		xSave();
		super.onStop();

	}

	// Setup and save Methods

	public static void createFragment(String cont1, String cont2,
			Constants.Frags type) {

		fragList = new FragmentCreator(fm).createFragment(cont1, cont2, type,
				fragCount, id, fragList);
		fragCount++;

	}

	private void setupMyListeners() {

		saveNote = new SaveNote() {

			@Override
			public void saveRun() {

				xSave();
			}
		};
		onDeleteFragment = new OnDeleteFragment() {

			@Override
			public void delete(String s, boolean dialog) {
				fragToDel = s;
				if (dialog) {
					DialogFragment delDialog = new DialogOnDelete();
					delDialog.show(fm, Constants.Flags.Dialog.name());
				} else {
					confirmDelete(fragToDel);
				}
			}
		};
		picDialogListener = new AddPicDialog.PicDialogListener() {

			@Override
			public void onDialogClickBrowsePic(DialogFragment dialog) {

				createFragment(null, null, Constants.Frags.PicFragment);

			}

			@Override
			public void onDialogClickCameraPic(DialogFragment dialog) {
				createFragment(null, Constants.CAMERA, Constants.Frags.PicFragment);
			}
		};
		audioDialogListener = new AddAudioDialog.AudioDialogListener() {

			@Override
			public void onDialogClickAudioBrowse(DialogFragment dialog) {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
					sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_MOUNTED,
							Uri.parse("file://"
									+ Environment.getExternalStorageDirectory())));
				} else {
					sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
							Uri.parse("file://"
									+ Environment.getExternalStorageDirectory())));
				}
				createFragment(null, null, Constants.Frags.AudioFragment);
			}

			@Override
			public void onDialogClickAudioRecord(DialogFragment dialog) {
				createFragment(Constants.RECORD, null, Constants.Frags.AudioFragment);

			}
		};
		nextAudio = new NextAudio() {

			@Override
			public void stopAllAudio(String cur) {
				stopAllAudioButCurrent(cur);
			 

			}

			@Override
			public void shuffle(boolean shuffle) {
				audioShuffle = shuffle;
				updateShuffleRepeat();

			}

			@Override
			public void repeat(boolean repeat) {
				audioRepeat = repeat;
				updateShuffleRepeat();

			}

			@Override
			public void refreshUi(boolean audioRepeat, boolean audioShuffle,
					boolean paused, int audionumber) {

				int k = 0;
				for (Map.Entry<String, String> entry : fragList.entrySet()) {
					if (entry.getValue().equalsIgnoreCase(
							Constants.Frags.AudioFragment.name())) {

						tempFragment = (AudioFragment) fm
								.findFragmentByTag(entry.getKey());
						((AudioFragment) tempFragment).refreshStopAudio();
						if (k == audionumber) {
							((AudioFragment) tempFragment)
									.refreshPlayAudio(paused);

						}
						k++;

					}

				}

			}

			@Override
			public void next(String cur, String flag) {

				stopAllAudioButCurrent(Constants.BLANK);
				playNextAudio(cur, flag);

			}

		};
		deleteDialog = new DeleteDialog() {

			@Override
			public void del() {

				confirmDelete(fragToDel);

			}
		};
	}

	private void naviSelect() {

		dataSelect = new ArrayList<Map<String, Object>>();
		for (int i = 1; i < Constants.select_images.length; i++) {

			m = new HashMap<String, Object>();
			m.put(Constants.ATTRIBUTE_NAME_TEXT,
					getResources().getString(Constants.select_names[i]));
			m.put(Constants.ATTRIBUTE_NAME_STYLE, Constants.select_images[i]);
			dataSelect.add(m);
		}

		String[] fromSel = { Constants.ATTRIBUTE_NAME_TEXT,
				Constants.ATTRIBUTE_NAME_STYLE };
		int[] toSel = { R.id.itemName, R.id.itemImg };
		selectAdapter = new SimpleAdapter(this, dataSelect, R.layout.select,
				fromSel, toSel);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		selectAdapter.setDropDownViewResource(R.layout.select);
		getActionBar().show();
		actionBar.setListNavigationCallbacks(selectAdapter, this);
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	private void setBackGround() {

		if (getResources().getConfiguration().orientation == 1) {
			mainLayout.setBackgroundResource(Constants.note_bg_images[style - 1]);
		} else {
			mainLayout
					.setBackgroundResource(Constants.note_bg_images_1[style - 1]);
		}
	}

	private void xSave() {
		FragmentSaver fs = new FragmentSaver(fm, fragList);

		Bundle titleBundle = fs.getTitleBundle();
		loaderManager.restartLoader(LoaderCallback.EDIT_REC_TITLE,
				titleBundle, callbacks);

		Bundle saveBundle = fs.saveFragment();
		loaderManager.restartLoader(LoaderCallback.SAVE_NOTE, saveBundle,
				callbacks);

	}

	private Bundle save() {

		Bundle bundle = new Bundle();
		bundle.putInt(Constants.STYLE, style);

		/* xSave(); */
		return bundle;

	}

	private void confirmDelete(String s) {
		tempFragment = fm.findFragmentByTag(s);
		fTrans = fm.beginTransaction();
		fTrans.hide(tempFragment);
		fTrans.remove(tempFragment);
		fTrans.commit();
 
		tempFragment = null;

		fragList.remove(s);
		xSave();
	}

	public static Constants.Frags setFragType(String type) {

		Constants.Frags[] frags = Constants.Frags.values();
		Constants.Frags eType = null;

		for (Constants.Frags frag : frags) {
			if (type.equalsIgnoreCase(frag.name())) {
				eType = frag;
			}
		}

		return eType;
	}

	// audio specific methods

	public void playNextAudio(String cur, String flag) {

		String id = Constants.BLANK;
		ArrayList<String> keys = new ArrayList<String>();
		int l = 0;
		int k = 0;
		for (Map.Entry<String, String> entry : fragList.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(
					Constants.Frags.AudioFragment.name())) {

				keys.add(entry.getKey());

				if (entry.getKey() == cur) {
					l = k;

				}
				k++;

			}

		}

		switch (flag) {
		case Constants.NEXT:
			if (keys.size() > l + 1) {

				id = keys.get(l + 1);
			} else {

				id = keys.get(0);
			}
			break;
		case Constants.PREVIOUS:
			if (l != 0) {

				id = keys.get(l - 1);
			} else {

				id = keys.get(keys.size() - 1);
			}
			break;
		case Constants.REPLAY:
			id = cur;
			break;
		case Constants.SHUFFLE_AUDIO:
			id = keys.get(MainActivity_old.randInt(0, keys.size() - 1));
			break;

		}
		tempFragment = (AudioFragment) fm.findFragmentByTag(id);
		((AudioFragment) tempFragment).playAudio();

	}

	public void stopAllAudioButCurrent(String s) {

		for (Map.Entry<String, String> entry : fragList.entrySet()) {

			String fragId = entry.getKey();
			String type = entry.getValue();
			Constants.Frags[] frags = Constants.Frags.values();
			Constants.Frags eType = null;

			for (Constants.Frags frag : frags) {
				if (type.equalsIgnoreCase(frag.name())) {
					eType = frag;
				}
			}

			switch (eType) {

			case AudioFragment:
				if (!fragId.equalsIgnoreCase(s)) {
					tempFragment = (AudioFragment) fm.findFragmentByTag(fragId);
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

			Constants.Frags[] frags = Constants.Frags.values();
			Constants.Frags eType = null;

			for (Constants.Frags frag : frags) {
				if (type.equalsIgnoreCase(frag.name())) {
					eType = frag;
				}
			}

			switch (eType) {

			case AudioFragment:

				tempFragment = (AudioFragment) fm.findFragmentByTag(fragId);
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
			Constants.Frags[] frags = Constants.Frags.values();
			Constants.Frags eType = null;

			for (Constants.Frags frag : frags) {
				if (type.equalsIgnoreCase(frag.name())) {
					eType = frag;
				}
			}

			switch (eType) {

			case AudioFragment:

				tempFragment = (AudioFragment) fm.findFragmentByTag(fragId);
				((AudioFragment) tempFragment).loadShuffleRepeat(audioShuffle,
						audioRepeat);

				break;

			}

		}

	}

}
