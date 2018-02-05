package com.omar.deathnote.mediaplay.devices;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class VoiceRecorder {
    private final IVoiceRecorderCallback callback;
    private final Handler callbackHandler;

    private AudioRecord recorder;
    private FileOutputStream output;

    private static final int STATUS_NORMAL = 0;
    private static final int STATUS_ERROR = 1;
    private static final int COUNTER = 2;

    private final short channelConfig = Constants.CHANNEL_PRESETS[0];

    private final int quality = Constants.QUALITY_PRESETS[1];
    private final short audioFormat = Constants.AUDIO_FORMAT_PRESETS[1];
    private static final int sleepMillis = 100;

    private final String filePath;

    private int minBufferSize;
    private final int mSampleRate;
    private final int mBitRate;
    private short[] buffer;
    private byte[] mp3buffer;

    private boolean recording;
    private long counter;

    public VoiceRecorder(String filepath,
                         IVoiceRecorderCallback callback) {
        this.filePath = filepath;
        this.callback = callback;
        this.mSampleRate = 41000;
        this.mBitRate = 192;
        callbackHandler = new CallbackHandler(callback);
    }

    public void recordStart() {
        minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, channelConfig, audioFormat);
        recording = true;
        recThread();

    }

    public void recordStop() {
        recording = false;
        sendHandlerMessage(STATUS_NORMAL, "Recorder stopped");
    }

    private void initBuffer() {
        buffer = new short[mSampleRate * (16 / 8) * 5]; // SampleRate[Hz]
        mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];
    }

    private void initRecorder() throws Exception {
        recorder = findAudioRecord();
    }

    private void initOutput() throws FileNotFoundException {
        output = null;
        output = new FileOutputStream(new File(filePath));
    }

    private void counterThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                counter = 0;
                while (recording) {
                    try {
                        Thread.sleep(sleepMillis);
                        counter += sleepMillis;
                        sendHandlerMessage(COUNTER, String.valueOf(counter));

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    private void recThread() {
        counterThread();
        new Thread(new Runnable() {
            @Override
            public void run() {

                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                try {
                    File outFile = new File(filePath);
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    outFile.createNewFile();
                    VoiceRecorder.this.initBuffer();
                    VoiceRecorder.this.initOutput();
                    VoiceRecorder.this.initRecorder();
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

                    if (outFile.exists()) {
                        VoiceRecorder.this.sendHandlerMessage(STATUS_NORMAL, "File saved to:" + filePath);
                    } else {
                        VoiceRecorder.this.sendHandlerMessage(STATUS_ERROR, "Error saving file");
                    }


                } catch (Exception e) {
                    recording = false;
                    VoiceRecorder.this.sendHandlerMessage(STATUS_ERROR, e.getMessage());
                }

            }
        }).start();
    }

    private AudioRecord findAudioRecord() throws Exception {
        minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, channelConfig, audioFormat);
        if (minBufferSize != AudioRecord.ERROR_BAD_VALUE) {

            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, channelConfig, audioFormat, minBufferSize * 4);
            try {
                SimpleLame.init(mSampleRate, 1, mSampleRate, mBitRate, quality);
            } catch (Exception e) {
                throw new Exception("Error init recorder");
            }
            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                sendHandlerMessage(STATUS_NORMAL, "Init success");
                return recorder;

            } else {
                throw new Exception("Error init recorder");
            }

        } else {
            throw new Exception("Error init recorder");
        }
    }

    private void sendHandlerMessage(int status, String data) {
        Message m = new Message();
        m.what = status;
        Bundle b = new Bundle();
        b.putString(Constants.DATA, data);
        m.setData(b);
        callbackHandler.sendMessage(m);

    }

    static class CallbackHandler extends Handler {

        private final IVoiceRecorderCallback callback;

        public CallbackHandler(IVoiceRecorderCallback callback) {
            this.callback = callback;
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case STATUS_NORMAL:
                    callback.normalMessage(message.getData().getString(Constants.DATA));
                    break;
                case STATUS_ERROR:
                    callback.onErrorOccured(message.getData().getString(Constants.DATA));
                    break;
                case COUNTER:
                    callback.sendPositionUpdate(Long.parseLong(message.getData().getString(Constants.DATA)));
            }
        }
    }
}
