package com.omar.deathnote.notes.add;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.omar.deathnote.R;
import com.omar.deathnote.notes.legacy.ui.NoteActivity_old;

public class AddPicDialog extends DialogFragment {

    public interface PicDialogListener {
        void onDialogClickBrowsePic(DialogFragment dialog);

        void onDialogClickCameraPic(DialogFragment dialog);
    }

    PicDialogListener mListener;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (NoteActivity_old.class.isInstance(activity)) {

            mListener = NoteActivity_old.getPicDialogListener();
        } else if (PicDialogListener.class.isInstance(activity)) {

            mListener = (PicDialogListener) activity;
        } else {
            throw new IllegalArgumentException("must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.add_pic_dialog).setItems(
                R.array.pic_add_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mListener.onDialogClickBrowsePic(AddPicDialog.this);
                                break;
                            case 1:
                                mListener.onDialogClickCameraPic(AddPicDialog.this);
                                break;
                            case 2:
                                break;
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        int dialogWidth = getResources().getDimensionPixelSize(
                R.dimen.popup_width);
        int dialogHeight = getResources().getDimensionPixelSize(
                R.dimen.popup_height);

        alertDialog.getWindow().setLayout(dialogWidth, dialogHeight);
        return alertDialog;
    }
}
