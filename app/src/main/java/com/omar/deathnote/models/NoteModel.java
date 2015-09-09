package com.omar.deathnote.models;

import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 9/7/15.
 */
public class NoteModel implements Serializable{

    private int id = -1;

    private List<Content> contentList = new ArrayList();

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

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }


    public static NoteModel create(int id, Cursor cursor) {
        NoteModel noteModel = new NoteModel();
        noteModel.setId(id);
        List<Content> contentList = new ArrayList<>();

        if (cursor != null) {

            while (cursor.moveToNext()) {
                contentList.add(Content.create(cursor));
            }
        }
        cursor.close();
        noteModel.setContentList(contentList);
        return noteModel;
    }


}
