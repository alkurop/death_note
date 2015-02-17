package com.omar.deathnote.picview;


import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.omar.deathnote.R;

public class SingleViewFragment extends Fragment {


    private static final String ARG_IMAGE_PATH = "path";

    public static SingleViewFragment buildWithResource(String path){
        Bundle args = new Bundle();
        /*args.putInt(ARG_IMAGE_RESOURCE, res);*/
        args.putString (ARG_IMAGE_PATH, path);

        SingleViewFragment fragment = new SingleViewFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        
        return inflater.inflate(R.layout.single_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	String path =  getArguments().getString (ARG_IMAGE_PATH);
        ImageView imageView = (ImageView) getView().findViewById(R.id.imageview);
      /*  imageView.setImageResource(resource);*/
        Uri uri = Uri.fromFile(new File(path));
        imageView.setImageURI(Uri.fromFile(new File(path)));
       /* Log.d("image uri ===>>", uri.getPath());*/
    }	
}

