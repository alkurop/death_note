package com.omar.deathnote.notes.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.LinearLayout;
import com.omar.deathnote.models.ContentItem;
import com.omar.deathnote.notes.item.bll.IContentEventHandler;

import java.util.List;

/**
 * Created by omar on 8/27/15.
 */
public interface INoteView {


    void DisplayFragment(int UID, Fragment fragment);

    void RemoveFragment(int UID);


}
