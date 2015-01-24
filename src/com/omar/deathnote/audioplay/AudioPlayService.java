package com.omar.deathnote.audioplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.omar.deathnote.MainActivity;
import com.omar.deathnote.R;
import com.omar.deathnote.fragments.AudioFragment;

public class AudioPlayService extends Service implements OnCompletionListener,
		OnPreparedListener, OnErrorListener, OnSeekCompleteListener,
		OnInfoListener, OnBufferingUpdateListener {

	static {
		System.loadLibrary("mp3lame");
	}

	AudioRecord recorder;

	MediaPlayer mediaPlayer;
	public String audioPath;
	String sntSeekPos;
	int intSeekPos;
	int mediaPos;
	int mediaMax;
	int audioNumber;
	ArrayList<String> pathList;
	private TelephonyManager telephoneManager;
	private boolean isPausedCall = false;
	private boolean autonome = false;

	boolean recording;

	String counter;
	boolean audioRepeat = false;
	boolean audioShuffle = false;
	boolean paused = false;

	String mode;
	Notification notification;

	private PhoneStateListener phoneStateListener;

	private final Handler handler = new Handler();
	private static int songEnded;
	public static final String BROADCAST_ACTION = "com.omar.deathnote.audioplay.progressbar";
	public static final String BROADCAST_ENDOFSONG = "com.omar.deathnote.audioplay.endofsong";
	public static final String BROADCAST_REFRESHUI = "com.omar.deathnote.audioplay.refreshui";
	public static final String BROADCAST_NOTIFIC = "com.omar.deathnote.audioplay.notif";

	Intent seekIntent = new Intent(BROADCAST_ACTION);
	Intent endSongIntent = new Intent(BROADCAST_ENDOFSONG);
	Intent refreshUi = new Intent(BROADCAST_REFRESHUI);

	BroadcastReceiver pauseSongReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String flag = intent.getStringExtra("flag");
		/*	Log.d("flag", flag);*/
			switch (flag) {
			case "destroy":

				stopSelf();
			case "pause":
				if (!recording) {
					pauseMedia();
					refreshUi();
					initNotification();
				} else {
					recordStop();
				}
				break;
			case "resume":
				if (!recording) {
					playMedia();
					refreshUi();
					initNotification();
				} else {
					recordStop();
				}
				break;
			}

		}
	};
	BroadcastReceiver runningAutonome = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			initNotification();
			autonome = intent.getBooleanExtra("flag", false);
			/*Log.d("autonome  --> ", String.valueOf(autonome));*/
			if (autonome) {
				audioRepeat = intent.getBooleanExtra("audioRepeat", false);
				audioShuffle = intent.getBooleanExtra("audioShuffle", false);
			} else {
				unregisterReceiver(broadcastReceiver);

				registerReceiver(broadcastReceiver, new IntentFilter(
						AudioFragment.BROADCAST_SEEKBAR));

			}

		}
	};
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateSeekPos(intent);

		}

	};
	public static final String BROADCAST_NOTIF = "com.omar.deathnote.audioplay.notif";

	BroadcastReceiver notifReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!recording) {

				String todo = intent.getStringExtra("todo");
		/*		Log.d("notif action recieved", todo);*/
				switch (todo) {

				case "playpause":

					if (paused) {
						paused = false;

						playMedia();
						initNotification();

					} else {

						pauseMedia();
						initNotification();
					}
					break;
				case "prev":
					paused = false;
					prev();
					initNotification();
					break;
				case "next":
					paused = false;
					next();
					initNotification();
					break;
				}
				refreshUi();
			} else {
				recordStop();
				sendBroadcast(endSongIntent);

			}

		}

	};

	public static final int NOTIFICATION_ID = 1;

	final int mSampleRate = 16000;
	FileOutputStream output;
	int readSize;

	byte[] mp3buffer;

	final int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate,
			AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		registerReceiver(runningAutonome, new IntentFilter(
				AudioFragment.BROADCAST_AUTONOME));

/*		Log.d("start ===>", "on start command");*/
		telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneStateListener = new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {

				case TelephonyManager.CALL_STATE_OFFHOOK:

				case TelephonyManager.CALL_STATE_RINGING:
					if (mediaPlayer != null) {

						pauseMedia();
						isPausedCall = true;
					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					if (mediaPlayer != null) {

						if (isPausedCall) {
							isPausedCall = false;
							playMedia();

						}

					}

					break;

				}

				super.onCallStateChanged(state, incomingNumber);
			}

		};
		telephoneManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		pathList = intent.getExtras().getStringArrayList("audioPath");

		audioNumber = intent.getExtras().getInt("audioNumber");

	/*	Log.d("audionumber --->", String.valueOf(audioNumber));*/
		audioPath = pathList.get(audioNumber);

		mode = intent.getExtras().getString("mode");
		File f = new File(audioPath);
		if (mode.equalsIgnoreCase("rec")) {
		/*	Log.d("service ======>", "new audio rec");*/
			recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
					mSampleRate, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);

			recordStart();
		} else {
			/*Log.d("service ======>", "new media player");*/
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnSeekCompleteListener(this);
			mediaPlayer.setOnInfoListener(this);
			mediaPlayer.setOnBufferingUpdateListener(this);

			if (!mediaPlayer.isPlaying()) {

				try {
					if (f.exists()) {
						mediaPlayer.setDataSource(audioPath);
						mediaPlayer.prepareAsync();
						registerReceiver(broadcastReceiver, new IntentFilter(
								AudioFragment.BROADCAST_SEEKBAR));
						setupHandler();
					} else {

						Toast.makeText(
								getBaseContext(),
								audioPath.substring(audioPath.lastIndexOf("/") + 1)
										+ " does not exist", Toast.LENGTH_LONG)
								.show();

						stopMedia();

					}

				} catch (IllegalArgumentException | SecurityException
						| IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		initNotification();
		return START_STICKY;
	}

	private void setupHandler() {
		handler.removeCallbacks(sendUpdatesToUi);
		handler.postDelayed(sendUpdatesToUi, 100);
		/* Log.d("post handler", "handler"); */

	}

	private Runnable sendUpdatesToUi = new Runnable() {
		public void run() {

			LogMediaPosition();
			handler.postDelayed(this, 250);
			/* Log.d("post handler2", "handler"); */
		}

		private void LogMediaPosition() {
			if (mediaPlayer.isPlaying()) {

				mediaPos = mediaPlayer.getCurrentPosition();
				mediaMax = mediaPlayer.getDuration();
			/*	Log.d("send ====>>", "seekIntent");*/
				seekIntent.putExtra("counter", String.valueOf(mediaPos));
				seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
				seekIntent.putExtra("song_ended", String.valueOf(songEnded));
				sendBroadcast(seekIntent);
				/* Log.d("sending broadcast", "boring"); */
			}

		}

	};

	protected void updateSeekPos(Intent intent) {
/*		Log.d("updateSeekPos", "updateSeekPos");*/
		int seekPos = intent.getIntExtra("seekpos", 0);
		if (mediaPlayer.isPlaying()) {

			handler.removeCallbacks(sendUpdatesToUi);
			mediaPlayer.seekTo(seekPos);
			setupHandler();
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		mode = "";

		/*
		 * registerReceiver(runningAutonome, new IntentFilter(
		 * AudioFragment.BROADCAST_AUTONOME));
		 */
		/* registerReceiver(destroy, new IntentFilter(Note.BROADCAST_DESTROY)); */

	}

	@Override
	public void onDestroy() {

		try {
			unregisterReceiver(broadcastReceiver);

		} catch (Exception e) {
			Log.d("Audio Service Error1", e.getClass().getName() + "  ___  "
					+ e.getMessage());
		}
		try {

			unregisterReceiver(pauseSongReceiver);

		} catch (Exception e) {
			Log.d("Audio Service Error1", e.getClass().getName() + "  ___  "
					+ e.getMessage());
		}
		try {

			unregisterReceiver(runningAutonome);
		} catch (Exception e) {
			Log.d("Audio Service Error1", e.getClass().getName() + "  ___  "
					+ e.getMessage());
		}

		Log.d(" destroy ====>>", "destroy");

		if (mediaPlayer != null) {
			Log.d(" destroy ====>>", "player");
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();

			}

			mediaPlayer.release();
			handler.removeCallbacks(sendUpdatesToUi);
		}

		if (recording) {
			Log.d(" destroy ====>>", "recorder");

			recordStop();
		}
		;

		if (phoneStateListener != null) {
			telephoneManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
		/*Log.d("audio service =======+++>", "Stop self");*/
		cancelNotification();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer arg0) {
		if (!mediaPlayer.isPlaying()) {

			playMedia();
		}

	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		playMedia();
	}

	public void playMedia() {

		if (mediaPlayer != null) {
			if (!mediaPlayer.isPlaying()) {
				paused = false;
				mediaPlayer.start();
				registerReceiver(pauseSongReceiver, new IntentFilter(
						AudioFragment.BROADCAST_PAUSESONG));
			}
		}

	}

	public void stopMedia() {
	/*	Log.d("media ====>>", "stop");*/
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				stopSelf();

			}

		}
		if (recorder != null) {

			recordStop();
			stopSelf();
		}
	}

	public void specialStop() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();

			}
			mediaPlayer.release();
			handler.removeCallbacks(sendUpdatesToUi);
		}

		if (recording) {
			recordStop();
		}

		try {
			unregisterReceiver(broadcastReceiver);

		} catch (Exception e) {
			Log.d("Audio Service Error1", e.getClass().getName() + "  ___  "
					+ e.getMessage());
		}
		try {

			unregisterReceiver(pauseSongReceiver);

		} catch (Exception e) {
			Log.d("Audio Service Error1", e.getClass().getName() + "  ___  "
					+ e.getMessage());
		}
		try {

			unregisterReceiver(runningAutonome);
		} catch (Exception e) {
			Log.d("Audio Service Error1", e.getClass().getName() + "  ___  "
					+ e.getMessage());
		}

		if (phoneStateListener != null) {
			telephoneManager.listen(phoneStateListener,
					PhoneStateListener.LISTEN_NONE);
		}
	}

	public void specialStart() {

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		registerReceiver(broadcastReceiver, new IntentFilter(
				AudioFragment.BROADCAST_SEEKBAR));
		registerReceiver(runningAutonome, new IntentFilter(
				AudioFragment.BROADCAST_AUTONOME));
		registerReceiver(pauseSongReceiver, new IntentFilter(
				AudioFragment.BROADCAST_PAUSESONG));
	}

	protected void pauseMedia() {
		if (mediaPlayer.isPlaying()) {
			paused = true;
			mediaPlayer.pause();
		}

	}

	@Override
	public void onCompletion(MediaPlayer arg0) {

		if (autonome) {
			next();

		} else {
			sendBroadcast(endSongIntent);

			stopMedia();

		}

	}

	private void initNotification() {
		registerReceiver(notifReceiver, new IntentFilter(BROADCAST_NOTIFIC));

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = audioPath.substring(audioPath
				.lastIndexOf("/") + 1);
		long when = System.currentTimeMillis();
		Intent prev = new Intent(BROADCAST_NOTIFIC);
		prev.putExtra("todo", "prev");
		Intent playpause = new Intent(BROADCAST_NOTIFIC);
		playpause.putExtra("todo", "playpause");
		Intent next = new Intent(BROADCAST_NOTIFIC);
		next.putExtra("todo", "next");

		PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0,
				prev, 0);
		PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 1,
				playpause, 0);
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 2,
				next, 0);

		Intent notificationIntent = new Intent(getApplicationContext(),
				com.omar.deathnote.Note.class);

		PendingIntent intentBack = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		if (recording) {

			notification = new NotificationCompat.Builder(this)
					.setContentText(tickerText)
					.setPriority(2)
					.setSmallIcon(icon)
					.setOngoing(true)
					.setContentTitle("Voice Recording")
					.setOnlyAlertOnce(true)
					.setContentIntent(intentBack)

					.addAction(R.drawable.ic_action_stop,
							"Stop voice recodring", pausePendingIntent) // #1

					.setWhen(when).build();

			notification.flags = NotificationCompat.PRIORITY_MAX;

			mNotificationManager.notify(NOTIFICATION_ID, notification);

		}

		else if (paused) {
			notification = new NotificationCompat.Builder(this)
					.setContentText(tickerText)
					.setSmallIcon(icon)
					.setPriority(2)
					.setOngoing(true)
					.setContentTitle("Death Note audio")
					.setOnlyAlertOnce(true)
					.setContentIntent(intentBack)
					.addAction(R.drawable.ic_action_previous, "",
							prevPendingIntent) // #0
					.addAction(R.drawable.ic_action_play, "",
							pausePendingIntent) // #1
					.addAction(R.drawable.ic_action_next, "", nextPendingIntent) // #2

					.setWhen(when).build();

			notification.flags = NotificationCompat.PRIORITY_MAX;

			mNotificationManager.notify(NOTIFICATION_ID, notification);
		} else {
			notification = new NotificationCompat.Builder(this)
					.setContentText(tickerText)
					.setSmallIcon(icon)
					.setPriority(2)
					.setContentTitle("Death Note audio")
					.setOnlyAlertOnce(true)
					.setContentIntent(intentBack)
					.setOngoing(true)
					.addAction(R.drawable.ic_action_previous, "",
							prevPendingIntent)
					// #0
					.addAction(R.drawable.ic_action_pause, "",
							pausePendingIntent)
					// #1
					.addAction(R.drawable.ic_action_next, "", nextPendingIntent)
					// #2
					.addAction(R.drawable.ic_action_previous, "",
							nextPendingIntent)

					.setWhen(when).build();

			notification.flags = Notification.PRIORITY_MAX;

			mNotificationManager.notify(NOTIFICATION_ID, notification);
		}
	}

	private void cancelNotification() {
		try {
			unregisterReceiver(notifReceiver);
		} catch (Exception e) {
			Log.d("Audio Service Error1", e.getClass().getName() + "  ___  "
					+ e.getMessage());
		}
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(NOTIFICATION_ID);

	}

	public void next() {
		specialStop();
		specialStart();

		if (!audioRepeat) {

			if (!audioShuffle) {
				if (pathList.size() > audioNumber + 1) {
					audioNumber++;

				} else {
					audioNumber = 0;

				}
		/*		Log.d("keys", "audioNumber ");*/

			} else {
				audioNumber = MainActivity.randInt(0, (pathList.size() - 1));
			/*	Log.d("keys", "audioNumber ");*/

			}
		}
		audioPath = pathList.get(audioNumber);

		try {
			mediaPlayer.setDataSource(audioPath);
			mediaPlayer.prepareAsync();

		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void prev() {
		specialStop();
		specialStart();

		if (!audioRepeat) {

			if (!audioShuffle) {
				if (pathList.size() <= 1) {
					audioNumber = pathList.size() - 1;

				} else {
					audioNumber = 0;

				}
			/*	Log.d("keys", "audioNumber ");*/

			} else {
				audioNumber = MainActivity.randInt(0, (pathList.size() - 1));
		/*		Log.d("keys", "audioNumber ");*/

			}
		}
		audioPath = pathList.get(audioNumber);

		try {
			mediaPlayer.setDataSource(audioPath);
			mediaPlayer.prepareAsync();

		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * protected void playRecorded() { File f = new File(audioPath); mode = "";
	 * Log.d("service ======>", "play"); mediaPlayer = new MediaPlayer();
	 * mediaPlayer.setOnCompletionListener(this);
	 * mediaPlayer.setOnPreparedListener(this);
	 * mediaPlayer.setOnErrorListener(this);
	 * mediaPlayer.setOnSeekCompleteListener(this);
	 * mediaPlayer.setOnInfoListener(this);
	 * mediaPlayer.setOnBufferingUpdateListener(this);
	 * 
	 * if (!mediaPlayer.isPlaying()) {
	 * 
	 * try { if (f.exists()) { mediaPlayer.setDataSource(audioPath);
	 * mediaPlayer.prepareAsync(); registerReceiver(broadcastReceiver, new
	 * IntentFilter( AudioFragment.BROADCAST_SEEKBAR)); setupHandler(); } else {
	 * 
	 * Toast.makeText( getBaseContext(),
	 * audioPath.substring(audioPath.lastIndexOf("/") + 1) + " does not exist",
	 * Toast.LENGTH_LONG) .show();
	 * 
	 * stopMedia();
	 * 
	 * }
	 * 
	 * } catch (IllegalArgumentException | SecurityException |
	 * IllegalStateException | IOException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * } mediaPlayer.start();
	 * 
	 * mediaPlayer.pause(); paused = true; initNotification();
	 * 
	 * }
	 */

	public void recordStart() {
/*		Log.d("recording ==== >>   ", audioPath);*/

		recording = true;
		registerReceiver(pauseSongReceiver, new IntentFilter(
				AudioFragment.BROADCAST_PAUSESONG));
		recThread();

	}

	public void recordStop() {
		recording = false;

	}

	static {
		System.loadLibrary("mp3lame");
	}

	public void recThread() {

		new Thread() {
			@Override
			public void run() {

				android.os.Process
						.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

				File outFile = new File(audioPath);
				if (outFile.exists()) {
					outFile.delete();
				}
				try {
					outFile.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
						mSampleRate, AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 4);

				short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 5]; // SampleRate[Hz]
																			// *
																			// 16bit
																			// *
																			// Mono
																			// *
																			// 5sec
				byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

				output = null;
				try {
					output = new FileOutputStream(new File(audioPath));
				} catch (FileNotFoundException e) {
					Log.e("audio recorder ===>>", "file not found");

				}
				SimpleLame.init(mSampleRate, 1, mSampleRate, 192);

			/*	Log.d("audiorecorder ====>", "record start");*/
				recorder.startRecording();
				int recordingState = recorder.getRecordingState();
				/*Log.d("audiorecorder ====>", "recordingState = "
						+ recordingState);*/

				int readSize = 0;

				String recCoutner = getResources().getString(
						R.string.rec_countet);

				RecCounter rC = new RecCounter();

				while (recording) {
					readSize = recorder.read(buffer, 0, minBufferSize);
					int encResult = SimpleLame.encode(buffer, buffer, readSize,
							mp3buffer);
					try {
						output.write(mp3buffer, 0, encResult);
						/*
						 * Log.d("recorder ===>", "writing buffer size = " +
						 * String.valueOf(encResult));
						 */

						recCoutner = rC.c();
						seekIntent.putExtra("recCounter", recCoutner);
						/*Log.d("send ====>>", "seekIntent");*/
						sendBroadcast(seekIntent);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

		/*		Log.d("record ====>>", "stop");*/

				int flushResult = SimpleLame.flush(mp3buffer);

				if (flushResult != 0) {
					try {
						output.write(mp3buffer, 0, flushResult);
						/*Log.d("recorder ===>",
								"writing flushresult= "
										+ String.valueOf(flushResult));*/
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				recorder.stop();
				recorder.release();

				SimpleLame.close();

				mode = "";

				/* stopSelf(); */
			}
		}.start();
	}

	public void refreshUi() {
		refreshUi = new Intent(BROADCAST_REFRESHUI);
		refreshUi.putExtra("audioRepeat", audioRepeat);
		refreshUi.putExtra("audioShuffle", audioShuffle);
		/*Log.d("audionumber --->", String.valueOf(audioNumber));*/
		refreshUi.putExtra("audioNumber", audioNumber);
		refreshUi.putExtra("paused", paused);

		sendBroadcast(refreshUi);
	/*	Log.d("sending broadcast ======>", " refresh UI");*/
	}

	public class RecCounter {

		String count = getResources().getString(R.string.rec_countet);
		int hours = 0;
		int mins = 0;
		int secs = 0;
		int l = 0;

		public String c() {

			if (l == 9) {
				count = preC();
				l = 0;
			} else {
				l++;
			}
			/* Log.d("connuter ==>", count); */
			return count;
		}

		public String preC() {
			String cSmall = "";

			if (mins == 60) {
				hours++;
				mins = 0;
			}

			if (hours < 10)
				cSmall = "0" + String.valueOf(hours) + " : ";
			else
				cSmall = String.valueOf(hours) + " : ";

			if (secs == 60) {
				mins++;
				secs = 0;
			}

			if (mins < 10)
				cSmall += "0" + String.valueOf(mins) + " : ";
			else
				cSmall += String.valueOf(mins) + " : ";

			secs++;
			if (secs < 10)
				cSmall += "0" + String.valueOf(secs);
			else
				cSmall += String.valueOf(secs);

		/*	Log.d("connuter ==>", cSmall);*/
			return cSmall;
		}
	}

}