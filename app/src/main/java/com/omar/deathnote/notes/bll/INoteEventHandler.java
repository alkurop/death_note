package com.omar.deathnote.notes.bll;

import com.omar.deathnote.notes.ui.INoteView;

/**
 * Created by omar on 8/27/15.
 */
public interface INoteEventHandler {

      void Init(int id);

      void InitEmpty();

      void SetView(INoteView view);

      void GetContent(int id);

      void CreateEmptyContent();

      void DeleteContentItem(int UID);
}
