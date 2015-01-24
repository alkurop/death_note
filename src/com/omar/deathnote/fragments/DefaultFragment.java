package com.omar.deathnote.fragments;

import java.util.TreeMap;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.omar.deathnote.R;

public class DefaultFragment extends Fragment {

	EditText etTitle;
 
	String title;
 
	 String fragId;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(savedInstanceState != null )fragId = savedInstanceState.getString("fragId");
		View v = inflater.inflate(R.layout.note_elem_default, null);

		etTitle = (EditText) v.findViewById(R.id.link);
		etTitle.setText(title);
	/*	etText = (EditText) v.findViewById(R.id.etTxt);
		etText.setText(text);*/
		return v;
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (fragId != null)
			outState.putString("fragId", fragId);}
	public TreeMap<String, String> saveContent() {
		String title = etTitle.getText().toString();
 
		TreeMap<String, String> content = new TreeMap<String, String>();
	 if (title.equalsIgnoreCase("")) {content.put("cont1", "No Title");}  else{content.put("cont1", title);}
		  
		 
		return content;

	}

	public void loadContent(TreeMap <String, String> temp) {
		 
		 

			title = temp.get("cont1");
		 
		 
	}
	public void loadFragId (String str){
		fragId = str;
	}
}