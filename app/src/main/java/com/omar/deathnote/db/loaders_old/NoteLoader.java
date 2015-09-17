package com.omar.deathnote.db.loaders_old;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.notes.ui.NoteActivity_old;

/**
 * Created by omar on 8/31/15.
 */
public class NoteLoader extends CursorLoader {
  private  DB db;

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