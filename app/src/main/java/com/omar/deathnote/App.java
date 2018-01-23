package com.omar.deathnote;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import com.omar.deathnote.dagger.ComponentContainer;
import com.omar.deathnote.main.bll.IMainEventHandler;
import com.omar.deathnote.main.bll.MainPresenter;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.bll.NotePresenter;

import timber.log.Timber;

/**
 * Created by omar on 8/27/15.
 */
public class App extends Application {
    private static IMainEventHandler mainPresenter;
    private static INoteEventHandler notePresenter;
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;


        ComponentContainer.getInstance().initialize(this);



        Stetho.initializeWithDefaults(this);


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static IMainEventHandler getMainPresenter() {
        if (mainPresenter == null)
            mainPresenter = new MainPresenter();
        return mainPresenter;
    }

    public static INoteEventHandler getNotePresenter() {
        if (notePresenter == null)
            notePresenter = new NotePresenter();
        return notePresenter;
    }

    public static Context getContext() {
        return context;
    }

}
