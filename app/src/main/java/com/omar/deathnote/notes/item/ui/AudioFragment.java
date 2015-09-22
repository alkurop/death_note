package com.omar.deathnote.notes.item.ui;

import android.content.Context;
import android.support.annotation.DrawableRes;
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
public class AudioFragment extends BaseItemFragment implements IAudioView {

    @InjectView(R.id.hidable)
    View hidable;
    @InjectView(R.id.btnPlay)
    ImageView btnPlay;
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
    public void setPlayBtnDrawable(@DrawableRes int drawable) {
        btnPlay.setImageResource(drawable);
    }


    @Override
    public void setShuffleBtnDrawable(@DrawableRes int drawable) {
        btnShuffle.setImageResource(drawable);
    }

    @Override
    public void setRepeatBtnDrawable(@DrawableRes int drawable) {
        btnRepeat.setImageResource(drawable);
    }

    @Override
    public void setHidableVisibility(boolean visible) {
        hidable.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.btnPlay)
    void playClicked() {
        audioEventHandler.playCLicked();
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

}
