package com.omar.deathnote.db.loaders_old;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.notes.ui.NoteActivity_old;
import com.omar.deathnote.utility.FragContent;

import java.util.ArrayList;

/**
 * Created by omar on 8/31/15.
 */
public class SaveNoteLoader extends CursorLoader {
   private DB db;
    private  ArrayList<FragContent> fragsArrayList;

    public SaveNoteLoader(Context context, DB db, Bundle bundle) {
        super(context);
        this.db = db;
        this.fragsArrayList = bundle
                .getParcelableArrayList(Constants.FRAGMENT_ARRAY_LIST);

    }

    @Override
    public Cursor loadInBackground() {

        db.deleteNoteTable(NoteActivity_old.getId());
        db.createNoteTable(NoteActivity_old.getId());
        for (FragContent listItem : fragsArrayList) {

           // db.addContentItem(NoteActivity_old.getId(), 0, listItem.getCont1(), listItem.getCont2());

        }

        return null;
    }

}
