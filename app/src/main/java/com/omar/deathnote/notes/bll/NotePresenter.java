package com.omar.deathnote.notes.bll;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.providers.OpenNoteProvider;
import com.omar.deathnote.db.providers.SaveNoteProvider;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.models.NoteModel;
import com.omar.deathnote.notes.item.bll.AudioItemPresenter;
import com.omar.deathnote.notes.item.bll.ContentItemPresenter;
import com.omar.deathnote.notes.item.bll.IContentEventHandler;
import com.omar.deathnote.notes.item.bll.PicItemPresenter;
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
    boolean allowDB = true;

    @Override
    public void init(NoteModel noteModel) {
        this.noteModel = noteModel;
        displayView();

    }

    @Override
    public void initEmpty() {
        createEmptyContent();
        displayView();
    }

    @Override
    public void displayView() {
        view.initToolbar();
        view.clearList(noteModel.getContentList());
        GenerateEventHandlers();
        displayContent();
    }

    @Override
    public void setView(INoteView _view) {
        view = _view;
    }

    @Override
    public void getContentId(Intent intent) {
        int id = intent.getIntExtra(Constants.ID, -1);
        if (id == -1) {
            initEmpty();

        }else
            loadContent(id);
    }

    @Override
    public void loadContent(int id) {
        OpenNoteProvider.I(view.getSupportLoaderManager()).LoadNote(id, new OpenNoteProvider.IOpenNoteCallback() {
            @Override
            public void onSuccess(NoteModel noteModel) {
                init(noteModel);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @Override
    public void createEmptyContent() {
        noteModel = new NoteModel();
        noteModel.getContentList().add(new Content(Content.ContentType.TITLE));
        noteModel.getContentList().add(new Content(Content.ContentType.NOTE));
    }

    @Override
    public void deleteContentItem(Content item) {
        if (noteModel.getContentList().size() > 2) {
            int index = noteModel.getContentList().indexOf(item);
            noteModel.getContentList().remove(index);
            eventHandlers.remove(index);
            view.removeFragment(item);
        }
    }

    private void GenerateEventHandlers() {
        eventHandlers = new ArrayList<>();
        IContentEventHandler eventHandler;
        for (Content item : noteModel.getContentList()) {
            switch (item.getType()) {
                case AUDIO:
                    eventHandler = new AudioItemPresenter();
                    break;
                case PICTURE:
                    eventHandler = new PicItemPresenter();
                    break;
                default:
                    eventHandler = new ContentItemPresenter();
                    break;

            }
            eventHandler.init(item, this);
            eventHandlers.add(eventHandler);
        }
    }

    @Override
    public void displayContent() {
        for (IContentEventHandler item : eventHandlers) {
            Fragment fragment = null;
            switch (item.getContent().getType()) {
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
            ((IContentView) fragment).setEventHandler(item);
            item.setView((IContentView) fragment);
            view.displayFragment(item.getContent(), fragment);
        }
    }

    @Override
    public void saveContent() {
        noteModel.setContentList(new ArrayList<Content>());
        for (IContentEventHandler item : eventHandlers) {
            item.saveData();
            noteModel.getContentList().add(item.getContent());
        }
    }

    @Override
    public void saveDB(final SaveDbCallback callback) {
        if(allowDB){
            allowDB = false;
        SaveNoteProvider.I(view.getSupportLoaderManager()).SaveNote(noteModel, new SaveNoteProvider.ISaveNoteCallback() {
            @Override
            public void onSuccess(int id) {
                noteModel.setId(id);
                callback.Success();
                allowDB = true;
            }

            @Override
            public void onError(String error) {
                allowDB = true;
            }
        });}
    }

    @Override
    public void shareClicked() {
        saveContent();
        saveDB(new SaveDbCallback() {
            @Override
            public void Success() {

            }
        });
    }

    @Override
    public void saveClicked() {
        saveContent();
        saveDB(new SaveDbCallback() {
            @Override
            public void Success() {
                view.onBackPressed();
            }
        });

    }

    public interface SaveDbCallback{
        void Success();
    }

}
