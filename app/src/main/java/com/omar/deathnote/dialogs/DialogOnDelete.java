package com.omar.deathnote.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import com.omar.deathnote.main.ui.MainActivity;
import com.omar.deathnote.main.ui.MainActivity_old;
import com.omar.deathnote.notes.ui.NoteActivity;
import com.omar.deathnote.R;

@SuppressLint("InflateParams")
public class DialogOnDelete extends DialogFragment implements OnClickListener {

	final String LOG_TAG = "myLogs";

	public interface DeleteDialog {
		public void del();
	}

	DeleteDialog deleteDialog;

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (NoteActivity.class.isInstance(activity))

		{
			deleteDialog = NoteActivity.getDeleteDialog();
		} else if (MainActivity.class.isInstance(activity)) {
			deleteDialog = MainActivity_old.getDeleteDialog();
		} else {
			if (DeleteDialog.class.isInstance(activity))

			{

				deleteDialog = (DeleteDialog) activity;
			} else {
				// The activity doesn't implement the interface, throw exception
				throw new IllegalArgumentException(
						"Activity must implement DeleteDialog interface");
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		int dialogWidth = getResources().getDimensionPixelSize(
				R.dimen.popup_width);
		int dialogHeight = getResources().getDimensionPixelSize(
				R.dimen.popup_height);
		getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View v = inflater.inflate(R.layout.dialogondelete, null);
		v.findViewById(R.id.btnYes).setOnClickListener(this);
		v.findViewById(R.id.btnNo).setOnClickListener(this);

		return v;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnYes:
			deleteDialog.del();

			dismiss();
			break;
		case R.id.btnNo:

			dismiss();
			break;
		}
	}

}