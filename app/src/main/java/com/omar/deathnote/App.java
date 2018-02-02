package com.omar.deathnote;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.omar.deathnote.lagacy_db.DB;

import timber.log.Timber;

public class App extends Application {
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
        DB.DBHelper deathnote = new DB.DBHelper(this, "deathnote", null, 4);
        deathnote.getWritableDatabase().close();

    }


    public static Context getContext() {
        return context;
    }

}
