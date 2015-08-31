package com.omar.deathnote.db.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.db.DB;

import java.util.concurrent.TimeUnit;

/**
 * Created by omar on 8/31/15.
 */
public class DeleteNoteLoader extends CursorLoader {

    private DB db;
    private int id;

    public DeleteNoteLoader (Context context, DB db, int id) {
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

