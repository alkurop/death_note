package com.omar.deathnote.db.loaders;

import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import com.omar.deathnote.App;
import com.omar.deathnote.db.MyContentProvider;

/**
 * Created by omar on 8/31/15.
 */
public class DeleteNoteLoader extends AsyncTaskLoader<Integer> {


    private int id;

    public DeleteNoteLoader(int id) {
        super(App.getContext());
        this.id = id;

    }

    @Override
    public Integer loadInBackground() {
        Uri u  = Uri.parse("content://" + MyContentProvider.AUTHORITY + "/" + MyContentProvider.MAIN_TABLE_PATH + "/"
                +  MyContentProvider.URI_MATCHER.DELETE_NOTE.ordinal() + "/" +
                id);
        return App.getContext().getContentResolver().delete(u,null, null);
    }
}

