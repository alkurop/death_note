package com.omar.deathnote.models;

/**
 * Created by omar on 9/7/15.
 */
public class ContentItem {
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




    public ContentItem(ContentType type) {
        this.type = type;

    }

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public ContentType getType() {
        return type;
    }

}
