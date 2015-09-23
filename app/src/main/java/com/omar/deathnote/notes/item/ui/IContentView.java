package com.omar.deathnote.notes.item.ui;

import com.omar.deathnote.notes.item.bll.IContentEventHandler;

/**
 * Created by omar on 9/7/15.
 */
public interface IContentView {
    void setEventHandler(IContentEventHandler eventHandler);

    void setContent1(String content1);

    void setContent2(String content2);

    String getContent1();

    String getContent2();

    void onDeleteClicked();

    int getLayout();

    void shouldRequestFocus();

    void requestFocus();
}
