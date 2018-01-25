package com.omar.deathnote.notes.legacy.bll;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.omar.deathnote.Constants;
import com.omar.deathnote.db.providers.OpenNoteProvider;
import com.omar.deathnote.db.providers.SaveNoteProvider;
import com.omar.deathnote.main.MySpinnerAdapter;
import com.omar.deathnote.mediaplay.controls.MediaManager;
import com.omar.deathnote.models.NoteModel;
import com.omar.deathnote.notes.ContentType;
import com.omar.deathnote.notes.add.bll.AddDialogPresenter;
import com.omar.deathnote.notes.add.ui.AddDialog;
import com.omar.deathnote.notes.legacy.item.bll.IAudioEventHandler;
import com.omar.deathnote.notes.legacy.item.bll.IContentEventHandler;
import com.omar.deathnote.notes.legacy.item.ui.IContentView;
import com.omar.deathnote.notes.legacy.ui.INoteView;

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
        MediaManager.I().stopAudio();
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
    public void deleteContentItem(ContentType item) {
        if (noteModel.getContentList().size() > 2) {
            int index = noteModel.getContentList().indexOf(item);
            noteModel.getContentList().remove(index);
            if (eventHandlers.get(index) instanceof IAudioEventHandler)
                MediaManager.I().remmoveMediaClient(((IAudioEventHandler)
                        eventHandlers.get(index)).getMediaClient());
            eventHandlers.remove(index);
            view.removeFragment(item);
        }
    }

    @Override
    public void addContentItem(ContentType content) {
        noteModel.getContentList().add(content);
    }

    private void generateEventHandlersList() {
        eventHandlers = new ArrayList<>();
        for (ContentType item : noteModel.getContentList()) {
            generateEventHandler(item);
        }
    }

    private IContentEventHandler generateEventHandler(ContentType content) {
        IContentEventHandler eventHandler = null;
   /*     switch (content.getType()) {
            case AUDIO_FILE:
            case AUDIO_RECORD:
                eventHandler = new AudioItemEventHandler();
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

        if (eventHandler instanceof IAudioEventHandler) MediaManager.I().addMediaCLient(((IAudioEventHandler)
                eventHandler).getMediaClient());*/
        return eventHandler;
    }

    @Override
    public void displayEventHandlerList() {
        for (IContentEventHandler item : eventHandlers) {
            displayEventHandler(item);

        }
    }

    private void displayEventHandler(IContentEventHandler eventHandler) {
        Fragment fragment = null;
       /* switch (eventHandler.getContent().getType()) {
            case LINK:
                fragment = new LinkFragment();
                break;
            case TITLE:
                fragment = new TitleFragment();
                break;
            case AUDIO_FILE:
            case AUDIO_RECORD:
                fragment = new AudioFragment();
                break;
            case PICTURE_FILE:
            case PICTURE_CAPTURE:
                break;
            case NOTE:
                fragment = new NoteFragment();
                break;
        }*/
        ((IContentView) fragment).setEventHandler(eventHandler);
        eventHandler.setView((IContentView) fragment);
        view.displayFragment(eventHandler, fragment);
    }

    @Override
    public void saveContent() {
        noteModel.setContentList(new ArrayList<ContentType>());
        for (IContentEventHandler item : eventHandlers) {
            /*if (item.getContent().getType() != ContentType.ContentType.AUDIO_RECORD) {
                item.saveData();
                noteModel.getContentList().add(item.getContent());
            }*/
        }
    }

    @Override
    public void saveDB() {

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
            public void addContent(ContentType content) {
                addContentItem(content);
                IContentEventHandler eventHandler = generateEventHandler(content);
                displayEventHandler(eventHandler);
                eventHandler.requestFocus();
            }
        });
        AddDialog addDialog = new AddDialog();
        addDialog.setEventHandler(dialogPresenter);
        dialogPresenter.setView(addDialog);
        addDialog.show(view.getSupportFragmentManager(), "");
    }
}