package com.omar.deathnote.db.providers;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.omar.deathnote.App;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.MyContentProvider;
import com.omar.deathnote.db.loaders.MainListLoader;
import com.omar.deathnote.models.ItemMainList;

import java.util.List;


/**
 * Created by omar on 8/31/15.
 */
public class MainListProvider {

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallback;
    private IMainListCallback mainListCallback;
    private LoaderManager loaderManager;
    private int ID;
    private MainListLoader loader;
    private static ContentObserver contentObserver;
    private static Handler handler;

    public interface IMainListCallback {
        void onSuccess(List<ItemMainList> createList);

        void onError(String error);
    }

    public   void initContentObserver(){
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (loaderManager.getLoader(ID) != null) loaderManager.destroyLoader(ID);
                loaderManager.initLoader(ID, null, loaderCallback);
                return false;
            }
        });
        contentObserver = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                handler.sendEmptyMessage(0);
            }
        };

        App.getContext().getContentResolver().registerContentObserver(MyContentProvider.PROVIDER_URI, true, contentObserver);
    }
    public MainListProvider(){
        initContentObserver();
    }

    public static MainListProvider I(LoaderManager loaderManager) {
        MainListProvider mainListProvider = new MainListProvider();
        mainListProvider.loaderManager = loaderManager;
        mainListProvider.ID = Constants.LOADERS.LOAD_NOTE.ordinal();
        return mainListProvider;
    }

    public void GetMainList(IMainListCallback callback, int style) {
        loaderCallback = getLoaderCallback(style);
        mainListCallback = callback;
        if (loaderManager.getLoader(ID) != null) loaderManager.destroyLoader(ID);
        loaderManager.initLoader(ID, null, loaderCallback);

    }

    private LoaderManager.LoaderCallbacks<Cursor> getLoaderCallback(final int style) {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
                loader = new MainListLoader(style);
                return loader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                mainListCallback.onSuccess(ItemMainList.createList(cursor));
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        };
    }
}
