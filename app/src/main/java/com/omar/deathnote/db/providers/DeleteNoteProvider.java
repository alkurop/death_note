package com.omar.deathnote.db.providers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.loaders.DeleteNoteLoader;


/**
 * Created by omar on 8/31/15.
 */
public class DeleteNoteProvider {

    private LoaderManager.LoaderCallbacks<Integer> loaderCallback;
    private IDeleteNoteCallback deleteNoteCallback;
    private LoaderManager loaderManager;
    private int ID;

    public interface IDeleteNoteCallback {
        void onSuccess( );
        void onError(String error);
    }

   private   Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            deleteNoteCallback.onSuccess();
            return true;
        }
    });


    public static DeleteNoteProvider I(LoaderManager loaderManager) {
        DeleteNoteProvider deleteNoteProvider = new DeleteNoteProvider();
        deleteNoteProvider.loaderManager = loaderManager;
        deleteNoteProvider.ID = Constants.LOADERS.DELETE_SOME_NOTE.ordinal();
        return deleteNoteProvider;
    }

    public void DeleteNote(int id, IDeleteNoteCallback callback) {
        loaderCallback = getLoaderCallback(id);
        deleteNoteCallback = callback;
        if (loaderManager.getLoader(ID) != null) loaderManager.destroyLoader(ID);
        loaderManager.initLoader(ID, null, loaderCallback).forceLoad();

    }

    private LoaderManager.LoaderCallbacks<Integer> getLoaderCallback(final int noteId) {
        return new LoaderManager.LoaderCallbacks<Integer>() {

            @Override
            public Loader<Integer> onCreateLoader(int id, Bundle bundle) {
                return new DeleteNoteLoader(noteId);
            }
            @Override
            public void onLoadFinished(Loader<Integer> loader, Integer data) {
                handler.sendEmptyMessage(noteId);
            }
            @Override
            public void onLoaderReset(Loader<Integer> loader) {
            }
        };
    }
}
