package com.omar.deathnote.main.ui;


import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by omar on 8/27/15.
 */
public interface IMainView {
    RecyclerView getRecyclerView();

    LoaderManager getSupportLoaderManager();

    void openEmptyNote();

    void openNote(int id);

}
