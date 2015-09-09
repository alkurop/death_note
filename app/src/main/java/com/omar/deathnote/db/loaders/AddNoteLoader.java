package com.omar.deathnote.db.loaders;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import com.omar.deathnote.App;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.MyContentProvider;
import com.omar.deathnote.models.NoteModel;
import com.omar.deathnote.utility.JsonHelper;

/**
 * Created by omar on 9/8/15.
 */
public class AddNoteLoader extends AsyncTaskLoader<Integer> {

    private NoteModel noteModel;

    public AddNoteLoader(NoteModel noteModel) {
        super(App.getContext());
        this.noteModel = noteModel;
    }

    @Override
    public Integer loadInBackground() {


        Uri u  = Uri.parse("content://" + MyContentProvider.AUTHORITY + "/" + MyContentProvider.MAIN_TABLE_PATH + "/"
                +  MyContentProvider.URI_MATCHER.INSERT_NOTE.ordinal() + "/" +
                0);

        ContentValues cv = new ContentValues();
        cv.put(Constants.DATA, JsonHelper.makeJson(noteModel));
        return Integer.parseInt(App.getContext().getContentResolver().insert(u, cv).getLastPathSegment());

    }



}
