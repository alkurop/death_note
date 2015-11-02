package com.omar.deathnote.mediaplay.devices;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
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


    private final int mSampleRate = 44100;

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
                time = System.currentTimeMillis();
                while (recording) {
                    Message msg = new Message();
                    msg.arg1 = (int) (System.currentTimeMillis() - time) ;
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
                try {
                    Log.d("recorder", filePath);
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                    File outFile = new File(filePath);
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    outFile.createNewFile();
                    initRecorder();
                    initBuffer();
                    initOutput();
                    SimpleLame.init(mSampleRate, 1, mSampleRate, 32);
                    counterThread();
                    recorder.startRecording();

                    int readSize = 0;
                    while (recording) {

                        readSize = recorder.read(buffer, 0, minBufferSize);
                        int encResult = SimpleLame.encode(buffer, buffer, readSize, mp3buffer);

                        output.write(mp3buffer, 0, encResult);
                    }
                    int flushResult = SimpleLame.flush(mp3buffer);
                    if (flushResult != 0) {
                        output.write(mp3buffer, 0, flushResult);
                    }
                    output.close();
                    recorder.stop();
                    recorder.release();
                    SimpleLame.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Log.e("no such file", filePath);
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
        try {
            recording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AudioRecord findAudioRecord() {
        try {
            minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat
                    .ENCODING_PCM_16BIT);

            if (minBufferSize != AudioRecord.ERROR_BAD_VALUE) {
                AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat
                        .ENCODING_PCM_16BIT, minBufferSize * 4);
                SimpleLame.init(41000, AudioFormat.CHANNEL_IN_MONO, 44100, 32);
                if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                    return recorder;
            }
        } catch (Exception e) {

        }


        return null;
    }

}
