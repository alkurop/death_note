package com.omar.deathnote.db.loaders;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.App;
import com.omar.deathnote.db.DB;

import java.util.concurrent.TimeUnit;

/**
 * Created by omar on 8/29/15.
 */
public class DeleteSomeNoteLoader  extends CursorLoader {

    private DB db;
    private int id;

    public DeleteSomeNoteLoader( int id) {
        super(App.getContext());
        this.db =DB.getInstance(App.getContext());
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