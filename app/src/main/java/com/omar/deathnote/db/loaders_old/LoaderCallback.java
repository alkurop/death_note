package com.omar.deathnote.db.loaders_old;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.omar.deathnote.Constants;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.db.loaders.MainListLoader;
import com.omar.deathnote.notes.ui.NoteActivity_old;
import com.omar.deathnote.utility.FragContent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
    private static LoaderCallback loaderCallback;

    public static final int SAVE_NOTE = 0;
    public static final int LOAD_NOTE = 1;
    public static final int EDIT_REC_TITLE = 2;
    public static final int LOAD_STYLE = 3;
    public static final int ADD_NEW_NOTE = 4;
    public static final int LOAD_LIST = 5;
    public static final int DELETE_SOME_NOTE = 6;

    private Context context;

    public static LoaderCallback getInstance(Context context) {

        if (loaderCallback == null) {
            loaderCallback = new LoaderCallback(context);
        }

        return loaderCallback;
    }

    private LoaderCallback(Context context) {
        this.context = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        DB db = DB.getInstance(context);
        db.open();

        switch (id) {

            case LOAD_NOTE:

                return new NoteLoader(context, db);

            case EDIT_REC_TITLE:
                return new EditNoteTitleLoader(context, db, bundle);

            case LOAD_STYLE:
                return new StyleLoader(context, db);

            case ADD_NEW_NOTE:
                return new NewNoteLoader(context, db);

            case SAVE_NOTE:
                return new SaveNoteLoader(context, db, bundle);

            case LOAD_LIST:

                return new MainListLoader(0);

            case DELETE_SOME_NOTE:

                int some = bundle.getInt(Constants.SOME);

                return new DeleteSomeNoteCursorLoader(context, db, some);

            default:
                return null;

        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case ADD_NEW_NOTE:
                NoteActivity_old.setId(cursor.getInt(cursor.getColumnIndex(DB.COLUMN_ID)));

                break;

            case LOAD_NOTE:
                if (cursor != null) {

                    while (cursor.moveToNext()) {
                        String type = cursor.getString(cursor
                                .getColumnIndex(DB.COLUMN_TYPE));
                        String cont1 = cursor.getString(cursor
                                .getColumnIndex(DB.COLUMN_CONT1));
                        String cont2 = cursor.getString(cursor
                                .getColumnIndex(DB.COLUMN_CONT2));

                        Constants.Frags eType = NoteActivity_old.setFragType(type);

                        NoteActivity_old.createFragment(cont1, cont2, eType);

                    }
                } else {
                /* Log.d ("loading saved note  ", "cursor == null"); */
                    NoteActivity_old.createFragment("", null, Constants.Frags.TitleFragment);
                    NoteActivity_old.createFragment(" ", null, Constants.Frags.NoteFragment);
                }

                break;

            case LOAD_STYLE:
            case LOAD_LIST:
                NoteActivity_old.setStyle(cursor.getInt(cursor.getColumnIndex(DB.COLUMN_STYLE)));
                break;

            case DELETE_SOME_NOTE:
                break;

            default:
                break;

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case LOAD_LIST:
                break;

            case DELETE_SOME_NOTE:
        }

    }

    private static class NoteLoader extends CursorLoader {
        DB db;

        public NoteLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor;
            cursor = db.getAllNoteData(NoteActivity_old.getId());
            return cursor;
        }

    }

    private static class StyleLoader extends CursorLoader {
        DB db;

        public StyleLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor;
            cursor = db.fetchRec(NoteActivity_old.getId());
            return cursor;
        }

    }

    private static class NewNoteLoader extends CursorLoader {
        DB db;

        public NewNoteLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor;
            db.addRec(NoteActivity_old.getStyle(), "");
            cursor = db.fetchLast();
            return cursor;
        }

    }

    private static class EditNoteTitleLoader extends CursorLoader {
        DB db;
        Bundle bundle;

        public EditNoteTitleLoader(Context context, DB db, Bundle bundle) {
            super(context);
            this.db = db;
            this.bundle = bundle;
        }

        @Override
        public Cursor loadInBackground() {

            db.editRec(NoteActivity_old.getId(), NoteActivity_old.getStyle(), bundle.getString(Constants.Flags.Cont1.name()));

            return null;
        }

    }

    private static class SaveNoteLoader extends CursorLoader {
        DB db;
        ArrayList<FragContent> fragsArrayList;

        public SaveNoteLoader(Context context, DB db, Bundle bundle) {
            super(context);
            this.db = db;
            this.fragsArrayList = bundle
                    .getParcelableArrayList(Constants.FRAGMENT_ARRAY_LIST);

        }

        @Override
        public Cursor loadInBackground() {

            db.deleteNoteTable(NoteActivity_old.getId());
            db.createNoteTable(NoteActivity_old.getId());
            for (FragContent listItem : fragsArrayList) {

                //	db.addContentItem(NoteActivity_old.getId(), 0, listItem.getCont1(), listItem.getCont2());

            }

            return null;
        }
    }

    private static class DeleteSomeNoteCursorLoader extends CursorLoader {

        private DB db;
        private int id;

        public DeleteSomeNoteCursorLoader(Context context, DB db, int id) {
            super(context);
            this.db = db;
            this.id = id;

        }

        @Override
        public Cursor loadInBackground() {
            db.delRec(id);
            db.deleteNoteTable(id);

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }

}
