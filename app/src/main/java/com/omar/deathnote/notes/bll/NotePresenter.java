package com.omar.deathnote.notes.bll;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.providers.OpenNoteProvider;
import com.omar.deathnote.db.providers.SaveNoteProvider;
import com.omar.deathnote.dialogs.add_dialog.bll.AddDialogPresenter;
import com.omar.deathnote.dialogs.add_dialog.ui.AddDialog;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.models.NoteModel;
import com.omar.deathnote.notes.item.bll.AudioItemPresenter;
import com.omar.deathnote.notes.item.bll.ContentItemPresenter;
import com.omar.deathnote.notes.item.bll.IContentEventHandler;
import com.omar.deathnote.notes.item.bll.PicItemPresenter;
import com.omar.deathnote.notes.item.ui.IContentView;
import com.omar.deathnote.notes.item.ui.LinkFragment;
import com.omar.deathnote.notes.item.ui.NoteFragment;
import com.omar.deathnote.notes.item.ui.TitleFragment;
import com.omar.deathnote.notes.ui.INoteView;
import com.omar.deathnote.spinner.MySpinnerAdapter;

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
    public void init(NoteModel noteModel) {
        this.noteModel = noteModel;
        displayView();
        setUpSpinner(noteModel.getStyle() - 1);

    }

    @Override
    public void initEmpty() {
        noteModel = NoteModel.createEmpty();
        displayView();
        setUpSpinner(0);
        noteModel.setStyle(1);
    }

    @Override
    public void displayView() {
        view.initToolbar();
        view.clearList(noteModel.getContentList());
        generateEventHandlersList();
        displayEventHandlerList();

    }

    private void setUpSpinner(final int pos) {
        view.setUpSpinner(pos, new MySpinnerAdapter.SpinnerCallback() {
            @Override
            public void onItemSelected(int position) {
                view.setBackGround(position);
                noteModel.setStyle(position + 1);
            }
        });
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
        } else loadContent(id);
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
    public void deleteContentItem(Content item) {
        if (noteModel.getContentList().size() > 2) {
            int index = noteModel.getContentList().indexOf(item);
            noteModel.getContentList().remove(index);
            eventHandlers.remove(index);
            view.removeFragment(item);
        }
    }

    @Override
    public void addContentItem(Content content) {
        noteModel.getContentList().add(content);

    }

    private void generateEventHandlersList() {
        eventHandlers = new ArrayList<>();
        for (Content item : noteModel.getContentList()) {
            generateEventHandler(item);
        }
    }

    private IContentEventHandler generateEventHandler(Content content) {
        IContentEventHandler eventHandler;
        switch (content.getType()) {
            case AUDIO_FILE:
            case AUDIO_RECORD:
                eventHandler = new AudioItemPresenter();
                break;
            case PICTURE_FILE:
            case PICTURE_CAPTURE:
                eventHandler = new PicItemPresenter();
                break;
            default:
                eventHandler = new ContentItemPresenter();
                break;

        }
        eventHandler.init(content, this);
        eventHandlers.add(eventHandler);
        return eventHandler;
    }

    @Override
    public void displayEventHandlerList() {
        for (IContentEventHandler item : eventHandlers) {
            displayEventHandler(item, false);
        }
    }

    private void displayEventHandler(IContentEventHandler eventHandler, boolean shouldRequestFocus) {
        Fragment fragment = null;
        switch (eventHandler.getContent().getType()) {
            case LINK:
                fragment = new LinkFragment();
                break;
            case TITLE:
                fragment = new TitleFragment();
                break;
            case AUDIO_FILE:
            case AUDIO_RECORD:

                break;
            case PICTURE_FILE:
            case PICTURE_CAPTURE:

                break;
            case NOTE:
                fragment = new NoteFragment();
                break;
        }
        ((IContentView) fragment).setEventHandler(eventHandler);
        eventHandler.setView((IContentView) fragment);
        view.displayFragment(eventHandler, fragment, shouldRequestFocus);
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
    public void saveDB( ) {

        SaveNoteProvider.I(view.getSupportLoaderManager()).SaveNote(noteModel, new SaveNoteProvider.ISaveNoteCallback() {
            @Override
            public void onSuccess(int id) {
                noteModel.setId(id);
            }
            @Override
            public void onError(String error) {
            }
        });
    }

    @Override
    public void shareClicked() {
        saveContent();
        saveDB();
    }

    @Override
    public void saveClicked() {
        saveContent();
        saveDB();
        view.onBackPressed();
    }

    @Override
    public void fabClicked() {
        AddDialogPresenter dialogPresenter = new AddDialogPresenter();
        dialogPresenter.init(new AddDialogPresenter.IAddDialogCallback() {
            @Override
            public void addContent(Content content) {
                addContentItem(content);
                IContentEventHandler eventHandler =  generateEventHandler(content);
                displayEventHandler(eventHandler, true);
            }
        });
        AddDialog addDialog = new AddDialog();
        addDialog.setEventHandler(dialogPresenter);
        dialogPresenter.setView(addDialog);
        addDialog.show(view.getSupportFragmentManager(), "");
    }
}
