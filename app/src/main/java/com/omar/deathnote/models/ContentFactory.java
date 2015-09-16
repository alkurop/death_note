package com.omar.deathnote.models;

/**
 * Created by omar on 9/15/15.
 */
public class ContentFactory  {
     public static Content getContent(Content.ContentType type) {
        return new Content(type);
    }
}
