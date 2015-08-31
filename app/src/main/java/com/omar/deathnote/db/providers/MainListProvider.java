package com.omar.deathnote.db.providers;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.omar.deathnote.App;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.loaders.MainListLoader;
import com.omar.deathnote.main.bll.ItemMainList;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by omar on 8/31/15.
 */
public class MainListProvider {

    private static List<MainListProvider> mMainListProviders;
    private   LoaderManager.LoaderCallbacks<Cursor> loaderCallback;
    private IMainListCallback mainListCallback;
    private LoaderManager loaderManager;
    private int ID;

    public interface IMainListCallback {
        void onSuccess(List<ItemMainList> createList);

        void onError(String error);
    }


    public static MainListProvider I(LoaderManager loaderManager) {
        if (mMainListProviders == null) mMainListProviders = new ArrayList<>();

        for(MainListProvider item : mMainListProviders)
            if(item.loaderManager == loaderManager)
                return item;

        MainListProvider mainListProvider = new MainListProvider();

        mainListProvider.loaderManager  = loaderManager;
        mMainListProviders.add(mainListProvider);
        mainListProvider.ID = mMainListProviders.indexOf(mainListProvider);

        return mainListProvider;
    }

    private ItemMainList create(Cursor _cursor) {
        ItemMainList item = new ItemMainList();

        {
            item.title = _cursor.getString(_cursor.getColumnIndex(DB.COLUMN_TITLE));
            item.timedate = _cursor.getString(_cursor.getColumnIndex(DB.COLUMN_TIMEDATE));
            item.id = _cursor.getInt(_cursor.getColumnIndex(DB.COLUMN_ID));
            item.img = Constants.select_images[_cursor.getInt(_cursor.getColumnIndex(DB.COLUMN_STYLE))];

        }
        return item;

    }

    public List<ItemMainList> createList(Cursor cursor) {
        List<ItemMainList> data = new ArrayList<>();
        if (cursor != null) while (!cursor.isAfterLast()) {
            data.add(create(cursor));
            cursor.moveToNext();
        }

        return data;
    }

    public void GetMainList(IMainListCallback callback,  int style) {
        loaderCallback = getLoaderCallback(style);
        mainListCallback = callback;

        if (loaderManager.getLoader(ID) != null) loaderManager.destroyLoader(ID);
        loaderManager.initLoader(ID, null, loaderCallback);

    }

    private LoaderManager.LoaderCallbacks<Cursor> getLoaderCallback(final int style) {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
                DB db = DB.getInstance(App.getContext());
                db.open();


                return new MainListLoader(App.getContext(), db, style);

            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                mainListCallback.onSuccess(createList(cursor));

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }
}
