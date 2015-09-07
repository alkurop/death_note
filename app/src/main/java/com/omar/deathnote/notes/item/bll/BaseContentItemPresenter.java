package com.omar.deathnote.notes.item.bll;

import com.omar.deathnote.models.ContentItem;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
 abstract public class BaseContentItemPresenter implements IContentEventHandler{

    protected int UID;
    protected ContentItem contentItem;
    protected INoteEventHandler noteEventHandler;
    protected IContentView contentView;

    BaseContentItemPresenter() {
        UID = (int) (Math.random() * Math.random() * 1000000);

    }

    public void Init(ContentItem contentItem, INoteEventHandler noteEventHandler){
        this.contentItem = contentItem;
        this.noteEventHandler = noteEventHandler;

    }

    @Override
    public void SetView(IContentView contentView) {
        this.contentView = contentView;
    }

    @Override
    public int GetUID() {
        return UID;
    }

    @Override
    public ContentItem GetContentItem() {
        return contentItem;
    }

    @Override
    public void Delete() {
        noteEventHandler.DeleteContentItem(UID);
    }
}
