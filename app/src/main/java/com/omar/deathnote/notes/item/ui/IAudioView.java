package com.omar.deathnote.notes.item.ui;

import android.support.annotation.DrawableRes;

/**
 * Created by omar on 9/17/15.
 */
public interface IAudioView {


    void setTitleLabel(String label);

    void setTimerLabel(String label);

    void setPlayBtnDrawable(@DrawableRes int drawable);

    void setShuffleBtnDrawable(@DrawableRes int drawable);

    void setRepeatBtnDrawable(@DrawableRes int drawable);

    void setHidableVisibility(boolean visible);

}
