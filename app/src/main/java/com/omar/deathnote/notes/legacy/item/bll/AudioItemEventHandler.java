package com.omar.deathnote.notes.legacy.item.bll;

import com.omar.deathnote.mediaplay.ports.AudioClient;
import com.omar.deathnote.mediaplay.ports.IAudioClient;
import com.omar.deathnote.mediaplay.ports.IMediaClient;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.legacy.bll.INoteEventHandler;
import com.omar.deathnote.notes.legacy.item.ui.IAudioView;
import com.omar.deathnote.notes.legacy.item.ui.IContentView;
import com.omar.deathnote.utility.FileManager;

import java.text.SimpleDateFormat;

/**
 * Created by omar on 9/7/15.
 */
public class AudioItemEventHandler extends ContentItemPresenter implements IAudioEventHandler {

    private String filepath = "";
    private IAudioClient audioClient;
    private IAudioView audioView;

    @Override
    public IMediaClient getMediaClient() {
        return ((IMediaClient) audioClient);
    }

    @Override
    public void init(Content content, INoteEventHandler noteEventHandler) {
        super.init(content, noteEventHandler);
        audioClient = new AudioClient() {

            @Override
            public void stopCallback() {
                audioView.setStopMode();
            }

            @Override
            public void recordCallback() {
                audioView.setRecordingMode();
            }

            @Override
            public void playCallback() {
                audioView.setSeekbarActive(true);
                audioView.setPlayingMode();
            }

            @Override
            public void pauseCallback() {
                audioView.setPausedMode();
            }

            @Override
            public void updateSeekBar(int max, int progress) {
                audioView.updateSeekbar(max, progress);
            }

            @Override
            public void updateRecordingProgress(int progress) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                audioView.setTimerLabel(dateFormat.format(progress));
            }


            @Override
            public void updateShuffleState(boolean state) {
                audioView.setShuffle(state);
            }

            @Override
            public void updateRepeatState(boolean state) {
                audioView.setRepeat(state);
            }
        };
    }

    @Override
    public void displayView() {


        switch (getContent().getType()) {
            case AUDIO_FILE:
                audioView.setStopMode();
                if (getContent().getContent1().isEmpty()) audioView.getAudioMediaStore();
                else setFilePath(getContent().getContent1());
                break;
            case AUDIO_RECORD:
                audioView.setRecordMode();

                break;
        }

        audioView.setRepeat(audioClient.isRepeat());
        audioView.setShuffle(audioClient.isShuffle());
    }

    @Override
    public void setView(IContentView contentView) {
        super.setView(contentView);
        audioView = (IAudioView) contentView;
    }

    @Override
    public Content getContent() {
        return content;
    }

    @Override
    public void saveData() {
        content.setContent1(filepath);
        content.setContent2(filepath.substring(filepath.lastIndexOf("/") + 1));
    }

    @Override
    public void playCLicked() {

        switch (audioClient.getThisState()) {
            case IS_PAUSED_THIS:
                audioClient.resume();
                break;
            case IS_RECORDING_THIS:
                audioClient.stop();
                break;
            case IS_PLAYING_THIS:
                audioClient.pause();
                break;
            case NOT_THIS_OR_STOPPED:
                audioClient.play();
                break;
        }
    }

    @Override
    public void nextClicked() {
        audioClient.playNext();
    }

    @Override
    public void prevClicked() {
        audioClient.playPrev();
    }

    @Override
    public void stopClicked() {
        audioClient.stop();
    }

    @Override
    public void progressUpdated(int position) {
        audioClient.updateProgress(position);
    }

    @Override
    public void shuffleClicked() {
        audioClient.setShuffleState(!audioClient.isShuffle());
    }

    @Override
    public void repeatClicked() {
        audioClient.setRepeatState(!audioClient.isRepeat());
    }

    @Override
    public void setFilePath(String s) {
        filepath = s;
        audioView.setTitleLabel(filepath.substring(filepath.lastIndexOf("/") + 1));
        audioClient.setFilePath(filepath);
    }

    @Override
    public String getFilePath() {
        return filepath;
    }

    @Override
    public void recordClicked() {
        switch (audioClient.getThisState()) {
            case IS_RECORDING_THIS:
                audioClient.stop();
                break;
            case NOT_THIS_OR_STOPPED:
                setFilePath(new FileManager().generateAudioFilePath());
                audioClient.record();
                getContent().setType(Content.ContentType.AUDIO_FILE);
                break;

        }
    }


}


