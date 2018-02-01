package com.omar.deathnote.notes.legacy.bll;

import android.content.Intent;

import com.omar.deathnote.models.NoteModel;
import com.omar.deathnote.notes.ContentType;
import com.omar.deathnote.notes.legacy.ui.INoteView;

/**
 * Created by omar on 8/27/15.
 */
public interface INoteEventHandler {

      void init(NoteModel noteModel);

      void initEmpty();

      void displayView();

      void setView(INoteView view);

      void getContentId(Intent intent);

      void loadContent(int id);

      void deleteContentItem(ContentType content);

      void addContentItem(ContentType content);

      void displayEventHandlerList();

      void saveContent();

      void saveDB();

      void shareClicked();

      void saveClicked();

      void fabClicked();
}
