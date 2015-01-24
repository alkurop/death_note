package com.omar.deathnote.fragments;

import java.util.TreeMap;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.omar.deathnote.R;
import com.omar.deathnote.utility.OnDeleteFragment;

public class LinkFragment extends Fragment implements OnDeleteFragment {

	EditText etLink;
	String link;
	String fragId;
	Button btnDel;
	LinearLayout main;

	OnDeleteFragment OnDeleteFragment;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			OnDeleteFragment = (OnDeleteFragment) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onSomeEventListener");
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null)
			fragId = savedInstanceState.getString("fragId");
		View v = inflater.inflate(R.layout.note_elem_link, null);
		main = (LinearLayout) v.findViewById(R.id.noteElemAudio);
		main.setFocusable(true); 
		main.requestFocus();
		
		etLink = (EditText) v.findViewById(R.id.link);
		etLink.setText(link);
		etLink.setFocusable(true);
		 
	
		
		
		
		etLink.setLinksClickable(true);
		 

		Linkify.addLinks(etLink, Linkify.WEB_URLS);
		etLink.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (Patterns.WEB_URL.matcher(s).matches()) {

					Linkify.addLinks(etLink, Linkify.WEB_URLS);
					 
						etLink.setLinksClickable(true);

				}

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!Patterns.WEB_URL.matcher(s).matches()) {
					Linkify.addLinks(etLink, Linkify.WEB_URLS);
					etLink.setLinksClickable(false);
				}

			}
		});

		ImageView del = (ImageView) v.findViewById(R.id.del);
		del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OnDeleteFragment.delete(fragId, true);

			}
		});

		return v;

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (fragId != null)
			outState.putString("fragId", fragId);
	}

	public TreeMap<String, String> saveContent() {

		String link = etLink.getText().toString();
		TreeMap<String, String> content = new TreeMap<String, String>();
		if (link.equalsIgnoreCase("")) {
			content.put("cont1", "No Link");
		} else {
			content.put("cont1", link);
		}

		content.put("cont2", "blank");

		return content;

	}

	public void loadContent(TreeMap<String, String> temp) {
		if (temp.get("cont1") != null) {

			link = temp.get("cont1");
			String space = temp.get("cont2");

		}
	}

	public void loadFragId(String str) {
		fragId = str;
	}

	@Override
	public void delete(String s, boolean dialog) {
		// TODO Auto-generated method stub

	}

}