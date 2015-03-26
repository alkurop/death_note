package com.omar.deathnote.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.omar.deathnote.NoteActivity;
import com.omar.deathnote.R;

public class AddPicDialog extends DialogFragment {

	public interface PicDialogListener {
		public void onDialogClickBrowsePic(DialogFragment dialog);

		public void onDialogClickCameraPic(DialogFragment dialog);
	}

	PicDialogListener mListener;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (NoteActivity.class.isInstance(activity)) {

			mListener = NoteActivity.getPicDialogListener();}
		
		else if (PicDialogListener.class.isInstance(activity)) {

			mListener = (PicDialogListener) activity;
		} else {
			// The activity doesn't implement the interface, throw exception
			throw new IllegalArgumentException(
					" must implement NoticeDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(R.string.add_pic_dialog).setItems(
				R.array.pic_add_dialog, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							/*
							 * Toast.makeText(getActivity(), "From Galery",
							 * Toast.LENGTH_LONG).show();
							 */
							mListener.onDialogClickBrowsePic(AddPicDialog.this);
							break;
						case 1:
							/*
							 * Toast.makeText(getActivity(), "From Camera",
							 * Toast.LENGTH_LONG).show();
							 */
							mListener.onDialogClickCameraPic(AddPicDialog.this);
							break;
						case 2:
							/*
							 * Toast.makeText(getActivity(), "Cancel",
							 * Toast.LENGTH_LONG).show();
							 */
							break;
						}
					}
				});
		// Create the AlertDialog object and return it

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