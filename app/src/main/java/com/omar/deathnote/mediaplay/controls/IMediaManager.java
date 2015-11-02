package com.omar.deathnote.mediaplay.controls;

import com.omar.deathnote.mediaplay.ports.IMediaClient;
import com.omar.deathnote.mediaplay.ports.MediaState;

/**
 * Created by omar on 9/8/15.
 */
public interface IMediaManager {

    void addMediaCLient(IMediaClient mediaClient);

    void remmoveMediaClient(IMediaClient mediaClient);

    void playAudio(IMediaClient mediaClient);

    void stopAudio();

    void recordAudio(IMediaClient mediaClient);

    void playPrev();

    void pauseAudio();

    void resumeAudio();

    void playNext();

    void playShuffle();

    void onPlayEnded();

    void repeat();


    void setShuffle(boolean shuffle);

    boolean isShuffle();



    MediaState getMediaState();

    void updateAudioProgress(int position);
}
