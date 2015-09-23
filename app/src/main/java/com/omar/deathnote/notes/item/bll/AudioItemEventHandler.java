package com.omar.deathnote.notes.item.bll;

import com.omar.deathnote.media_play.ports.AudioClient;
import com.omar.deathnote.media_play.ports.IAudioClient;
import com.omar.deathnote.media_play.ports.IMediaClient;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.ui.IAudioView;
import com.omar.deathnote.notes.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public class AudioItemEventHandler extends ContentItemPresenter implements IAudioEventHandler {

    private String filepath = "";
    private IAudioClient audioClinet;
    private IAudioView audioView;

    @Override
    public IMediaClient getMediaClient() {
        return ((IMediaClient) audioClinet);
    }

    @Override
    public void init(Content content, INoteEventHandler noteEventHandler) {
        super.init(content, noteEventHandler);
        audioClinet = new AudioClient() {

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
                audioView.setPlayingMode();
            }

            @Override
            public void pauseCallback() {
                audioView.setPausedMode();
            }

            @Override
            public void setSeekbarMax(int max) {

            }

            @Override
            public void setSeekbarProgress(int max) {

            }

            @Override
            public void setShuffleState(boolean state) {
                audioView.setShuffle(state);
            }

            @Override
            public void setRepeatState(boolean state) {
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
                break;
        }
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
        switch (audioClinet.getThisState()) {
            case IS_PAUSED_THIS:
                audioClinet.play();
                break;
            case IS_RECORDING_THIS:
                audioClinet.stop();
                break;
            case IS_PLAYING_THIS:
                audioClinet.pause();
                break;
            case NOT_THIS:
                audioClinet.play();
                break;
        }
    }

    @Override
    public void nextClicked() {

    }

    @Override
    public void prevClicked() {

    }

    @Override
    public void stopClicked() {
        audioClinet.stop();
    }

    @Override
    public void progressUpdated(int position) {

    }

    @Override
    public void shuffleClicked() {
    }

    @Override
    public void repeatClicked() {
    }

    @Override
    public void setFilePath(String s) {
        filepath = s;
        audioView.setTitleLabel(filepath.substring(filepath.lastIndexOf("/") + 1));
        audioClinet.setFilePath(filepath);
    }

    @Override
    public String getFilePath() {
        return filepath;
    }


}


