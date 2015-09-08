package com.omar.deathnote.notes.bll;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.models.NoteModel;
import com.omar.deathnote.notes.item.bll.base.ContentItemPresenter;
import com.omar.deathnote.notes.item.bll.base.IContentEventHandler;
import com.omar.deathnote.notes.item.ui.IContentView;
import com.omar.deathnote.notes.item.ui.NoteFragment;
import com.omar.deathnote.notes.item.ui.TitleFragment;
import com.omar.deathnote.notes.ui.INoteView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 8/27/15.
 */
public class NotePresenter implements INoteEventHandler {

    private NoteModel noteModel;
    private INoteView view;
    private List<IContentEventHandler> eventHandlers;

    @Override
    public void Init(int id) {
    }

    @Override
    public void InitEmpty() {
        CreateEmptyContent();
    }

    @Override
    public void DisplayView() {
        view.InitToolbar();
        view.ClearList(noteModel.getContentList());
        GenerateEventHandlers();
        DisplayContent();
    }

    @Override
    public void SetView(INoteView _view) {
        view = _view;
    }

    @Override
    public void GetContentId(Intent intent) {
        int id = intent.getIntExtra("id", -1);
        if (id == -1) {
            InitEmpty();
            DisplayView();
        }
    }

    @Override
    public void LoadContent(int id) {
    }

    @Override
    public void CreateEmptyContent() {
        noteModel = new NoteModel();
        noteModel.getContentList().add(new Content(Content.ContentType.TITLE));
        noteModel.getContentList().add(new Content(Content.ContentType.NOTE));
    }

    @Override
    public void DeleteContentItem(Content item) {
        if (noteModel.getContentList().size() > 2) {
            int index =  noteModel.getContentList().indexOf(item);
            noteModel.getContentList().remove(index);
            eventHandlers.remove(index);
            view.RemoveFragment(item);
        }
    }

    private void GenerateEventHandlers() {
        eventHandlers = new ArrayList<>();
        for (Content item : noteModel.getContentList()) {
            IContentEventHandler eventHandler = new ContentItemPresenter();
            eventHandler.Init(item, this);
            eventHandlers.add(eventHandler);
        }
    }

    @Override
    public void DisplayContent() {
        for (IContentEventHandler item : eventHandlers) {
            Fragment fragment = null;
            switch (item.GetContent().getType()) {
                case LINK:
                    break;
                case TITLE:
                    fragment = new TitleFragment();
                    break;
                case AUDIO:
                    break;
                case PICTURE:
                    break;
                case NOTE:
                    fragment = new NoteFragment();
                    break;
            }
            ((IContentView) fragment).SetEventHandler(item);
            item.SetView((IContentView) fragment);
            view.DisplayFragment(item.GetContent(), fragment);
        }
    }

    @Override
    public void SaveContent() {
        noteModel.setContentList(new ArrayList<Content>());
        for(IContentEventHandler item : eventHandlers){
            item.SaveData();
            noteModel.getContentList().add(item.GetContent());
        }
    }
}
