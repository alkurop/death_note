package com.omar.deathnote.utility;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.omar.deathnote.Select;
import com.omar.deathnote.fragments.AudioFragment;
import com.omar.deathnote.fragments.DefaultFragment;
import com.omar.deathnote.fragments.LinkFragment;
import com.omar.deathnote.fragments.NoteFragment;
import com.omar.deathnote.fragments.PicFragment;

public class FragmentSaver {
	private FragmentManager fm;
	private Fragment tempFragment;
	private TreeMap<String, String> fragList;

	public FragmentSaver(FragmentManager fm) {
		this.fm = fm;
	}
	
	
	
	public Bundle getTitleBundle(){
		TreeMap<String, String> temp = new TreeMap<String, String>();
		String cont1 = "";
	 

		temp = ((DefaultFragment) tempFragment).saveContent();

		if (temp.get(Select.Flags.Cont1.name()) != null) {
			cont1 = temp.get(Select.Flags.Cont1.name());
		} else {
			cont1 = "No Title";
		}
		Bundle titleBundle = new Bundle();
		titleBundle.putString(Select.Flags.Cont1.name(), cont1);
		return titleBundle;
		
	}

	public Bundle saveFragment() {

		Bundle saveBundle = new Bundle();
		ArrayList<FragContent> fragsArrayList = new ArrayList<FragContent>();

		if (fragList != null)
			/* Log.d("fraglist length", String.valueOf(fragList.size())); */

			for (Map.Entry<String, String> entry : fragList.entrySet()) {
				TreeMap<String, String> temp = new TreeMap<String, String>();

				String fragId = entry.getKey();
				String type = entry.getValue();
	 
				String cont1 = "";
				String cont2 = "";
				Select.Frags[] frags = Select.Frags.values();
				Select.Frags eType = null;

				for (Select.Frags frag : frags) {

					if (type.equalsIgnoreCase(frag.name())) {
						eType = frag;
						/* Log.d("frags", eType.name()); */
					}
				}

				switch (eType) {

				case DefaultFragment:

					tempFragment = (DefaultFragment) fm
							.findFragmentByTag(fragId);

					temp = ((DefaultFragment) tempFragment).saveContent();

					if (temp.get(Select.Flags.Cont1.name()) != null) {
						cont1 = temp.get(Select.Flags.Cont1.name());
					} else {
						cont1 = "No Title";
					}

				

					break;
				case PicFragment:

					tempFragment = (PicFragment) fm.findFragmentByTag(fragId);
					temp = ((PicFragment) tempFragment).saveContent();

					cont1 = temp.get(Select.Flags.Cont1.name());
					if (cont1 != null)
						;

					break;
				case NoteFragment:

					tempFragment = (NoteFragment) fm.findFragmentByTag(fragId);
					temp = ((NoteFragment) tempFragment).saveContent();

					if (!temp.get(Select.Flags.Cont1.name()).equalsIgnoreCase(
							"")) {
						cont1 = temp.get(Select.Flags.Cont1.name());
					} else {
						cont1 = "No Content";
					}

					break;

				case LinkFragment:

					tempFragment = (LinkFragment) fm.findFragmentByTag(fragId);
					temp = ((LinkFragment) tempFragment).saveContent();

					if (!temp.get(Select.Flags.Cont1.name()).equalsIgnoreCase(
							"")) {
						cont1 = temp.get(Select.Flags.Cont1.name());
					} else {
						cont1 = "No Link";
					}

					break;
				case AudioFragment:

					tempFragment = (AudioFragment) fm.findFragmentByTag(fragId);
					temp = ((AudioFragment) tempFragment).saveContent();

					if (!temp.get(Select.Flags.Cont1.name()).equalsIgnoreCase(
							"")) {
						cont1 = temp.get(Select.Flags.Cont1.name());
					} else {
						cont1 = "No Audio";
					}
					if (!temp.get(Select.Flags.Cont2.name()).equalsIgnoreCase(
							"")) {
						cont2 = temp.get(Select.Flags.Cont2.name());
					} else {
						cont2 = "No Audio";
					}
					break;

				default:
					throw new IllegalArgumentException("illigal fragment type");

				}

				FragContent fragContent = new FragContent(type, cont1, cont2);
				fragsArrayList.add(fragContent);

			}

		saveBundle.putParcelableArrayList("fragsArrayList", fragsArrayList);

		return saveBundle;
	}
	
	
	



	}
