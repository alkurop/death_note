package com.omar.deathnote.db.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.notes.ui.NoteActivity;
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

        db.deleteNoteTable(NoteActivity.getId());
        db.createNoteTable(NoteActivity.getId());
        for (FragContent listItem : fragsArrayList) {

            db.addFragment(NoteActivity.getId(), listItem.getType(),
                    listItem.getCont1(), listItem.getCont2());

        }

        return null;
    }

}
