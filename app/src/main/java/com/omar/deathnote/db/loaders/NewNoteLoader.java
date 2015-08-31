package com.omar.deathnote.db.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.notes.ui.NoteActivity;

/**
 * Created by omar on 8/31/15.
 */
public class NewNoteLoader extends CursorLoader {
    DB db;

    public NewNoteLoader(Context context, DB db) {
        super(context);
        this.db = db;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor;
        db.addRec(NoteActivity.getStyle(), "");
        cursor = db.fetchLast();
        return cursor;
    }

}
