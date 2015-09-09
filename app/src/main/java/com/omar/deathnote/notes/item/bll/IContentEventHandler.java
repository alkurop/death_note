package com.omar.deathnote.notes.item.bll;

import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public interface IContentEventHandler {

    void init(Content content, INoteEventHandler noteEventHandler);

    void setView(IContentView contentView);


    void displayView();

    void delete();

    Content getContent();

    void saveData();
}
