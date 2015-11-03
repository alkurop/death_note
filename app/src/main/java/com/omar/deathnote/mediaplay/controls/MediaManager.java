package com.omar.deathnote.mediaplay.controls;

import com.omar.deathnote.mediaplay.devices.AudioPlayer;
import com.omar.deathnote.mediaplay.devices.IAudioPlayerCallback;
import com.omar.deathnote.mediaplay.devices.IVoiceRecorderCallback;
import com.omar.deathnote.mediaplay.devices.VoiceRecorder;
import com.omar.deathnote.mediaplay.ports.AudioClient;
import com.omar.deathnote.mediaplay.ports.IAudioClient;
import com.omar.deathnote.mediaplay.ports.IMediaClient;
import com.omar.deathnote.mediaplay.ports.MediaState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by omar on 9/17/15.
 */
public class MediaManager implements IMediaManager {
    private static MediaState mediaState;
    private List<IMediaClient> mediaClientList;
    private static MediaManager mediaManager;

    private static AudioPlayer audioPlayer;
    private static VoiceRecorder voiceRecorder;

    private static IAudioPlayerCallback audioPlayerCallback;
    private static IVoiceRecorderCallback voiceRecorderCallback;
    private Random rand;
    private boolean isShuffle;


    private static final String TAG = "Media C0ntroller";

    private MediaManager() {
        initAudioPlayerCallbacks();
        initVoiseRecoderCallback();
        mediaClientList = new ArrayList<>();
        mediaState = new MediaState();
        audioPlayer = new AudioPlayer(audioPlayerCallback);
        voiceRecorder = new VoiceRecorder(voiceRecorderCallback);
        rand = new Random();
    }

    private void initVoiseRecoderCallback() {
        voiceRecorderCallback = new IVoiceRecorderCallback() {
            @Override
            public void onErrorOccured(String error) {

            }

            @Override
            public void normalMessage(String message) {

            }

            @Override
            public void sendPositionUpdate(int position) {
                ((IAudioClient) (mediaState.getClient())).updateRecordingProgress(position);
            }
        };
    }

    public static IMediaManager I() {
        if (mediaManager == null) mediaManager = new MediaManager();
        return mediaManager;
    }

    private void initAudioPlayerCallbacks() {
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

        audioPlayer.playerStart(((IAudioClient) (mediaState.getClient())).getFilePath());
        ((IAudioClient) (mediaState.getClient())).playCallback();

    }

    @Override
    public void stopAudio() {
        if (mediaState.getState() == MediaState.STATES.STOPPED)
            return;
        if (mediaState.getState() == MediaState.STATES.RECORDING_AUDIO)
            voiceRecorder.recordStop();
        audioPlayer.playerStop();
        mediaState.setState(MediaState.STATES.STOPPED);
        ((IAudioClient) (mediaState.getClient())).stopCallback();
    }

    @Override
    public void recordAudio(IMediaClient mediaClient) {
        mediaState.setClinet(mediaClient);
        voiceRecorder.recordStart(((AudioClient) mediaClient).getFilePath());
        mediaState.setState(MediaState.STATES.RECORDING_AUDIO);
        ((IAudioClient) (mediaState.getClient())).recordCallback();
    }

    @Override
    public void playPrev() {
        stopAudio();
        int index = -1;
        try {
            index = mediaClientList.indexOf(mediaState.getClient());
        } catch (Exception e) {
        } finally {
            if (index != -1)
                if (index > 0)
                    playAudio(mediaClientList.get(index - 1));
               /* else{
                    playAudio(mediaClientList.get(0));}*/
        }
    }

    @Override
    public void pauseAudio() {
        mediaState.setState(MediaState.STATES.PAUSED_AUDIO);
        audioPlayer.playerPause();
        ((IAudioClient) (mediaState.getClient())).pauseCallback();
    }

    @Override
    public void resumeAudio() {
        mediaState.setState(MediaState.STATES.PLAYING_AUDIO);
        audioPlayer.playerResume();
        ((IAudioClient) (mediaState.getClient())).playCallback();
    }

    @Override
    public void playNext() {
        stopAudio();
        int index = -1;
        try {
            index = mediaClientList.indexOf(mediaState.getClient());
        } catch (Exception e) {} finally {
            if (index != -1)
                if (mediaClientList.size() > index + 1)
                    playAudio(mediaClientList.get(index + 1));
        /*        else{
                    playAudio(mediaClientList.get(0));}*/
        }

    }

    @Override
    public void playShuffle() {
        stopAudio();

        playAudio(mediaClientList.get(randInt(0, mediaClientList.size() - 1)));

    }

    @Override
    public void onPlayEnded() {
        mediaState.setState(MediaState.STATES.STOPPED);
        ((IAudioClient) (mediaState.getClient())).stopCallback();
        if (((AudioClient) mediaState.getClient()).isRepeat())
            repeat();
        else if (isShuffle) ;
        else
            playNext();
    }

    @Override
    public void repeat() {
        stopAudio();
        int index = -1;
        try {
            index = mediaClientList.indexOf(mediaState.getClient());
        } catch (Exception e) {} finally {
            if (index != -1)

                playAudio(mediaClientList.get(index));

        }
    }


    @Override
    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
        for (IMediaClient item : mediaClientList)
            ((AudioClient) item).updateShuffleState(shuffle);
    }

    @Override
    public boolean isShuffle() {
        return isShuffle;
    }


    @Override
    public MediaState getMediaState() {
        return mediaState;
    }

    @Override
    public void updateAudioProgress(int position) {
        audioPlayer.updateAudioProgress(position);
    }

    public int randInt(int min, int max) {

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
