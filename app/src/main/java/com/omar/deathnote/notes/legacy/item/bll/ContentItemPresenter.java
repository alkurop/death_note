package com.omar.deathnote.notes.legacy.item.bll;

import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.legacy.bll.INoteEventHandler;
import com.omar.deathnote.notes.legacy.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public class ContentItemPresenter implements IContentEventHandler {


    protected Content content;
    protected INoteEventHandler noteEventHandler;
    protected IContentView contentView;

    public void init(Content content, INoteEventHandler noteEventHandler) {
        this.content = content;
        this.noteEventHandler = noteEventHandler;

    }

    @Override
    public void displayView() {
        contentView.setContent1(content.getContent1());
        contentView.setContent2(content.getContent2());
    }

    @Override
    public void delete() {
        noteEventHandler.deleteContentItem(content);
    }

    @Override
    public void setView(IContentView contentView) {
        this.contentView = contentView;
    }

    @Override
    public Content getContent() {
        return content;
    }

    @Override
    public void saveData() {
        content.setContent1(contentView.getContent1());
        content.setContent2(contentView.getContent2());
    }

    @Override
    public void requestFocus() {
        contentView.shouldRequestFocus();
    }
}
