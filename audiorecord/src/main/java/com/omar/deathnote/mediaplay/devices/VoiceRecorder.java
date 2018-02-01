package com.omar.deathnote.mediaplay.devices;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class VoiceRecorder {


    private final int mSampleRate = 44100;

    private short[] buffer;
    private byte[] mp3buffer;
    private int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    private AudioRecord recorder;
    private String filePath;
    private boolean isRecording;
    private FileOutputStream output;
    private static Handler handler;
    private IVoiceRecorderCallback mCallback;

    private long time;

    static {
        System.loadLibrary("mp3lame");
    }


    public VoiceRecorder(final IVoiceRecorderCallback callback) {
        this.mCallback = callback;
        handler = new MyHandler(callback);
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
            mCallback.onErrorOccured("file not found");
        }

    }

    private void counterThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                time = System.currentTimeMillis();
                while (isRecording) {
                    Message msg = new Message();
                    msg.arg1 = (int) (System.currentTimeMillis() - time);
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

    private void recThread() {


        new Thread() {
            @Override
            public void run() {
                try {
                    Timber.d(filePath);
                    Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
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
                    while (isRecording) {

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

                } catch (Exception e) {
                    Timber.e(filePath);
                }
            }
        }.start();
    }

    public void recordStart(String path) {
        this.filePath = path;
        isRecording = true;
        recThread();
    }

    public void recordStop() {
        try {
            isRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AudioRecord findAudioRecord() {
        minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat
                .ENCODING_PCM_16BIT);

        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat
                .ENCODING_PCM_16BIT, minBufferSize * 4);
        SimpleLame.init(41000, AudioFormat.CHANNEL_IN_MONO, 44100, 32);
        return recorder;
    }

    static class MyHandler extends Handler {
        private final IVoiceRecorderCallback mCallback;

        MyHandler(IVoiceRecorderCallback callback) {
            mCallback = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mCallback.onErrorOccured("error recording");
                    break;
                case 1:
                    mCallback.sendPositionUpdate(msg.arg1);
                    break;
            }
        }
    }
}
