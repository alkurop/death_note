package com.omar.deathnote.audioplay_old;

import android.annotation.SuppressLint;
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
import android.media.MediaPlayer.OnCompletionListener;
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

import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.omar.deathnote.notes.ContentActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AudioPlayService extends Service implements OnCompletionListener,
        OnPreparedListener, OnSeekCompleteListener {


    public static final int NOTIFICATION_ID = 1;

    private final int mSampleRate = 16000;
    private final int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    private static int songEnded;

    private String audioPath;
    private int mediaPos;
    private int mediaMax;
    private int audioNumber;
    private ArrayList<String> pathList;
    private TelephonyManager telephoneManager;
    private boolean isPausedCall = false;
    private boolean autonome = false;
    private boolean recording;
    private boolean audioRepeat = false;
    private boolean audioShuffle = false;
    private boolean paused = false;
    private String mode = Constants.BLANK;

    private AudioRecord recorder;
    private MediaPlayer mediaPlayer;
    private Notification notification;
    private PhoneStateListener phoneStateListener;

    private final Handler handler = new Handler();

    private Intent seekIntent = new Intent(Constants.BROADCAST_ACTION);
    private Intent endSongIntent = new Intent(Constants.BROADCAST_ENDOFSONG);
    private Intent refreshUi = new Intent(Constants.BROADCAST_REFRESHUI);

    private BroadcastReceiver pauseSongReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String flag = intent.getStringExtra(Constants.FLAG);

            switch (flag) {
                case Constants.DESTROY:
                    stopSelf();
                    break;
                case Constants.PAUSE:
                    if (!recording) {
                        pauseMedia();
                        refreshUi();
                        initNotification();
                    } else {
                        recordStop();
                    }
                    break;
                case Constants.RESUME:
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
    private BroadcastReceiver runningAutonome = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            initNotification();
            autonome = intent.getBooleanExtra(Constants.FLAG, false);

            if (autonome) {
                audioRepeat = intent.getBooleanExtra(Constants.AUDIO_REPEAT, false);
                audioShuffle = intent.getBooleanExtra(Constants.AUDIO_SHUFFLE, false);
            } else {
                unregisterReceiver(broadcastReceiver);

                registerReceiver(broadcastReceiver, new IntentFilter(
                        Constants.BROADCAST_SEEKBAR));

            }

        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);

        }

    };
    private BroadcastReceiver notifReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!recording) {

                String todo = intent.getStringExtra(Constants.TODO);

                switch (todo) {

                    case Constants.PLAY_PAUSE:

                        if (paused) {
                            paused = false;

                            playMedia();
                            initNotification();

                        } else {

                            pauseMedia();
                            initNotification();
                        }
                        break;
                    case Constants.PREVIOUS:
                        paused = false;
                        prev();
                        initNotification();
                        break;
                    case Constants.NEXT:
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

    private FileOutputStream output;

    static {
        System.loadLibrary(Constants.MP3_LAME);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(runningAutonome, new IntentFilter(
                Constants.BROADCAST_AUTONOME));

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

        pathList = intent.getExtras().getStringArrayList(Constants.AUDIO_PATH);

        audioNumber = intent.getExtras().getInt(Constants.AUDIO_NUMBER);

        audioPath = pathList.get(audioNumber);

        mode = intent.getExtras().getString(Constants.MODE);
        File f = new File(audioPath);
        if (mode.equalsIgnoreCase(Constants.RECORD)) {

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    mSampleRate, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);

            recordStart();
        } else {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);

            if (!mediaPlayer.isPlaying()) {

                try {
                    if (f.exists()) {
                        mediaPlayer.setDataSource(audioPath);
                        Log.d(Constants.AUDIO_PATH, audioPath);
                        mediaPlayer.prepareAsync();
                        registerReceiver(broadcastReceiver, new IntentFilter(
                                Constants.BROADCAST_SEEKBAR));
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


    }

    private Runnable sendUpdatesToUi = new Runnable() {
        public void run() {

            LogMediaPosition();
            handler.postDelayed(this, 250);
        }

        private void LogMediaPosition() {
            if (mediaPlayer.isPlaying()) {

                mediaPos = mediaPlayer.getCurrentPosition();
                mediaMax = mediaPlayer.getDuration();

                seekIntent.putExtra(Constants.COUNTER, String.valueOf(mediaPos));
                seekIntent.putExtra(Constants.MEDIA_MAX, String.valueOf(mediaMax));
                seekIntent.putExtra(Constants.SONG_ENDED, String.valueOf(songEnded));
                sendBroadcast(seekIntent);

            }

        }

    };

    protected void updateSeekPos(Intent intent) {

        int seekPos = intent.getIntExtra(Constants.SEEK_POSITION, 0);
        if (mediaPlayer.isPlaying()) {

            handler.removeCallbacks(sendUpdatesToUi);
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }

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

        if (phoneStateListener != null) {
            telephoneManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }
        /* Log.d("audio service =======+++>", "Stop self"); */
        cancelNotification();
        super.onDestroy();
    }

    @Override
    public void onSeekComplete(MediaPlayer arg0) {
        if (!mediaPlayer.isPlaying()) {

            playMedia();
        }

    }

    @Override
    public void onPrepared(MediaPlayer arg0) {

        playMedia();
    }

    public void playMedia() {

        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                paused = false;
                mediaPlayer.start();
                registerReceiver(pauseSongReceiver, new IntentFilter(
                        Constants.BROADCAST_PAUSESONG));
            }
        }

    }

    public void stopMedia() {
        Log.d("stopping", "stopmedia");

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

        mediaPlayer.setOnSeekCompleteListener(this);

        registerReceiver(broadcastReceiver, new IntentFilter(
                Constants.BROADCAST_SEEKBAR));
        registerReceiver(runningAutonome, new IntentFilter(
                Constants.BROADCAST_AUTONOME));
        registerReceiver(pauseSongReceiver, new IntentFilter(
                Constants.BROADCAST_PAUSESONG));
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

    @SuppressLint("InlinedApi")
    private void initNotification() {
        registerReceiver(notifReceiver, new IntentFilter(Constants.BROADCAST_NOTIFIC));

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.mipmap.ic_launcher;
        CharSequence tickerText = audioPath.substring(audioPath
                .lastIndexOf("/") + 1);
        long when = System.currentTimeMillis();
        Intent prev = new Intent(Constants.BROADCAST_NOTIFIC);
        prev.putExtra(Constants.TODO, Constants.PREVIOUS);
        Intent playpause = new Intent(Constants.BROADCAST_NOTIFIC);
        playpause.putExtra(Constants.TODO, Constants.PLAY_PAUSE);
        Intent next = new Intent(Constants.BROADCAST_NOTIFIC);
        next.putExtra(Constants.TODO, Constants.NEXT);

        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0,
                prev, 0);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 1,
                playpause, 0);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 2,
                next, 0);

        Intent notificationIntent = new Intent(getApplicationContext(),
                ContentActivity.class);

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

        } else if (paused) {
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

            } else {
                audioNumber = randInt(0, (pathList.size() - 1));


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

    public static int randInt(int min, int max) {

        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
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


            } else {
                audioNumber = randInt(0, (pathList.size() - 1));


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

    public void recordStart() {


        recording = true;
        registerReceiver(pauseSongReceiver, new IntentFilter(
                Constants.BROADCAST_PAUSESONG));
        recThread();

    }

    public void recordStop() {
        recording = false;

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

                recorder.startRecording();

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

                        recCoutner = rC.c();
                        seekIntent.putExtra(Constants.RECORDING_COUNTER, recCoutner);

                        sendBroadcast(seekIntent);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                int flushResult = SimpleLame.flush(mp3buffer);

                if (flushResult != 0) {
                    try {
                        output.write(mp3buffer, 0, flushResult);

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

                mode = Constants.BLANK;

            }
        }.start();
    }

    public void refreshUi() {
        refreshUi = new Intent(Constants.BROADCAST_REFRESHUI);
        refreshUi.putExtra(Constants.AUDIO_REPEAT, audioRepeat);
        refreshUi.putExtra(Constants.AUDIO_SHUFFLE, audioShuffle);
        refreshUi.putExtra(Constants.AUDIO_NUMBER, audioNumber);
        refreshUi.putExtra(Constants.PAUSED, paused);

        sendBroadcast(refreshUi);

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
            String cSmall = Constants.BLANK;

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

			/* Log.d("connuter ==>", cSmall); */
            return cSmall;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
