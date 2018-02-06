package com.alkurop.audioplay;


import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;

public interface AudioPlayer {

    void startPlayback(@NotNull String item, long position);

    void stopPlayback();

    void seekTo(long position);

    Observable<AudioPlayerProgress> getPlayerProgress();

    void tearDown();
}
