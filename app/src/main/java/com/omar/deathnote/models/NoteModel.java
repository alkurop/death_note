package com.omar.deathnote.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 9/7/15.
 */
public class NoteModel {

   private  int id;

  private   List <Content> contentList = new ArrayList();


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
}
