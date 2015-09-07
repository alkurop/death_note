package com.omar.deathnote.notes.item.bll;

import com.omar.deathnote.models.ContentItem;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public interface IContentEventHandler {

    void Init(ContentItem contentItem, INoteEventHandler noteEventHandler);

    void SetView(IContentView contentView);

    void SetContent1();

    void SetContent2();

    void Delete();

    int GetUID();

    ContentItem GetContentItem();
}
