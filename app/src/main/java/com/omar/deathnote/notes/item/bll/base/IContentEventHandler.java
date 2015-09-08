package com.omar.deathnote.notes.item.bll.base;

import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public interface IContentEventHandler {

    void Init(Content content, INoteEventHandler noteEventHandler);

    void SetView(IContentView contentView);


    void DisplayView();

    void Delete();

    Content GetContent();

    void SaveData();
}
