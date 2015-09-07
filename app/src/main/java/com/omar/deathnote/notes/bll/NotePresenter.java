package com.omar.deathnote.notes.bll;

import com.omar.deathnote.models.ContentItem;
import com.omar.deathnote.models.NoteModel;
import com.omar.deathnote.notes.item.bll.*;
import com.omar.deathnote.notes.ui.INoteView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 8/27/15.
 */
public class NotePresenter implements INoteEventHandler{

    private NoteModel noteModel;
    private INoteView view;
    private List<IContentEventHandler>  eventHandlers;

    @Override
    public void Init(int id){}

    @Override
    public void InitEmpty(){
        CreateEmptyContent();
        GenerateEventHandlers();
    }

    @Override
    public void SetView(INoteView _view){
        view = _view;
    }

    @Override
    public void GetContent(int id){}

    @Override
    public void CreateEmptyContent(){
        noteModel = new NoteModel();
        noteModel.getContentList().add(new ContentItem(ContentItem.ContentType.TITLE));
        noteModel.getContentList().add(new ContentItem(ContentItem.ContentType.NOTE));
    }

    @Override
    public void DeleteContentItem(int UID) {

    }

    private void GenerateEventHandlers(){
        eventHandlers = new ArrayList<>();
        for (ContentItem item : noteModel.getContentList()){
            IContentEventHandler eventHandler = null;
            switch (item.getType()){
                case TITLE:
                    eventHandler = new TitleEventHandler();
                    break;

                case NOTE:
                    eventHandler = new NoteEventHandler();
                    break;

                case PICTURE:
                    eventHandler = new PicEventHandler();
                    break;

                case AUDIO:
                    eventHandler = new AudioEventHandler();
                    break;

                case LINK:
                    eventHandler = new LinkEventHandler();
                    break;
            }
            eventHandler.Init(item, this);
            eventHandlers.add(eventHandler);
        }

    }



}
