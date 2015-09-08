package com.omar.deathnote.notes.bll;

import android.content.Intent;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.ui.INoteView;

/**
 * Created by omar on 8/27/15.
 */
public interface INoteEventHandler {

      void Init(int id);

      void InitEmpty();

      void DisplayView();

      void SetView(INoteView view);

      void GetContentId(Intent intent);

      void LoadContent(int id);

      void CreateEmptyContent();

      void DeleteContentItem(Content content);

      void DisplayContent();

      void SaveContent();
}
