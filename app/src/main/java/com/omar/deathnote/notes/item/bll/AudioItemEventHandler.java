package com.omar.deathnote.notes.item.bll;

import com.omar.deathnote.audioPlay.AudioClient;
import com.omar.deathnote.audioPlay.IAudioClient;
import com.omar.deathnote.audioPlay.IMediaManager;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.ui.IAudioView;
import com.omar.deathnote.notes.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public class AudioItemEventHandler
        extends ContentItemPresenter
        implements IAudioEventHandler {

    private IAudioClient audioClinet;
    private IAudioView audioView;
    @Override
    public IAudioClient getAudioClinet() {
        return audioClinet;
    }



    @Override
    public void init(Content content, INoteEventHandler noteEventHandler) {
       super.init(content, noteEventHandler);
        audioClinet = new AudioClient() {
            @Override
            public void stop() {

            }

            @Override
            public void play() {

            }

            @Override
            public void setSeekbarMax(int max) {

            }

            @Override
            public void setSeekbarProgress(int max) {

            }

            @Override
            public void setAudioTitle(String title) {

            }

            @Override
            public void setShuffleState(boolean state) {

            }

            @Override
            public void setHidableState(boolean state) {

            }

            @Override
            public void setAudioController(IMediaManager audioController) {

            }
        };
    }


    @Override
    public void displayView() {

    }

    @Override
    public void setView(IContentView contentView) {
       super. setView(contentView);
        audioView = (IAudioView)contentView;
    }

    @Override
    public Content getContent() {
        return content;
    }

    @Override
    public void saveData() {
        content.setContent1(null);
        content.setContent2(null);
    }
    @Override
    public void playCLicked() {

    }


    @Override
    public void nextClicked() {

    }

    @Override
    public void prevClicked() {

    }

    @Override
    public void stopClicked() {

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


}


