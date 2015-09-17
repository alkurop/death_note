package com.omar.deathnote.models;

import android.database.Cursor;
import com.omar.deathnote.db.DB;

import java.io.Serializable;

/**
 * Created by omar on 9/7/15.
 */
public class Content implements Serializable{
    public enum ContentType{
        TITLE,
        NOTE,
        LINK,
        AUDIO_FILE,
        PICTURE_FILE,
        AUDIO_RECORD,
        PICTURE_CAPTURE
    }
    private  ContentType  type;
    private  String content1;
    private   String content2;
    private int UID = (int) (Math.random() * Math.random() * 1000000);

    public int getUID() {
        return UID;
    }

    public Content(ContentType type) {
        this.type = type;
    }

    public String getContent1() {
        if(content1 == null)
        content1 = "";
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getContent2() {
        if(content2 == null)
            content2 = "";
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public ContentType getType() {
         return type;
    }

    public static Content create(Cursor cursor){
        int typIndex = cursor.getInt(cursor.getColumnIndex(DB.COLUMN_TYPE));
        String cont1 = cursor.getString(cursor.getColumnIndex(DB.COLUMN_CONT1));
        String cont2 = cursor.getString(cursor.getColumnIndex(DB.COLUMN_CONT2));

        ContentType type = ContentType.values()[typIndex];
        Content content = new Content(type);
        content.setContent1(cont1);
        content.setContent2(cont2);
        return content;
    }

}
