package com.omar.deathnote.utility;

import java.util.TreeMap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import com.omar.deathnote.R;
import com.omar.deathnote.Constants;
import com.omar.deathnote.notes.legacy.AudioFragment;
import com.omar.deathnote.notes.legacy.DefaultFragment;
import com.omar.deathnote.notes.legacy.LinkFragment;
import com.omar.deathnote.notes.legacy.NoteFragment;
import com.omar.deathnote.notes.legacy.PicFragment;

public class FragmentCreator {
	private	FragmentManager fm;
	private	FragmentTransaction fTrans;
	private	TreeMap<String, String> tempMap;
	private	Fragment tempFragment;
	private  TreeMap<String, String> fragList;
	
 
	
	public FragmentCreator(FragmentManager fm){
		this.fm = fm;
	}

	public TreeMap<String, String> createFragment(String cont1, String cont2, Constants.Frags type, int fragCount, long
			noteId, TreeMap<String, String> list ) {
		if(fragList != null)
		Log.d("fraglist length", String.valueOf(fragList.size()));
		
		
		fragList = list;
		tempMap= new TreeMap<String, String>();
	 
		 
		fTrans = fm.beginTransaction();

		String fragId = Integer.toString(fragCount);
 
		if (cont1 != null)
			tempMap.put(Constants.Flags.Cont1.name(), cont1);
		if (cont2 != null)
			tempMap.put(Constants.Flags.Cont2.name(), cont2);

		switch (type) {

		case TitleFragment:
			tempFragment = new DefaultFragment();
		

			if (tempMap != null)
				((DefaultFragment) tempFragment).loadContent(tempMap);
			((DefaultFragment) tempFragment).loadFragId(fragId);

			
			fTrans.add(R.id.noteList, ((DefaultFragment) tempFragment), fragId);
			fragList.put(fragId, Constants.Frags.TitleFragment.name());
			
			break;

		case NoteFragment:
			tempFragment = new NoteFragment();
		

			if (tempMap != null)
				((NoteFragment) tempFragment).loadContent(tempMap);
			((NoteFragment) tempFragment).loadFragId(fragId);
			
			fTrans.add(R.id.noteList, ((NoteFragment) tempFragment), fragId);
			fragList.put(fragId, Constants.Frags.NoteFragment.name());

			break;
		case LinkFragment:
			tempFragment = new LinkFragment();


			if (tempMap != null)
				((LinkFragment) tempFragment).loadContent(tempMap);
			((LinkFragment) tempFragment).loadFragId(fragId);
			
			fTrans.add(R.id.noteList, ((LinkFragment) tempFragment), fragId);
			fragList.put(fragId, Constants.Frags.LinkFragment.name());

			break;
		case PicFragment:
			tempFragment = new PicFragment();


			if (tempMap != null)
				((PicFragment) tempFragment).loadContent(tempMap);
			((PicFragment) tempFragment).loadFragId(fragId);
			((PicFragment) tempFragment).loadNoteId(String.valueOf(noteId));
			
			fTrans.add(R.id.noteList, ((PicFragment) tempFragment), fragId);
			fragList.put(fragId, Constants.Frags.PicFragment.name());
			break;
		case AudioFragment:

			tempFragment = new AudioFragment();
		

			if (tempMap != null)
				((AudioFragment) tempFragment).loadContent(tempMap);
			((AudioFragment) tempFragment).loadFragId(fragId);
			((AudioFragment) tempFragment).loadNoteId(String.valueOf(noteId));
			
			fTrans.add(R.id.noteList, ((AudioFragment) tempFragment), fragId);
			fragList.put(fragId, Constants.Frags.AudioFragment.name());
			break;
		case NoticeDialogFragment:
			throw new IllegalArgumentException("Illigal fragment type");

		}

		fTrans.commitAllowingStateLoss();
		
		Log.d("fraglist length", String.valueOf(fragList.size()));
		
		
		
		
		return fragList;
	}
	
	
	
}
