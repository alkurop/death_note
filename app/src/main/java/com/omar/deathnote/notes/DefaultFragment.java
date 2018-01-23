package com.omar.deathnote.notes;

import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.omar.deathnote.R;
import com.omar.deathnote.Constants;

@SuppressLint("InflateParams")
public class DefaultFragment extends Fragment {
	private EditText etTitle;
	private String title;

	private String fragId;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null)
			fragId = savedInstanceState.getString(Constants.FRAGMENT_ID);
		
		
		View v = inflater.inflate(R.layout.note_elem_default, null);

		etTitle = (EditText) v.findViewById(R.id.tvTitle);
		etTitle.setText(title);
		
		etTitle.requestFocus();

		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (fragId != null)
			outState.putString(Constants.FRAGMENT_ID, fragId);
	}

	public TreeMap<String, String> saveContent() {
		String title = etTitle.getText().toString();

		TreeMap<String, String> content = new TreeMap<String, String>();
		if (title.equalsIgnoreCase("")) {
			content.put(Constants.Flags.Cont1.name(), "No Title");
		} else {
			content.put(Constants.Flags.Cont1.name(), title);
		}

		return content;

	}

	public void loadContent(TreeMap<String, String> temp) {

		title = temp.get(Constants.Flags.Cont1.name());

	}

	public void loadFragId(String str) {
		fragId = str;
		
	}
	 
	
	
	
	
}
