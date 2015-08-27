package com.omar.deathnote.pref;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

 

public class Contact extends Activity {
 
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
	 
		 initContactIntent();
		super.onCreate(savedInstanceState);
	}

	private void initContactIntent() {
		Intent contact = new Intent(Intent.ACTION_VIEW);  
		Uri data = Uri.parse("mailto:?subject=" + "Death Note Feedback" +   "&to=" + "alkurop@gmail.com");  
		contact.setData(data);  
		startActivity(contact);  
			 finish();
		}	
	
 

	 
	
	
	
}
