package com.omar.deathnote.models;

import android.database.Cursor;

import com.omar.deathnote.db.DB;
import com.omar.deathnote.notes.ContentType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 9/7/15.
 */
public class NoteModel implements Serializable {

    private int id = -1;

    private List<ContentType> contentList = new ArrayList();

    private int style;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ContentType> getContentList() {
        return contentList;
    }

    public void setContentList(List<ContentType> contentList) {
        this.contentList = contentList;
    }


    public static NoteModel create(int id, Cursor cursor) {
        NoteModel noteModel = new NoteModel();
        noteModel.setId(id);
        List<ContentType> contentList = new ArrayList<>();

        if (cursor != null) {

            while (cursor.moveToNext()) {
                noteModel.setStyle(cursor.getInt(cursor.getColumnIndex(DB.COLUMN_STYLE)));
                //contentList.add(ContentType.create(cursor));
            }
        }
        cursor.close();
        noteModel.setContentList(contentList);
        return noteModel;
    }


    public static NoteModel createEmpty() {
        NoteModel noteModel = new NoteModel();
        //noteModel.getContentList().add(ContentType.TITLE);
        noteModel.getContentList().add(ContentType.NOTE);
        return noteModel;
    }

}
