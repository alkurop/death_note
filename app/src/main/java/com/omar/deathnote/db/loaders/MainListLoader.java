package com.omar.deathnote.db.loaders;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.App;
import com.omar.deathnote.db.MyContentProvider;

/**
 * Created by omar on 8/29/15.
 */
public class MainListLoader extends CursorLoader {

    private int style;


    public MainListLoader( int style) {

        super(App.getContext());
        this.style = style;
    }

    @Override
    public Cursor loadInBackground() {
        Uri u  = Uri.parse("content://" + MyContentProvider.AUTHORITY + "/" + MyContentProvider.MAIN_TABLE_PATH + "/" + MyContentProvider.URI_MATCHER.GET_NOTES_STYLE.ordinal() + "/" +
                style);
         return App.getContext().getContentResolver().query(u, null, null, null, null);

    }

}
