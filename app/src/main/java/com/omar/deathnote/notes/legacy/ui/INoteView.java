package com.omar.deathnote.notes.legacy.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.legacy.item.bll.IContentEventHandler;
import com.omar.deathnote.main.MySpinnerAdapter;

import java.util.List;

/**
 * Created by omar on 8/27/15.
 */
public interface INoteView {

    void displayFragment(IContentEventHandler eventHandler, Fragment fragment);

    void removeFragment(Content content);

    void clearList(List<Content> contentList);

    void initToolbar();

    LoaderManager getSupportLoaderManager();

    FragmentManager getSupportFragmentManager();

    void onBackPressed();

    void setUpSpinner(int pos, MySpinnerAdapter.SpinnerCallback spinnerCallback);

    void setBackGround(int index);

}
