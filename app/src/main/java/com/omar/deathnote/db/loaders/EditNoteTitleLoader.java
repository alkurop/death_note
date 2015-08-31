package com.omar.deathnote.db.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.notes.ui.NoteActivity;

/**
 * Created by omar on 8/31/15.
 */
public class EditNoteTitleLoader  extends CursorLoader {
    private DB db;
    private Bundle bundle;

    public EditNoteTitleLoader(Context context, DB db, Bundle bundle) {
        super(context);
        this.db = db;
        this.bundle = bundle;
    }

    @Override
    public Cursor loadInBackground() {

        db.editRec(NoteActivity.getId(), NoteActivity.getStyle(), bundle.getString(Constants.Flags.Cont1.name()));

        return null;
    }

}
