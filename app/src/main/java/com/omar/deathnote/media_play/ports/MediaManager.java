package com.omar.deathnote.media_play.ports;

import com.omar.deathnote.media_play.devices.AudioPlayer;
import com.omar.deathnote.media_play.devices.AudioRecorder;
import com.omar.deathnote.media_play.devices.IAudioPlayerCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 9/17/15.
 */
public class MediaManager implements IMediaManager {

    private static MediaState mediaState;
    private List<IMediaClient> mediaClientList;
    private static MediaManager mediaManager;

    private static AudioPlayer audioPlayer;
    private static AudioRecorder audioRecorder;

    private static IAudioPlayerCallback audioPlayerCallback;


    private boolean isShuffle;
    private boolean isRepeat;

    private MediaManager() {
        audioPlayerCallbacks();
        mediaClientList = new ArrayList<>();
        mediaState = new MediaState();
        audioPlayer = new AudioPlayer(audioPlayerCallback);
        audioRecorder = new AudioRecorder();
    }

    public static IMediaManager I() {
        if (mediaManager == null) mediaManager = new MediaManager();
        return mediaManager;
    }


    private void audioPlayerCallbacks() {
        audioPlayerCallback = new IAudioPlayerCallback() {
            @Override
            public void onErrorOccured(String error) {

            }

            @Override
            public void onAudioEndedAndStoped() {
                onPlayEnded();
            }

            @Override
            public void normalMessage(String message) {

            }

            @Override
            public void sendPlayerId(int id) {

            }

            @Override
            public void sendPositionUpdate(int max, int position) {
                if (mediaManager.getMediaState().getClient() != null)
                    ((IAudioClient) (mediaState.getClient())).updateSeekBar(max, position);
            }
        };
    }

    @Override
    public void addMediaCLient(IMediaClient mediaClient) {
        mediaClientList.add(mediaClient);
    }

    @Override
    public void remmoveMediaClient(IMediaClient mediaClient) {
        mediaClientList.remove(mediaClient);
    }

    @Override
    public void playAudio(IMediaClient mediaClient) {
        stopAudio();
        mediaState.setClinet(mediaClient);
        mediaState.setState(MediaState.STATES.PLAYING_AUDIO);
        if (mediaManager.getMediaState().getClient() != null) {
            audioPlayer.playerStart(((IAudioClient) (mediaState.getClient())).getFilePath());
            ((IAudioClient) (mediaState.getClient())).playCallback();
        }
    }

    @Override
    public void stopAudio() {
        if (mediaState.getState() == MediaState.STATES.STOPPED)
            return;
        audioPlayer.playerStop();
        mediaState.setState(MediaState.STATES.STOPPED);
        if (mediaManager.getMediaState().getClient() != null)
            ((IAudioClient) (mediaState.getClient())).stopCallback();
    }

    @Override
    public void recordAudio(IMediaClient mediaClient) {
        mediaState.setClinet(mediaClient);
        mediaState.setState(MediaState.STATES.RECORDING_AUDIO);
        if (mediaManager.getMediaState().getClient() != null)
            ((IAudioClient) (mediaState.getClient())).recordCallback();
    }

    @Override
    public void playPrev() {
    }

    @Override
    public void pause() {
        mediaState.setState(MediaState.STATES.PAUSED_AUDIO);
        if (mediaManager.getMediaState().getClient() != null)
            ((IAudioClient) (mediaState.getClient())).pauseCallback();
    }

    @Override
    public void playNext() {
    }

    @Override
    public void onPlayEnded() {
        mediaState.setState(MediaState.STATES.STOPPED);
        if (mediaManager.getMediaState().getClient() != null)
            ((IAudioClient) (mediaState.getClient())).stopCallback();
    }

    @Override
    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    @Override
    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    @Override
    public MediaState getMediaState() {
        return mediaState;
    }
}
