package com.omar.deathnote.db.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.notes.ui.NoteActivity;

/**
 * Created by omar on 8/31/15.
 */
public class StyleLoader  extends CursorLoader {
    private DB db;

    public StyleLoader(Context context, DB db) {
        super(context);
        this.db = db;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor;
        cursor = db.fetchRec(NoteActivity.getId());
        return cursor;
    }
}