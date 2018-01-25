package com.omar.deathnote.notes.legacy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.omar.deathnote.R;
import com.omar.deathnote.notes.legacy.ui.NoteActivity_old;

public class AddAudioDialog extends DialogFragment {

    private AudioDialogListener mListener;

    public interface AudioDialogListener {

        void onDialogClickAudioBrowse(DialogFragment dialog);

        void onDialogClickAudioRecord(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (NoteActivity_old.class.isInstance(activity)) {

            mListener = NoteActivity_old.getAudioDialogListener();

        } else {
            throw new IllegalArgumentException(
                    " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.audio_add_dialog).setItems(
                R.array.audio_add_dialog,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mListener
                                        .onDialogClickAudioBrowse(AddAudioDialog.this);
                                break;
                            case 1:
                                mListener
                                        .onDialogClickAudioRecord(AddAudioDialog.this);
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
