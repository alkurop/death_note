package com.omar.deathnote.db.providers;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.loaders.OpenNoteLoader;
import com.omar.deathnote.models.NoteModel;


/**
 * Created by omar on 8/31/15.
 */
public class OpenNoteProvider {

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallback;
    private IOpenNoteCallback openNoteCallback;
    private LoaderManager loaderManager;
    private int ID;

    public interface IOpenNoteCallback {
        void onSuccess(NoteModel noteModel);

        void onError(String error);
    }

   private   Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            NoteModel model  =  (NoteModel)msg.getData().getSerializable(Constants.DATA);
            openNoteCallback.onSuccess(model);
            return true;
        }
    });


    public static OpenNoteProvider I(LoaderManager loaderManager) {
        OpenNoteProvider saveNoteProvider = new OpenNoteProvider();
        saveNoteProvider.loaderManager = loaderManager;
        saveNoteProvider.ID = Constants.LOADERS.LOAD_NOTE.ordinal();
        return saveNoteProvider;
    }

    public void LoadNote(int id, IOpenNoteCallback callback) {
        loaderCallback = getLoaderCallback(id);
        openNoteCallback = callback;
        if (loaderManager.getLoader(ID) != null) loaderManager.destroyLoader(ID);
        loaderManager.initLoader(ID, null, loaderCallback).forceLoad();

    }

    private LoaderManager.LoaderCallbacks<Cursor> getLoaderCallback(final int noteId) {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
                return new OpenNoteLoader(noteId);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

                Message message = new Message();
                Bundle b = new Bundle();
                b.putSerializable(Constants.DATA, NoteModel.create(noteId, data));
                message.setData(b);
                handler.sendMessage(message);
            }


            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        };
    }
}
