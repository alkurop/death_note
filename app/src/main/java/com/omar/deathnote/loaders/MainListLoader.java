package com.omar.deathnote.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.db.DB;

/**
 * Created by omar on 8/29/15.
 */
public class MainListLoader extends CursorLoader {

    private DB db;
    private int style;


    public MainListLoader(Context context, DB db, int style) {
        super(context);
        this.db = db;
        this.style = style;
    }

    @Override
    public Cursor loadInBackground() {
        return  style == 0 ? db.getAllData() : db.getByStyle(style);
    }
}
