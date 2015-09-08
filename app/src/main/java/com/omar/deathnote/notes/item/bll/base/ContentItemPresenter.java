package com.omar.deathnote.notes.item.bll.base;

import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.ui.IContentView;

/**
 * Created by omar on 9/7/15.
 */
public class ContentItemPresenter implements IContentEventHandler {


    protected Content content;
    protected INoteEventHandler noteEventHandler;
    protected IContentView contentView;



    public void Init(Content content, INoteEventHandler noteEventHandler){
        this.content = content;
        this.noteEventHandler = noteEventHandler;
    }

    @Override
    public void DisplayView() {
        contentView.SetContent1(content.getContent1());
        contentView.SetContent2(content.getContent2());
    }

    @Override
    public void Delete() {
        noteEventHandler.DeleteContentItem(content);
    }

    @Override
    public void SetView(IContentView contentView) {
        this.contentView = contentView;
    }



    @Override
    public Content GetContent() {
        return content;
    }


    @Override
    public void SaveData() {
        content.setContent1(contentView.GetContent1());
        content.setContent2(contentView.GetContent2());
    }
}
