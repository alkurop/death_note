package com.omar.deathnote.db.providers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.loaders.AddNoteLoader;
import com.omar.deathnote.models.NoteModel;


/**
 * Created by omar on 8/31/15.
 */
public class SaveNoteProvider {

    private   LoaderManager.LoaderCallbacks<Integer> loaderCallback;
    private ISaveNoteCallback saveNoteCallback;
    private LoaderManager loaderManager;
    private int ID;
    public interface ISaveNoteCallback {
        void onSuccess(int id);
        void onError(String error);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            saveNoteCallback.onSuccess(msg.what);
            return true;
        }
    });


    public static SaveNoteProvider I(LoaderManager loaderManager) {
        SaveNoteProvider saveNoteProvider = new SaveNoteProvider();
        saveNoteProvider.loaderManager  = loaderManager;
        saveNoteProvider.ID = Constants.LOADERS.SAVE_NOTE.ordinal();
        return saveNoteProvider;
    }

    public void SaveNote(NoteModel noteModel, ISaveNoteCallback callback) {
        loaderCallback = getLoaderCallback(noteModel);
        saveNoteCallback = callback;
        if (loaderManager.getLoader(ID) != null) loaderManager.destroyLoader(ID);
        loaderManager.initLoader(ID, null, loaderCallback).forceLoad();

    }

    private LoaderManager.LoaderCallbacks<Integer> getLoaderCallback(final NoteModel noteModel) {
        return new LoaderManager.LoaderCallbacks<Integer>() {
            @Override
            public Loader<Integer> onCreateLoader(int id, Bundle bundle) {
                return new AddNoteLoader( noteModel);
            }

            @Override
            public void onLoadFinished(Loader<Integer> loader, Integer data) {
                 handler.sendEmptyMessage(data);
            }

            @Override
            public void onLoaderReset(Loader<Integer> loader) {
            }
        };
    }
}
