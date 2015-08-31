package com.omar.deathnote.fragments;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.omar.deathnote.db.DB;
import com.omar.deathnote.utils.FileManager;
import com.omar.deathnote.notes.ui.NoteActivity;
import com.omar.deathnote.R;
import com.omar.deathnote.Constants;
import com.omar.deathnote.Constants.Frags;
import com.omar.deathnote.audioplay.AudioPlayService;
import com.omar.deathnote.utility.OnDeleteFragment;
import com.omar.deathnote.utility.SaveNote;

@SuppressLint({ "SimpleDateFormat", "InflateParams" })
public class AudioFragment extends Fragment implements OnFocusChangeListener {

	private static int songEnded = 0;
	private String noteId;
	public boolean musicPlaying, recording, recorderMode, musicPaused,
			repeatAudio, shuffleAudio;
	private String audioName, audioPath, fragId;
	private int seekMax;

	private OnDeleteFragment OnDeleteFragment;
	private SaveNote sX;

	private Intent seekBarChanged, songPause, audioAutonome, audioPlayIntent;
	private Context thiscontext;
	private ImageView del, play, prev, stop, next, repeat, shuffle;
	private SeekBar seekBar;
	private LinearLayout hidable;
	private TextView songName, songTime;
	private View v;
	private LinearLayout audioLayout;
	private NextAudio nextAudio;

	private BroadcastReceiver progressbarReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent serviceIntent) {
			if (!recording) {

				updateUI(serviceIntent);
			} else {

				updateRecUi(serviceIntent);
			}
		}

		private void updateUI(Intent serviceIntent) {
			String counter = serviceIntent.getStringExtra(Constants.COUNTER);
			String mediamax = serviceIntent.getStringExtra(Constants.MEDIA_MAX);
			String strSongEnded = serviceIntent
					.getStringExtra(Constants.SONG_ENDED);
			int seekProgress = Integer.parseInt(counter);

			seekMax = Integer.parseInt(mediamax);
			songEnded = Integer.parseInt(strSongEnded);
			seekBar.setMax(seekMax);
			seekBar.setProgress(seekProgress);

			if (songEnded == 1) {

				stopAudio();
				musicPlaying = false;
				play.setBackgroundResource(R.drawable.media_play);

			}

		}

		private void updateRecUi(Intent serviceIntent) {
			String recCounter = serviceIntent
					.getStringExtra(Constants.RECORDING_COUNTER);
			songTime.setText(recCounter);

		}

	};
	private BroadcastReceiver endSongReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (!recording)
				nextAudio();
			else
				recStop();

		}
	};
	private BroadcastReceiver refreshUI = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			musicPaused = intent.getBooleanExtra(Constants.PAUSED, false);
			repeatAudio = intent.getBooleanExtra(Constants.AUDIO_REPEAT, false);
			shuffleAudio = intent.getBooleanExtra(Constants.AUDIO_SHUFFLE,
					false);
			int audioNumber = intent.getIntExtra(Constants.AUDIO_NUMBER, 0);

			playerMode();

			nextAudio.refreshUi(repeatAudio, shuffleAudio, musicPaused,
					audioNumber);
		}
	};

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnPlay:
				audioLayout.requestFocus();

				if (musicPaused) {
					resumeAudio();
				} else if (recorderMode && !recording) {
					recAudio();
				} else if (recorderMode && recording) {
					recStop();
				} else if (!musicPlaying) {
					playerMode();
					playAudio();
				} else {
					playerMode();
					pauseAudio();
				}
				break;

			case R.id.btnPrev:
				playerMode();
				prevAudio();

				break;
			case R.id.btnStop:
				playerMode();
				if (musicPlaying) {
					stopAudio();

				} else if (recorderMode && recording) {
					recStop();
					nextAudio.stopAllAudio(Constants.BLANK);
				} else {
					nextAudio.stopAllAudio(Constants.BLANK);
				}

				break;
			case R.id.btnNext:
				playerMode();
				superNextAudio();

				break;
			case R.id.btnRepeat:
				if (repeatAudio) {
					repeatAudio = false;
					repeat.setBackgroundResource(R.drawable.media_replay);

				} else {
					repeatAudio = true;
					repeat.setBackgroundResource(R.drawable.media_replay_pressed);
				}
				nextAudio.repeat(repeatAudio);

				break;
			case R.id.btnShuffle:
				if (shuffleAudio) {
					shuffleAudio = false;
					shuffle.setBackgroundResource(R.drawable.media_shuffle);
					nextAudio.shuffle(false);
				} else {
					shuffleAudio = true;
					shuffle.setBackgroundResource(R.drawable.media_shuffle_pressed);
					nextAudio.shuffle(true);
				}

				break;
			case R.id.del:
				if (musicPlaying) {
					stopAudio();

				} else if (recorderMode && recording) {
					recStop();
				}
				OnDeleteFragment.delete(fragId, true);

				break;

			}

		}

	};
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			if (fromUser) {

				int seekPos = seekBar.getProgress();
				seekBarChanged.putExtra(Constants.SEEK_POSITION, seekPos);
				getActivity().sendBroadcast(seekBarChanged);

			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};

	public interface NextAudio {

		public void next(String cur, String flag);

		public void stopAllAudio(String cur);

		public void shuffle(boolean shuffle);

		public void repeat(boolean repeat);

		public void refreshUi(boolean audioRepeat, boolean audioShuffle,
				boolean paused, int audionumber);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		thiscontext = container.getContext();
		v = inflater.inflate(R.layout.note_elem_audio, null);
		seekBar = (SeekBar) v.findViewById(R.id.seekBar1);
		del = (ImageView) v.findViewById(R.id.del);
		play = (ImageView) v.findViewById(R.id.btnPlay);
		play.setBackgroundResource(R.drawable.media_play);

		prev = (ImageView) v.findViewById(R.id.btnPrev);
		stop = (ImageView) v.findViewById(R.id.btnStop);
		next = (ImageView) v.findViewById(R.id.btnNext);
		repeat = (ImageView) v.findViewById(R.id.btnRepeat);
		shuffle = (ImageView) v.findViewById(R.id.btnShuffle);
		repeat.setBackgroundResource(R.drawable.media_replay);

		shuffle.setBackgroundResource(R.drawable.media_shuffle);

		songName = (TextView) v.findViewById(R.id.songTitle);

		songTime = (TextView) v.findViewById(R.id.songTime);

		seekBarChanged = new Intent(Constants.BROADCAST_SEEKBAR);

		hidable = (LinearLayout) v.findViewById(R.id.hidable);

		if (savedInstanceState != null) {

			noteId = savedInstanceState.getString(Constants.NOTE_ID);
			fragId = savedInstanceState.getString(Constants.FRAGMENT_ID);
			audioName = savedInstanceState.getString(Constants.AUDIO_NAME);
			audioPath = savedInstanceState.getString(Constants.AUDIO_PATH);
			musicPlaying = savedInstanceState.getBoolean(
					Constants.MUSIC_PLAYING);

			musicPaused = savedInstanceState.getBoolean(Constants.MUSIC_PAUSED);

			repeatAudio = savedInstanceState.getBoolean(Constants.REPEAT_AUDIO);
			shuffleAudio = savedInstanceState
					.getBoolean(Constants.SHUFFLE_AUDIO);
			recorderMode = savedInstanceState
					.getBoolean(Constants.RECORDER_MODE);
			recording = savedInstanceState.getBoolean(Constants.RECORD, true);

			if (audioPath == null) {
				audioPath = Constants.BLANK;
				audioName = "";
				nextAudio.stopAllAudio(Constants.BLANK);

				loadAudioFromBrowser();
			} else if (audioPath.equalsIgnoreCase(Constants.RECORD)) {

				audioPath = Constants.BLANK;
				audioName = Constants.BLANK;
				nextAudio.stopAllAudio(Constants.BLANK);

				recMode();
			}

			else {
				songName.setText(audioName);
				playerMode();
			}

		} else {
			if (audioPath == null) {
				audioPath = Constants.BLANK;
				audioName = Constants.BLANK;
				nextAudio.stopAllAudio(Constants.BLANK);

				loadAudioFromBrowser();
			} else if (audioPath.equalsIgnoreCase(Constants.RECORD)) {

				audioPath = Constants.BLANK;
				audioName = Constants.BLANK;
				nextAudio.stopAllAudio(Constants.BLANK);

				recMode();
			}

			else {
				songName.setText(audioName);

				playerMode();
			}

		}

		del.setOnClickListener(onClickListener);
		play.setOnClickListener(onClickListener);
		prev.setOnClickListener(onClickListener);
		stop.setOnClickListener(onClickListener);
		next.setOnClickListener(onClickListener);
		repeat.setOnClickListener(onClickListener);
		shuffle.setOnClickListener(onClickListener);

		audioLayout = (LinearLayout) v.findViewById(R.id.noteElemAudio);
		audioLayout.setClickable(true);
		audioLayout.setFocusable(true);
		audioLayout.setFocusableInTouchMode(true);
		audioLayout.requestFocus();

		try {

			audioPlayIntent = new Intent(thiscontext, AudioPlayService.class);
		} catch (Exception e) {
			Log.d("Audio Intent Error3",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

		if (musicPlaying) {
			seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(Constants.BROADCAST_ACTION));
			getActivity().registerReceiver(endSongReceiver,
					new IntentFilter(Constants.BROADCAST_ENDOFSONG));

			if (!musicPaused) {
				play.setBackgroundResource(R.drawable.media_pause);
			}
			songTime.setVisibility(View.GONE);

		}

		if (recorderMode) {

			play.setBackgroundResource(R.drawable.ic_action_rec);
			recMode();

		}
		if (recording) {
			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(Constants.BROADCAST_ACTION));
			play.setBackgroundResource(R.drawable.ic_action_recording);

		}

		if (shuffleAudio)
			shuffle.setBackgroundResource(R.drawable.media_shuffle_pressed);

		if (repeatAudio)
			repeat.setBackgroundResource(R.drawable.media_replay_pressed);

		v.setOnFocusChangeListener(this);
		return v;
	}

	@Override
	public void onPause() {

		if (musicPlaying) {
			seekBar.setOnSeekBarChangeListener(null);
			audioAutonome = new Intent(Constants.BROADCAST_AUTONOME);
			audioAutonome.putExtra(Constants.FLAG, true);
			audioAutonome.putExtra(Constants.AUDIO_REPEAT, repeatAudio);
			audioAutonome.putExtra(Constants.AUDIO_SHUFFLE, shuffleAudio);
			getActivity().sendBroadcast(audioAutonome);
		}
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (NoteActivity.class.isInstance(activity)) {

			sX = NoteActivity.getSaveNote();
			OnDeleteFragment = NoteActivity.getOnDeleteFragment();
			nextAudio = NoteActivity.getNextAudio();
		} else {
			throw new IllegalArgumentException(
					"Activity must implement NextAudio interface ");
		}

		thiscontext = activity.getApplicationContext();

	}

	@Override
	public void onResume() {

		if (musicPlaying) {
			getActivity().registerReceiver(refreshUI,
					new IntentFilter(Constants.BROADCAST_REFRESHUI));
			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(Constants.BROADCAST_ACTION));
			getActivity().registerReceiver(endSongReceiver,
					new IntentFilter(Constants.BROADCAST_ENDOFSONG));

			audioAutonome = new Intent(Constants.BROADCAST_AUTONOME);
			audioAutonome.putExtra(Constants.FLAG, false);
			getActivity().sendBroadcast(audioAutonome);
		}

		super.onResume();
	}

	@Override
	public void onDestroy() {

		try {
			getActivity().unregisterReceiver(refreshUI);
			getActivity().unregisterReceiver(progressbarReceiver);
			getActivity().unregisterReceiver(endSongReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error1",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(Constants.NOTE_ID, noteId);
		if (fragId != null)
			outState.putString(Constants.FRAGMENT_ID, fragId);

		if (audioName != null)
			outState.putString(Constants.AUDIO_NAME, audioName);

		if (audioPath != null)
			outState.putString(Constants.AUDIO_PATH, audioPath);
		if (musicPlaying) {
			outState.putBoolean(Constants.MUSIC_PLAYING, true);

			try {

				getActivity().unregisterReceiver(progressbarReceiver);

			} catch (Exception e) {
				Log.d("Audio Intent Error2", e.getClass().getName() + "  ___  "
						+ e.getMessage());
			}
			try {

				getActivity().unregisterReceiver(endSongReceiver);

			} catch (Exception e) {
				Log.d("Audio Intent Error2", e.getClass().getName() + "  ___  "
						+ e.getMessage());
			}

		}
		if (musicPaused) {
			outState.putBoolean(Constants.MUSIC_PAUSED, true);

		}
		if (repeatAudio) {
			outState.putBoolean(Constants.REPEAT_AUDIO, true);

		}
		if (shuffleAudio) {
			outState.putBoolean(Constants.SHUFFLE_AUDIO, true);

		}
		if (recorderMode) {
			outState.putBoolean(Constants.RECORDER_MODE, true);

		}
		if (recording) {
			outState.putBoolean(Constants.RECORD, true);

		}

	}

	@Override
	public void onDetach() {
		try {

			getActivity().unregisterReceiver(progressbarReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(endSongReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

		super.onDetach();
	}

	private void loadAudioFromBrowser() {

		Intent audioPicker = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(audioPicker, 1);

	}

	public void prevAudio() {
		seekBar.setOnSeekBarChangeListener(null);
		nextAudio.next(fragId, Constants.PREVIOUS);

	}

	protected void resumeAudio() {
		play.setBackgroundResource(R.drawable.media_pause);
		songPause = new Intent(Constants.BROADCAST_PAUSESONG);
		songPause.putExtra(Constants.FLAG, Constants.RESUME);
		musicPaused = true;
		getActivity().sendBroadcast(songPause);
		musicPaused = false;
	}

	public void pauseAudio() {
		play.setBackgroundResource(R.drawable.media_play);
		songPause = new Intent(Constants.BROADCAST_PAUSESONG);
		songPause.putExtra(Constants.FLAG, Constants.PAUSE);
		getActivity().sendBroadcast(songPause);
		musicPaused = true;
	}

	public void refreshPlayAudio(boolean p) {

		seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		if (p)
			play.setBackgroundResource(R.drawable.media_play);
		else
			play.setBackgroundResource(R.drawable.media_pause);
		try {
			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(Constants.BROADCAST_ACTION));
			getActivity().registerReceiver(endSongReceiver,
					new IntentFilter(Constants.BROADCAST_ENDOFSONG));
			musicPlaying = true;

		} catch (Exception e) {
			Log.d("Audio Intent Error1",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

		try {
			getActivity().registerReceiver(refreshUI,
					new IntentFilter(Constants.BROADCAST_REFRESHUI));
		} catch (Exception e) {
			Log.d("Audio Intent Error1",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

	}

	public void nextAudio() {
		seekBar.setOnSeekBarChangeListener(null);
		if (repeatAudio) {
			nextAudio.next(fragId, Constants.REPLAY);
		} else if (shuffleAudio) {
			nextAudio.next(fragId, Constants.SHUFFLE_AUDIO);
		} else {
			nextAudio.next(fragId, Constants.NEXT);
		}
	}

	public void superNextAudio() {
		if (shuffleAudio) {
			nextAudio.next(fragId, Constants.SHUFFLE_AUDIO);
		} else {
			nextAudio.next(fragId, Constants.NEXT);
		}
	}

	public void stopAudio() {
		seekBar.setOnSeekBarChangeListener(null);
		play.setBackgroundResource(R.drawable.media_play);
		try {

			getActivity().stopService((audioPlayIntent));

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(progressbarReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(endSongReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(refreshUI);
		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

		musicPlaying = false;
		playerMode();
		recording = false;
		resetSeekPos();

	}

	public void specialStopAudio() {

		seekBar.setOnSeekBarChangeListener(null);
		musicPlaying = false;
		playerMode();
		recording = false;
		resetSeekPos();
		play.setBackgroundResource(R.drawable.media_play);
		try {

			getActivity().stopService((audioPlayIntent));

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(progressbarReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(endSongReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(refreshUI);
		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

	}

	public void refreshStopAudio() {
		seekBar.setOnSeekBarChangeListener(null);
		if (musicPlaying)
			musicPlaying = false;
		resetSeekPos();
		play.setBackgroundResource(R.drawable.media_play);

		try {

			getActivity().unregisterReceiver(progressbarReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(endSongReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(refreshUI);
		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

	}

	public TreeMap<String, String> saveContent() {

		TreeMap<String, String> content = new TreeMap<String, String>();
		if (audioPath.equalsIgnoreCase(Constants.BLANK)) {
			content.put(Constants.Flags.Cont1.name(), "No Audio");
		} else {
			content.put(Constants.Flags.Cont1.name(), audioPath);
		}
		if (audioName.equalsIgnoreCase(Constants.BLANK)) {
			content.put(Constants.Flags.Cont2.name(), "No Audio");
		} else {
			content.put(Constants.Flags.Cont2.name(), audioName);
		}

		return content;

	}

	public void loadContent(TreeMap<String, String> temp) {

		audioPath = temp.get(Constants.Flags.Cont1.name());
		audioName = temp.get(Constants.Flags.Cont2.name());

	}

	public void loadFragId(String str) {
		fragId = str;
	}

	public void loadShuffleRepeat(boolean sh, boolean rp) {
		shuffleAudio = sh;
		repeatAudio = rp;
		if (rp) {
			repeat.setBackgroundResource(R.drawable.media_replay_pressed);
		}
		if (!rp) {
			repeat.setBackgroundResource(R.drawable.media_replay);
		}

		if (sh) {
			shuffle.setBackgroundResource(R.drawable.media_shuffle_pressed);
		}
		if (!sh) {
			shuffle.setBackgroundResource(R.drawable.media_shuffle);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {

			if (requestCode == 1) {

				Uri audioUri = data.getData();

				if (audioUri != null) {

					String[] filePathColumn = { MediaStore.Audio.Media.DATA };
					Cursor cursor = thiscontext.getContentResolver().query(
							audioUri, filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					audioPath = cursor.getString(columnIndex);
					audioName = audioPath
							.substring(audioPath.lastIndexOf("/") + 1);

					cursor.close();

					songName.setText(audioName);
					sX.saveRun();

				}

			}
		} else {

			OnDeleteFragment.delete(fragId, false);

		}
	}

	public void resetSeekPos() {
		seekBar.setProgress(0);
	}

	/**
	 * This only method should only be used in a separate thread
	 * 
	 * @return
	 */
	public ArrayList<String> loadAudioToService(DB db, Cursor cursor) {

		ArrayList<String> values = new ArrayList<String>();
		db.open();
		cursor = db.getAllNoteData(noteId);
		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndex(DB.COLUMN_TYPE))
					.equalsIgnoreCase(Frags.AudioFragment.name())) {

				values.add(cursor.getString(cursor
						.getColumnIndex(DB.COLUMN_CONT1)));

			}

		}
		cursor.close();
		db.close();

		return values;
	}

	public int getPositionToViewer(ArrayList<String> values, String path) {
		int position = 0;

		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).equals(path)) {
				position = i;
			}
		}

		return position;
	}

	public void loadNoteId(String str) {
		noteId = str;
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		if (arg1) {
			hidable.setVisibility(View.VISIBLE);
		} else {
			hidable.setVisibility(View.GONE);
		}

	}

	public void recMode() {
		recorderMode = true;
		seekBar.setVisibility(View.GONE);
		songTime.setVisibility(View.VISIBLE);

	}

	public void playerMode() {
		recorderMode = false;
		recording = false;
		seekBar.setVisibility(View.VISIBLE);
		songTime.setVisibility(View.GONE);
	}

	public void recStop() {
		try {

			getActivity().stopService((audioPlayIntent));

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(progressbarReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(endSongReceiver);

		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		try {

			getActivity().unregisterReceiver(refreshUI);
		} catch (Exception e) {
			Log.d("Audio Intent Error2",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

		pauseAudio();
		musicPaused = false;
		playerMode();

	}

	public void playAudio() {
		PlayAudio playTask = new PlayAudio();
		playTask.execute();
	}

	private class PlayAudio extends AsyncTask<Void, Void, ArrayList<String>> {
		private DB db = DB.getInstance(thiscontext);
		private Cursor cursor = null;

		@Override
		protected ArrayList<String> doInBackground(Void... params) {

			return loadAudioToService(db, cursor);
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			playAudio(result);

		}

		private void playAudio(ArrayList<String> result) {
			seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
			nextAudio.stopAllAudio(fragId);
			play.setBackgroundResource(R.drawable.media_pause);

			audioPlayIntent.putStringArrayListExtra(Constants.AUDIO_PATH,
					result);
			audioPlayIntent.putExtra(Constants.AUDIO_NUMBER,
					getPositionToViewer(result, audioPath));
			audioPlayIntent.putExtra(Constants.MODE, Constants.PLAY);

			try {
				getActivity().registerReceiver(progressbarReceiver,
						new IntentFilter(Constants.BROADCAST_ACTION));
				getActivity().registerReceiver(endSongReceiver,
						new IntentFilter(Constants.BROADCAST_ENDOFSONG));
				getActivity().registerReceiver(refreshUI,
						new IntentFilter(Constants.BROADCAST_REFRESHUI));
				musicPlaying = true;
				getActivity().startService((audioPlayIntent));
			} catch (Exception e) {
				Log.d("Audio Intent Error1", e.getClass().getName() + "  ___  "
						+ e.getMessage());
			}

		}

	}

	public void recAudio() {
		RecAudio recTask = new RecAudio();
		recTask.execute();
	}

	private class RecAudio extends AsyncTask<Void, Void, ArrayList<String>> {
		private DB db = DB.getInstance(thiscontext);
		private Cursor cursor = null;

		@Override
		protected ArrayList<String> doInBackground(Void... params) {

			return loadAudioToService(db, cursor);
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			recAudio(result);

		}

		private void recAudio(ArrayList<String> result) {
			recording = true;

			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(Constants.BROADCAST_ACTION));

			SimpleDateFormat time = new SimpleDateFormat("_dd_MMMM_HH-MM-ss");
			FileManager fileManager = new FileManager(thiscontext);
			audioName = "VoiceRecording" + time.format(new Date());
			time = null;

			audioPath = fileManager.sdPath + "/" + fileManager.MusicFolder
					+ "/" + audioPath + audioName + ".mp3";
			File f = new File(audioPath);

			try {
				f.createNewFile();
			} catch (IOException e1) {
				Toast.makeText(thiscontext, "Record not created",
						Toast.LENGTH_SHORT).show();
			}

			sX.saveRun();

			nextAudio.stopAllAudio(fragId);
			play.setBackgroundResource(R.drawable.ic_action_recording);
			songName.setText(audioName);

			audioPlayIntent.putStringArrayListExtra(Constants.AUDIO_PATH,
					result);
			audioPlayIntent.putExtra(Constants.AUDIO_NUMBER,
					getPositionToViewer(result, audioPath));

			audioPlayIntent.putExtra(Constants.MODE, Constants.RECORD);

			try {

				getActivity().registerReceiver(progressbarReceiver,
						new IntentFilter(Constants.BROADCAST_ACTION));
				getActivity().registerReceiver(endSongReceiver,
						new IntentFilter(Constants.BROADCAST_ENDOFSONG));
				getActivity().registerReceiver(refreshUI,
						new IntentFilter(Constants.BROADCAST_REFRESHUI));

				getActivity().startService((audioPlayIntent));
			} catch (Exception e) {
				Log.d("Audio Intent Error1", e.getClass().getName() + "  ___  "
						+ e.getMessage());
			}

		}

	}

}
