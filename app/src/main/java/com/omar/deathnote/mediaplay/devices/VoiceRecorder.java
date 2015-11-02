package com.omar.deathnote.mediaplay.devices;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import com.omar.deathnote.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by omar on 11/2/15.
 */
public class VoiceRecorder {
    private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};
    private static String TAG = "recorder";
    private final int mSampleRate = 8000;

    private short[] buffer;
    private byte[] mp3buffer;
    private int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private AudioRecord recorder;
    private String filePath;
    boolean recording;
    private FileOutputStream output;
    private static Handler handler;
    private static IVoiceRecorderCallback callback;

    private long time;

    static {
        System.loadLibrary(Constants.MP3_LAME);
    }


    public VoiceRecorder(final IVoiceRecorderCallback callback) {
        this.callback = callback;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        callback.onErrorOccured("error recording");
                        break;
                    case 1:
                        callback.sendPositionUpdate(msg.arg1);
                        break;
                }
            }
        }

        ;
    }

    private void initBuffer() {
        buffer = new short[mSampleRate * (16 / 8) * 1 * 5]; // SampleRate[Hz]
        mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];
    }

    private void initRecorder() {
        recorder = findAudioRecord();
    }

    private void initOutput() {
        output = null;
        try {
            output = new FileOutputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            callback.onErrorOccured("file not found");
        }

    }

    public void counterThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                time = SystemClock.currentThreadTimeMillis();
                while (recording) {
                    Message msg = new Message();
                    msg.arg1 = (int) (time - (SystemClock.currentThreadTimeMillis()) / 100);
                    msg.what = 1;
                    handler.sendMessage(msg);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void recThread() {


        new Thread() {
            @Override
            public void run() {

                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);


                File outFile = new File(filePath);
                if (outFile.exists()) {
                    outFile.delete();
                }
                try {
                    outFile.createNewFile();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                initRecorder();
                initBuffer();
                initOutput();

                SimpleLame.init(mSampleRate, 1, mSampleRate, 32);
                try {
                    recorder.startRecording();
                    counterThread();
                    int readSize = 0;
                    while (recording) {

                        readSize = recorder.read(buffer, 0, minBufferSize);
                        int encResult = SimpleLame.encode(buffer, buffer, readSize, mp3buffer);
                        callback.normalMessage("recording");

                        try {
                            output.write(mp3buffer, 0, encResult);
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
                        e.printStackTrace();
                    }
                    recorder.stop();
                    recorder.release();
                    SimpleLame.close();
                } catch (Exception e) {
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    public void recordStart(String path) {
        this.filePath = path;
        recording = true;
        recThread();
    }

    public void recordStop() {
        recording = false;

    }

    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        minBufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (minBufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, minBufferSize);
                            SimpleLame.init(rate, channelConfig, rate, 32);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }

}
