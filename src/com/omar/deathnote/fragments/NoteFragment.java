package com.omar.deathnote.fragments;

import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.omar.deathnote.NoteActivity;
import com.omar.deathnote.R;
import com.omar.deathnote.Select;
import com.omar.deathnote.utility.OnDeleteFragment;

@SuppressLint("InflateParams")
public class NoteFragment extends Fragment {

	private EditText etText;
	private String text;
	private String fragId;
	private ImageView del;

	private OnDeleteFragment OnDeleteFragment;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (	NoteActivity.class.isInstance(
				activity)) {
			OnDeleteFragment = NoteActivity.getOnDeleteFragment();
		} else {
			throw new IllegalArgumentException(
					"Activity must implement OnDeleteFragment interface ");
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (savedInstanceState != null)
			fragId = savedInstanceState.getString("fragId");
		View v = inflater.inflate(R.layout.note_elem_note, null);
		etText = (EditText) v.findViewById(R.id.etTxt);
		etText.setText(text);
		etText.setFocusable(true);
		etText.setFocusableInTouchMode(true);
		etText.requestFocus();
		etText.setLinksClickable(false);
		TextView tv = (TextView) v.findViewById(R.id.songTitle);

		if (fragId.equalsIgnoreCase("1")) {
			etText.setMinHeight(150);

			tv.setVisibility(View.VISIBLE);

		}

		Linkify.addLinks(etText, Linkify.WEB_URLS);
		etText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (Patterns.WEB_URL.matcher(s).matches()) {

					Linkify.addLinks(etText, Linkify.WEB_URLS);
					try {
						TimeUnit.MILLISECONDS.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					etText.setLinksClickable(true);

				}

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!Patterns.WEB_URL.matcher(s).matches()) {
					Linkify.addLinks(etText, Linkify.WEB_URLS);

				}

			}
		});

		del = (ImageView) v.findViewById(R.id.del);
		del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OnDeleteFragment.delete(fragId, true);

			}
		});

		return v;
	}

	public TreeMap<String, String> saveContent() {
		text = etText.getText().toString();

		TreeMap<String, String> content = new TreeMap<String, String>();
		if (text != null) {
			content.put(Select.Flags.Cont1.name(), text);
		} else {
			content.put(Select.Flags.Cont1.name(), "No Text");
		}
		 

		return content;

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (fragId != null)
			outState.putString("fragId", fragId);
	}

	public void loadContent(TreeMap<String, String> temp) {
		if (temp.get(Select.Flags.Cont1.name()) != null) {

			text = temp.get(Select.Flags.Cont1.name());

		}

	}

	public void loadFragId(String str) {
		fragId = str;
	}

}
