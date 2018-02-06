package com.alkurop.audioplay;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class AudioPlayerImpl implements AudioPlayer {

    private static final int UPDATE_SEEKER_INTERVAL = 500;

    private final SimpleExoPlayer mExoPlayer;
    private final ExoPlayer.EventListener mListener;


    private final PublishSubject<Notification<AudioPlayerProgress>> mProgressSubject = PublishSubject.create();
    private final CompositeDisposable mProgressSubscription = new CompositeDisposable();

    private String mPlaylistItem;

    private final AudioManager mAudioManager;
    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;

    public AudioPlayerImpl(@NotNull Context context) {
        TrackSelector trackSelector = new DefaultTrackSelector();
        DefaultLoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        mListener = new AudioPlayerSimpleEventListener() {

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                stopPlayback();
                mProgressSubject.onNext(Notification.<AudioPlayerProgress>createOnError(error));
            }

            @Override
            void onTrackEnded() {
                AudioPlayerProgress playerProgress =
                        new AudioPlayerProgress(mExoPlayer.getDuration(), mExoPlayer.getDuration());
                mProgressSubject.onNext(Notification.createOnNext(playerProgress));
                stopPlayback();
            }
        };

        mExoPlayer.addListener(mListener);

        mAudioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    startPlaybackOnFocusGranted();
                } else {
                    pausePlayback();
                }
            }
        };
    }

    @Override
    public void startPlayback(@NotNull String item, long position) {
        try {
            stopPlayback();
            mPlaylistItem = item;
            MediaSource mMediaSource = buildMediaSource();
            mExoPlayer.prepare(mMediaSource, false, false);
            mExoPlayer.seekTo(position);
            int audioFocus = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (audioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                startPlaybackOnFocusGranted();
            }
        } catch (Exception error) {
            mProgressSubject.onNext(Notification.<AudioPlayerProgress>createOnError(error));
        }
    }

    private void startPlaybackOnFocusGranted() {
        if (mPlaylistItem != null) {
            mExoPlayer.setPlayWhenReady(true);
            updateProgress();
        }
    }

    private void pausePlayback() {
        mProgressSubject.onNext(Notification.<AudioPlayerProgress>createOnComplete());
        mExoPlayer.setPlayWhenReady(false);
        stopProgressUpdates();
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    @Override
    public void stopPlayback() {
        mExoPlayer.stop();
        mPlaylistItem = null;
        mProgressSubject.onNext(Notification.<AudioPlayerProgress>createOnComplete());
        stopProgressUpdates();
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    @Override
    public void seekTo(long position) {
        mExoPlayer.seekTo(position);
    }

    @Override
    public void tearDown() {
        stopProgressUpdates();
        mExoPlayer.release();
        mExoPlayer.removeListener(mListener);
    }

    @Override
    public Observable<AudioPlayerProgress> getPlayerProgress() {
        return mProgressSubject.dematerialize();
    }

    private MediaSource buildMediaSource() throws FileDataSource.FileDataSourceException {
        Uri uri = Uri.parse(mPlaylistItem);
        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        fileDataSource.open(dataSpec);

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        return new ExtractorMediaSource(fileDataSource.getUri(),
                                        factory, new DefaultExtractorsFactory(), null, null);
    }

    private void updateProgress() {
        Disposable intervalSub = Observable
                .interval(UPDATE_SEEKER_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        long duration = mExoPlayer.getDuration();
                        long currentPosition = mExoPlayer.getCurrentPosition();
                        if (duration > currentPosition) {
                            AudioPlayerProgress playerProgress =
                                    new AudioPlayerProgress(duration, currentPosition);
                            mProgressSubject.onNext(Notification.createOnNext(playerProgress));
                        }
                    }
                });
        mProgressSubscription.add(intervalSub);
    }

    private void stopProgressUpdates() {
        mProgressSubject.onNext(Notification.<AudioPlayerProgress>createOnComplete());
        mProgressSubscription.clear();
    }

}
