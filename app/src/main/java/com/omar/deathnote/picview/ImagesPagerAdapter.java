package com.omar.deathnote.picview;

import java.util.ArrayList;

import com.omar.deathnote.Constants;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ImagesPagerAdapter extends FragmentPagerAdapter {
	public ImagesPagerAdapter(FragmentManager fm, ArrayList<String> list) {
		super(fm);

		values = list;
	}

	private ArrayList<String> values = new ArrayList<String>();

	@Override
	public Fragment getItem(int i) {
		Bundle args = new Bundle();
		args.putString(Constants.PATH, values.get(i));

		SingleViewFragment fragment = new SingleViewFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public int getCount() {
		return values.size();
	}
}