package com.omar.deathnote.models;

import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public class Content {
    public enum ContentType{
        TITLE,
        NOTE,
        LINK,
        AUDIO,
        PICTURE
    }
    private  ContentType  type;
    private  String content1;
    private   String content2;
    protected int UID;


    public int getUID() {
        return UID;
    }

    public Content(ContentType type) {
        this.type = type;
        UID = (int) (Math.random() * Math.random() * 1000000);

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

}
