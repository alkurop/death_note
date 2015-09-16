package com.omar.deathnote.notes.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.spinner.MySpinnerAdapter;

import java.util.List;

/**
 * Created by omar on 8/27/15.
 */
public interface INoteView {

    void displayFragment(Content content, Fragment fragment);

    void removeFragment(Content content);

    void clearList(List<Content> contentList);

    void initToolbar();

    LoaderManager getSupportLoaderManager();
    FragmentManager getSupportFragmentManager();

    void onBackPressed();

    void setUpSpinner(int pos, MySpinnerAdapter.SpinnerCallback spinnerCallback);

    void setBackGround(int index);


}
