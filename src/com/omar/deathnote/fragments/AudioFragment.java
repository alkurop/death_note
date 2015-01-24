package com.omar.deathnote.fragments;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
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

import com.omar.deathnote.DB;
import com.omar.deathnote.FileManager;
import com.omar.deathnote.R;
import com.omar.deathnote.audioplay.AudioPlayService;
import com.omar.deathnote.utility.OnDeleteFragment;
import com.omar.deathnote.utility.SaveNote;

public class AudioFragment extends Fragment implements OnDeleteFragment,
		SaveNote, OnFocusChangeListener {

	public static final String BROADCAST_SEEKBAR = "com.omar.deathnote.fragments.audiofragment.seekbar";
	public static final String BROADCAST_PAUSESONG = "com.omar.deathnote.fragments.audiofragment.pausesong";
	public static final String BROADCAST_AUTONOME = "com.omar.deathnote.fragments.audiofragment.autonome";

	Intent seekBarChanged, songPause, audioAutonome, audioPlayIntent;

	OnDeleteFragment OnDeleteFragment;
	String audioName, audioPath, fragId;

	Context thiscontext;
	SaveNote sX;

	public boolean musicPlaying, recording, recorderMode, musicPaused,
			repeatAudio, shuffleAudio;

	ImageView del, play, prev, stop, next, repeat, shuffle;
	SeekBar seekBar;
	LinearLayout hidable;
	TextView songName, songTime;
	View v;

	String noteId;
	DB db;
	Cursor cursor;

	LinearLayout audioLayout;
	NextAudio nextAudio;
	BroadcastReceiver progressbarReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent serviceIntent) {
			if (!recording) {
				/* Log.d("broadcastReceiver", "updateUI"); */
				updateUI(serviceIntent);
			} else {
				/* Log.d("broadcastReceiver", "updateRecUi"); */
				updateRecUi(serviceIntent);
			}
		}

		private void updateUI(Intent serviceIntent) {
			String counter = serviceIntent.getStringExtra("counter");
			String mediamax = serviceIntent.getStringExtra("mediamax");
			String strSongEnded = serviceIntent.getStringExtra("song_ended");
			int seekProgress = Integer.parseInt(counter);
			seekMax = Integer.parseInt(mediamax);

			songEnded = Integer.parseInt(strSongEnded);

			seekBar.setMax(seekMax);
			seekBar.setProgress(seekProgress);

			/*
			 * Log.d("songEnded", "updating "+ String.valueOf(songEnded));
			 * Log.d("seekMax", "updating "+ String.valueOf(seekMax));
			 */
			/* Log.d("update", "updating"+ String.valueOf(seekProgress)); */
			if (songEnded == 1) {

				stopAudio();
				musicPlaying = false;
				play.setBackgroundResource(R.drawable.media_play);
				/* Log.d("update", "song ended"); */
			}

		}

		private void updateRecUi(Intent serviceIntent) {
			String recCounter = serviceIntent.getStringExtra("recCounter");
			/* Log.d("set Song Time ===>", "recCounter"); */
			songTime.setText(recCounter);

		}

	};

	BroadcastReceiver endSongReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			/* Log.d("end song", "received"); */
			if (!recording)
				nextAudio();
			else
				recStop();

		}
	};
	BroadcastReceiver refreshUI = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			musicPaused = intent.getBooleanExtra("paused", false);
			/* Log.d(" musicPaused ======>", String.valueOf(musicPaused)); */
			repeatAudio = intent.getBooleanExtra("audioRepeat", false);
			shuffleAudio = intent.getBooleanExtra("audioShuffle", false);
			int audioNumber = intent.getIntExtra("audioNumber", 0);
			/*
			 * Log.d("resrech ui", "recieved"); Log.d("audionumber --->",
			 * String.valueOf(audioNumber));
			 */

			playerMode();

			nextAudio.refreshUi(repeatAudio, shuffleAudio, musicPaused,
					audioNumber);
		}
	};

	OnClickListener onClickListener = new OnClickListener() {

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
					nextAudio.stopAllAudio("");
				} else {
					nextAudio.stopAllAudio("");
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

	OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

			if (fromUser) {

				int seekPos = seekBar.getProgress();
				seekBarChanged.putExtra("seekpos", seekPos);
				getActivity().sendBroadcast(seekBarChanged);
				/* Log.d("seekbar changed", "cahnged"); */
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};

	int seekMax;
	static int songEnded = 0;

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

		seekBarChanged = new Intent(BROADCAST_SEEKBAR);

		hidable = (LinearLayout) v.findViewById(R.id.hidable);

		if (savedInstanceState != null) {
			fragId = savedInstanceState.getString("fragId");
			audioName = savedInstanceState.getString("audioName");
			audioPath = savedInstanceState.getString("audioPath");

			songName.setText(audioName);

			musicPlaying = savedInstanceState.getBoolean("musicPlaying");
			musicPaused = savedInstanceState.getBoolean("musicPaused");
			repeatAudio = savedInstanceState.getBoolean("repeatAudio");
			shuffleAudio = savedInstanceState.getBoolean("shuffleAudio");
			noteId = savedInstanceState.getString("noteId");

			recorderMode = savedInstanceState.getBoolean("recorderMode");
			recording = savedInstanceState.getBoolean("recording");

		} else {
			if (audioPath == null) {
				audioPath = "";
				audioName = "";
				nextAudio.stopAllAudio("");

				loadAudioFromBrowser();
			} else if (audioPath.equalsIgnoreCase("rec")) {

				audioPath = "";
				audioName = "";
				nextAudio.stopAllAudio("");

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
					new IntentFilter(AudioPlayService.BROADCAST_ACTION));
			getActivity().registerReceiver(endSongReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ENDOFSONG));
			/*
			 * Log.d("reciever", "registered"); Log.d("musicplaying", "true");
			 */
			if (!musicPaused) {
				play.setBackgroundResource(R.drawable.media_pause);
			}
			songTime.setVisibility(v.GONE);

		}

		if (recorderMode) {

			/* Log.d("Recodrer mode ===>>>>", "true"); */
			play.setBackgroundResource(R.drawable.ic_action_rec);
			recMode();

		}
		if (recording) {
			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ACTION));
			play.setBackgroundResource(R.drawable.ic_action_recording);

		}

		if (shuffleAudio)
			shuffle.setBackgroundResource(R.drawable.media_shuffle_pressed);

		if (repeatAudio)
			repeat.setBackgroundResource(R.drawable.media_replay_pressed);

		db = new DB(thiscontext);
		v.setOnFocusChangeListener(this);
		return v;
	}

	private void loadAudioFromBrowser() {

		Intent audioPicker = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(audioPicker, 1);

	}

	public void prevAudio() {
		seekBar.setOnSeekBarChangeListener(null);
		nextAudio.next(fragId, "prev");

	}

	protected void resumeAudio() {
		play.setBackgroundResource(R.drawable.media_pause);
		/*
		 * try {
		 * 
		 * getActivity().stopService((audioPlayIntent)); } catch (Exception e) {
		 * Log.d("Audio Intent Error4", e.getClass().getName() + "  ___  " +
		 * e.getMessage()); }
		 */
		songPause = new Intent(BROADCAST_PAUSESONG);
		songPause.putExtra("flag", "resume");
		musicPaused = true;
		getActivity().sendBroadcast(songPause);
		musicPaused = false;
	}

	public void pauseAudio() {
		play.setBackgroundResource(R.drawable.media_play);
		/*
		 * try {
		 * 
		 * getActivity().stopService((audioPlayIntent)); } catch (Exception e) {
		 * Log.d("Audio Intent Error4", e.getClass().getName() + "  ___  " +
		 * e.getMessage()); }
		 */
		songPause = new Intent(BROADCAST_PAUSESONG);
		songPause.putExtra("flag", "pause");

		getActivity().sendBroadcast(songPause);
		musicPaused = true;
	}

	public void playAudio() {
		seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		/* audioLayout.requestFocus(); */
		nextAudio.stopAllAudio(fragId);
		play.setBackgroundResource(R.drawable.media_pause);

		audioPlayIntent.putStringArrayListExtra("audioPath",
				loadAudioToService());
		audioPlayIntent.putExtra("audioNumber",
				getPositionToViewer(loadAudioToService(), audioPath));
		audioPlayIntent.putExtra("mode", "play");
		try {
			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ACTION));
			getActivity().registerReceiver(endSongReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ENDOFSONG));
			getActivity().registerReceiver(refreshUI,
					new IntentFilter(AudioPlayService.BROADCAST_REFRESHUI));
			musicPlaying = true;
			getActivity().startService((audioPlayIntent));
		} catch (Exception e) {
			Log.d("Audio Intent Error1",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

		/* Log.d("reciever", "registered"); */
	}

	public void recAudio() {
		recording = true;

		getActivity().registerReceiver(progressbarReceiver,
				new IntentFilter(AudioPlayService.BROADCAST_ACTION));

		SimpleDateFormat time = new SimpleDateFormat("_dd_MMMM_HH-MM-ss");
		FileManager fileManager = new FileManager();
		audioName = "VoiceRecording" + time.format(new Date());
		time = null;
		/* Log.d("audiorecording  title =======>", audioName); */
		audioPath = fileManager.sdPath + "/" + fileManager.MusicFolder + "/"
				+ audioPath + audioName + ".mp3";
		File f = new File(audioPath);
		/* Log.d("audiorecording  path =======>", audioPath); */
		try {
			f.createNewFile();
		} catch (IOException e1) {
			Toast.makeText(thiscontext, "Record not created",
					Toast.LENGTH_SHORT);
		}

		sX.saveRun();

		/* audioLayout.requestFocus(); */
		nextAudio.stopAllAudio(fragId);
		play.setBackgroundResource(R.drawable.ic_action_recording);
		songName.setText(audioName);

		audioPlayIntent.putStringArrayListExtra("audioPath",
				loadAudioToService());
		audioPlayIntent.putExtra("audioNumber",
				getPositionToViewer(loadAudioToService(), audioPath));

		audioPlayIntent.putExtra("mode", "rec");

		try {

			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ACTION));
			getActivity().registerReceiver(endSongReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ENDOFSONG));
			getActivity().registerReceiver(refreshUI,
					new IntentFilter(AudioPlayService.BROADCAST_REFRESHUI));

			getActivity().startService((audioPlayIntent));
		} catch (Exception e) {
			Log.d("Audio Intent Error1",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

		/* Log.d("reciever", "registered"); */

	}

	public void refreshPlayAudio(boolean p) {
		/* audioLayout.requestFocus(); */
		seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		if (p)
			play.setBackgroundResource(R.drawable.media_play);
		else
			play.setBackgroundResource(R.drawable.media_pause);
		try {
			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ACTION));
			getActivity().registerReceiver(endSongReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ENDOFSONG));
			musicPlaying = true;

		} catch (Exception e) {
			Log.d("Audio Intent Error1",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}

		try {
			getActivity().registerReceiver(refreshUI,
					new IntentFilter(AudioPlayService.BROADCAST_REFRESHUI));
		} catch (Exception e) {
			Log.d("Audio Intent Error1",
					e.getClass().getName() + "  ___  " + e.getMessage());
		}
		/* Log.d("reciever", "registered"); */
	}

	public void nextAudio() {
		seekBar.setOnSeekBarChangeListener(null);
		if (repeatAudio) {
			nextAudio.next(fragId, "replay");
		} else if (shuffleAudio) {
			nextAudio.next(fragId, "shuffle");
		} else {
			nextAudio.next(fragId, "next");
		}
	}

	public void superNextAudio() {
		if (shuffleAudio) {
			nextAudio.next(fragId, "shuffle");
		} else {
			nextAudio.next(fragId, "next");
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
		/* Log.d("special stop", "stopping frag    -- " + fragId); */
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {

			sX = (SaveNote) activity;
			OnDeleteFragment = (OnDeleteFragment) activity;
			nextAudio = (NextAudio) activity;
			thiscontext = ((Activity) OnDeleteFragment).getApplicationContext();
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement  ");
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		/* Log.d("onSaveInstanceState", ""); */
		outState.putString("noteId", noteId);
		if (fragId != null)
			outState.putString("fragId", fragId);

		if (audioName != null)
			outState.putString("audioName", audioName);

		if (audioName != null)
			outState.putString("audioPath", audioPath);
		if (musicPlaying) {
			outState.putBoolean("musicPlaying", true);
			/* Log.d("musicplaying", "true"); */

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
			outState.putBoolean("musicPaused", true);
			/* Log.d("musicPaused", "true"); */

		}
		if (repeatAudio) {
			outState.putBoolean("repeatAudio", true);
			/* Log.d("repeatAudio", "true"); */

		}
		if (shuffleAudio) {
			outState.putBoolean("shuffleAudio", true);
			/* Log.d("shuffleAudio", "true"); */

		}
		if (recorderMode) {
			outState.putBoolean("recorderMode", true);
			/* Log.d("recorderMode", "true"); */

		}
		if (recording) {
			outState.putBoolean("recording", true);
			/* Log.d("recording", "true"); */

		}

	}

	public TreeMap<String, String> saveContent() {

		TreeMap<String, String> content = new TreeMap<String, String>();
		if (audioPath.equalsIgnoreCase("")) {
			content.put("cont1", "No Audio");
		} else {
			content.put("cont1", audioPath);
		}
		if (audioName.equalsIgnoreCase("")) {
			content.put("cont2", "No Audio");
		} else {
			content.put("cont2", audioName);
		}

		return content;

	}

	public void loadContent(TreeMap<String, String> temp) {

		audioPath = temp.get("cont1");
		audioName = temp.get("cont2");

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
	public void delete(String s, boolean dialog) {

	}

	@Override
	public void saveRun() {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {

			if (requestCode == 1) {

				Uri audioUri = data.getData();
				/* Log.d("uri", audioUri.toString()); */
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
			/* Log.d("de;eteig =======>", "audio"); */
			OnDeleteFragment.delete(fragId, false);

		}
	}

	// TODO Auto-generated method stub

	public void resetSeekPos() {
		seekBar.setProgress(0);
	}

	public ArrayList<String> loadAudioToService() {

		ArrayList<String> values = new ArrayList<String>();
		db.open();
		cursor = db.getAllNoteData(noteId);
		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndex(db.COLUMN_TYPE))
					.equalsIgnoreCase("AudioFragment")) {

				values.add(cursor.getString(cursor
						.getColumnIndex(db.COLUMN_CONT1)));

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
		/* Log.d("curretn image position", String.valueOf(position)); */
		return position;
	}

	public void loadNoteId(String str) {
		noteId = str;
	}

	@Override
	public void onPause() {
		/* Log.d("onpause", ""); */
		if (musicPlaying) {
			seekBar.setOnSeekBarChangeListener(null);
			audioAutonome = new Intent(BROADCAST_AUTONOME);
			audioAutonome.putExtra("flag", true);
			audioAutonome.putExtra("audioRepeat", repeatAudio);
			audioAutonome.putExtra("audioShuffle", shuffleAudio);
			getActivity().sendBroadcast(audioAutonome);
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		/* Log.d("onresume", ""); */
		if (musicPlaying) {
			getActivity().registerReceiver(refreshUI,
					new IntentFilter(AudioPlayService.BROADCAST_REFRESHUI));
			getActivity().registerReceiver(progressbarReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ACTION));
			getActivity().registerReceiver(endSongReceiver,
					new IntentFilter(AudioPlayService.BROADCAST_ENDOFSONG));
			audioAutonome = new Intent(BROADCAST_AUTONOME);
			audioAutonome.putExtra("flag", false);
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
	public void onFocusChange(View arg0, boolean arg1) {
		if (arg1) {
			hidable.setVisibility(v.VISIBLE);
		} else {
			hidable.setVisibility(v.GONE);
		}

	}

	public void recMode() {
		recorderMode = true;
		seekBar.setVisibility(v.GONE);
		songTime.setVisibility(v.VISIBLE);

	}

	public void playerMode() {
		recorderMode = false;
		recording = false;
		seekBar.setVisibility(v.VISIBLE);
		songTime.setVisibility(v.GONE);
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

}
