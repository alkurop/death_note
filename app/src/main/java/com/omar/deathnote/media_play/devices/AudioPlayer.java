package com.omar.deathnote.media_play.devices;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import com.omar.deathnote.App;

import java.io.IOException;

/**
 * Created by omar on 11/2/15.
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private Context context;
    private IAudioPlayerCallback callback;
    private MediaPlayer mediaPlayer;
    private static Handler positionUpdateHandeler = new Handler();

    public AudioPlayer(IAudioPlayerCallback _callback) {
        context = App.getContext();
        callback = _callback;
    }

    public void playerStop() {
        stopMedia();
    }

    public void playerStart(String audioSource) {
        setupMediaPlayer(audioSource);
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopMedia();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {playMedia();}

    private void setupMediaPlayer(String voiceURL) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        try {
            mediaPlayer.setDataSource(voiceURL);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            callback.onErrorOccured("Audio Player Error");
        }
    }

    private void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            callback.normalMessage("Audio Player Stopped");
            callback.onAudioEndedAndStoped();
        } else {
            callback.onErrorOccured("Audio Player Error");
        }
    }

    private void playMedia() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            callback.sendPlayerId(mediaPlayer.getAudioSessionId());
            callback.normalMessage("Audio Player Started");
            setupHandler();
        } else {
            callback.onErrorOccured("Audio Player Error");
        }
    }

    private Runnable sendUpdatesToUi = new Runnable() {
        public void run() {
            LogMediaPosition();
            positionUpdateHandeler.postDelayed(this, 250);
        }
        private void LogMediaPosition() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                callback.sendPositionUpdate(mediaPlayer.getDuration(),  mediaPlayer.getCurrentPosition());
            }

        }

    };

    private void setupHandler() {
        positionUpdateHandeler.removeCallbacks(sendUpdatesToUi);
        positionUpdateHandeler.postDelayed(sendUpdatesToUi, 0);


    }


}
