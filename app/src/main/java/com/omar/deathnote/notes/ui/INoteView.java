package com.omar.deathnote.notes.ui;

import android.support.v4.app.Fragment;
import com.omar.deathnote.models.Content;

import java.util.List;

/**
 * Created by omar on 8/27/15.
 */
public interface INoteView {

    void DisplayFragment(Content content, Fragment fragment);

    void RemoveFragment(Content content);

    void ClearList(List<Content> contentList);

    void InitToolbar();




}
