package com.omar.deathnote.notes.legacy.item.bll;

import com.omar.deathnote.notes.ContentType;
import com.omar.deathnote.notes.legacy.bll.INoteEventHandler;
import com.omar.deathnote.notes.legacy.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public interface IContentEventHandler {

    void init(ContentType content, INoteEventHandler noteEventHandler);

    void setView(IContentView contentView);

    void displayView();

    void delete();

    ContentType getContent();

    void saveData();

    void requestFocus();


}
