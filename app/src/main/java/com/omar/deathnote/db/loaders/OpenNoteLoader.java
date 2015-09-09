package com.omar.deathnote.db.loaders;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.App;
import com.omar.deathnote.db.MyContentProvider;

/**
 * Created by omar on 9/9/15.
 */
public class OpenNoteLoader extends CursorLoader

{

    private int id;

    public OpenNoteLoader(int id) {
        super(App.getContext());
        this.id = id;
    }

    @Override
    public Cursor loadInBackground() {
        Uri u  = Uri.parse("content://" + MyContentProvider.AUTHORITY + "/" + MyContentProvider.MAIN_TABLE_PATH + "/"
                +  MyContentProvider.URI_MATCHER.GET_NOTE.ordinal() + "/" +
                id);

        return App.getContext().getContentResolver().query(u,null,null,null,null);
    }

}