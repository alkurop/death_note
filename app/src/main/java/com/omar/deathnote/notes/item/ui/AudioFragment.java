package com.omar.deathnote.notes.item.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.omar.deathnote.R;
import com.omar.deathnote.notes.item.bll.IAudioEventHandler;
import com.omar.deathnote.notes.item.bll.IContentEventHandler;

/**
 * Created by omar on 9/8/15.
 */
public class AudioFragment extends BaseItemFragment implements IAudioView, SeekBar.OnSeekBarChangeListener {

    @InjectView(R.id.hidable)
    View hidable;
    @InjectView(R.id.btnPlay)
    ImageView btnPlay;
    @InjectView(R.id.btnRecord)
    ImageView btnRecord;
    @InjectView(R.id.songTime)
    TextView tvTimer;
    @InjectView(R.id.songTitle)
    TextView tvSongTitle;
    @InjectView(R.id.seekBar1)
    SeekBar seekBar;
    @InjectView(R.id.btnPrev)
    ImageView btnPrev;
    @InjectView(R.id.btnStop)
    ImageView btnStop;
    @InjectView(R.id.btnNext)
    ImageView btnNext;
    @InjectView(R.id.btnShuffle)
    ImageView btnShuffle;
    @InjectView(R.id.btnRepeat)
    ImageView btnRepeat;


    private int newProgress;
    private boolean blockUpdating;

    private IAudioEventHandler audioEventHandler;

    @Override
    public void setEventHandler(IContentEventHandler eventHandler) {
        super.setEventHandler(eventHandler);
        audioEventHandler = (IAudioEventHandler) eventHandler;
    }

    @Override
    public void setContent1(String content1) {
    }

    @Override
    public void setContent2(String content2) {
        tvSongTitle.setText(content2);
    }

    @Override
    public String getContent1() {
        return null;
    }

    @Override
    public String getContent2() {
        return null;
    }

    @Override
    public int getLayout() {
        return R.layout.note_elem_audio;
    }

    @Override
    public void requestFocus() {
    }

    @OnClick(R.id.songTitle)
    void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void setTitleLabel(String label) {
        tvSongTitle.setText(label);
    }

    @Override
    public void setTimerLabel(String label) {
        tvTimer.setText(label);
    }

    @Override
    public void setShuffle(boolean isSHuffle) {
        btnShuffle.setImageResource(!isSHuffle ? R.drawable.ic_action_shuffle_dark : R.drawable.ic_action_shuffle);
    }

    @Override
    public void setRepeat(boolean isRepeat) {
        btnRepeat.setImageResource(!isRepeat ? R.drawable.ic_action_repeat_dark : R.drawable.ic_action_repeat);
    }

    @Override
    public void getAudioMediaStore() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            try {
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + Environment.getDataDirectory())));
            } catch (Exception e) {
            }
        } else {
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            try {
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + Environment.getDataDirectory())));
            } catch (Exception e) {
            }
        }
        Intent audioPicker = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(audioPicker, 1);
    }

    @Override
    public void setStopMode() {
        btnRecord.setVisibility(View.GONE);
        btnPlay.setVisibility(View.VISIBLE);
        btnPlay.setBackgroundResource(R.drawable.media_play);
        tvTimer.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
    }

    @Override
    public void setPlayingMode() {
        btnRecord.setVisibility(View.GONE);
        btnPlay.setVisibility(View.VISIBLE);
        btnPlay.setBackgroundResource(R.drawable.media_pause);
        tvTimer.setVisibility(View.GONE);
        seekBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setRecordMode() {
        btnRecord.setVisibility(View.VISIBLE);
        btnRecord.setBackgroundResource(R.drawable.ic_action_rec);
        btnPlay.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);

    }

    @Override
    public void setRecordingMode() {
        btnRecord.setVisibility(View.VISIBLE);
        btnRecord.setBackgroundResource(R.drawable.ic_action_recording);
        btnPlay.setVisibility(View.GONE);
        tvTimer.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.GONE);
    }

    @Override
    public void setPausedMode() {
        btnRecord.setVisibility(View.GONE);
        btnPlay.setBackgroundResource(R.drawable.media_paused);
        AnimationDrawable pausedAnimation = (AnimationDrawable) btnPlay.getBackground();
        pausedAnimation.start();

        tvTimer.setVisibility(View.GONE);
        seekBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateSeekbar(int max, int position) {
        if (!blockUpdating) {
            seekBar.setMax(max);
            seekBar.setProgress(position);
        }
    }

    @Override
    public void setSeekbarActive(boolean b) {
        seekBar.setOnSeekBarChangeListener(b ? this : null);
    }

    @OnClick(R.id.btnPlay)
    void playClicked() {
        audioEventHandler.playCLicked();
    }

    @OnClick(R.id.btnRecord)
    void recordClicked() {
        audioEventHandler.recordClicked();
    }

    @OnClick(R.id.btnPrev)
    void prevClicked() {
        audioEventHandler.prevClicked();
    }

    @OnClick(R.id.btnStop)
    void stopClicked() {
        audioEventHandler.stopClicked();
    }

    @OnClick(R.id.btnNext)
    void nextClicked() {
        audioEventHandler.nextClicked();
    }

    @OnClick(R.id.btnShuffle)
    void shuffleClicked() {
        audioEventHandler.shuffleClicked();
    }

    @OnClick(R.id.btnRepeat)
    void repeatClicked() {
        audioEventHandler.repeatClicked();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == 1) {
                Uri audioUri = data.getData();
                if (audioUri != null) {

                    String[] filePathColumn = {MediaStore.Audio.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(audioUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    audioEventHandler.setFilePath(cursor.getString(columnIndex));
                    cursor.close();
                }
            }
        } else {
            eventHandler.delete();
        }


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        newProgress = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        blockUpdating = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        audioEventHandler.progressUpdated(newProgress);
        blockUpdating = false;
    }
}