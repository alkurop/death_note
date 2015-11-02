package com.omar.deathnote.mediaplay.devices;

/**
 * Created by omar on 11/2/15.
 */
public interface IAudioPlayerCallback {
    void onErrorOccured(String error);

    void onAudioEndedAndStoped();

    void normalMessage(String message);

    void sendPlayerId(int id);

    void sendPositionUpdate( int max, int position);
}
