package com.omar.deathnote.picview;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.omar.deathnote.R;

public class SingleViewActivity extends FragmentActivity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_LIST = "list";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_viewpager);

		int position = getIntent().getIntExtra(EXTRA_ID, 0);

		ArrayList<String> list = getIntent()
				.getStringArrayListExtra(EXTRA_LIST);
		
		
		
		/*Log.d("position", String.valueOf(position));*/
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagesPagerAdapter(getSupportFragmentManager(),
				this, list));
		pager.setCurrentItem(position);
	}
}
