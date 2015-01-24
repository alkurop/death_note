package com.omar.deathnote.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import com.omar.deathnote.R;

@SuppressLint("InflateParams")
public class DialogOnDelete extends DialogFragment implements OnClickListener {

	final String LOG_TAG = "myLogs";

	public interface  DeleteDialog {
		public void del(String dialogId,String s);
	}

	DeleteDialog deleteDialog;
 @Override 
public void onStart(){
	super.onStart();
/*
	  // safety check
	  if (getDialog() == null)
	    return;*/

	  int dialogWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
	  int dialogHeight =getResources().getDimensionPixelSize(R.dimen.popup_height);
	  getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
} 
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			deleteDialog = (DeleteDialog) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onSomeEventListener");
		}
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
			deleteDialog.del("delItem","Yes");
			/*Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());*/
			dismiss();
			break;
		case R.id.btnNo:
			deleteDialog.del("delItem","No");
	/*		Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());*/
			dismiss();
			break;
		}
	}

	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		/*Log.d(LOG_TAG, "Dialog 1: onDismiss");*/
	}

	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		/*Log.d(LOG_TAG, "Dialog 1: onCancel");*/
	}

}