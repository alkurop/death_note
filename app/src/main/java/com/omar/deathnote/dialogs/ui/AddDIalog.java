package com.omar.deathnote.dialogs.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import com.omar.deathnote.R;

/**
 * Created by omar on 9/14/15.
 */
public class AddDIalog extends AppCompatDialogFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }



}
