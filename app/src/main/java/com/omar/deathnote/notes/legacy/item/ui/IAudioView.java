package com.omar.deathnote.notes.legacy.item.ui;

/**
 * Created by omar on 9/17/15.
 */
public interface IAudioView {

    void setTitleLabel(String label);

    void setTimerLabel(String label);

    void setShuffle(boolean isSHuffle);

    void setRepeat (boolean isRepeat);

    void getAudioMediaStore();

    void setStopMode();

    void setPlayingMode();

    void setRecordMode();

    void setRecordingMode();

    void setPausedMode();

    void updateSeekbar(int max, int position);



    void setSeekbarActive(boolean b);
}
