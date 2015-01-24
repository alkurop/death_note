package com.omar.deathnote.picview;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;



public class ImagesPagerAdapter extends FragmentPagerAdapter{

    public Context ctx;
    public ArrayList <String> values = new ArrayList<String>();
    
    public ImagesPagerAdapter(FragmentManager fm,Context ctx, ArrayList<String> list) {
        super(fm);
        this.ctx=ctx;
        values = list;
    }

    public Integer[] mThumbIds = {
            
    };

    @Override
    public Fragment getItem(int i) {
        Bundle args = new Bundle();
        args.putString ("path",values.get(i) );

        SingleViewFragment fragment = new SingleViewFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return values.size();
    }
}