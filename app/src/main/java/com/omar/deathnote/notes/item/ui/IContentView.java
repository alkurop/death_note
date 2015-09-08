package com.omar.deathnote.notes.item.ui;

import com.omar.deathnote.notes.item.bll.base.IContentEventHandler;

/**
 * Created by omar on 9/7/15.
 */
public interface IContentView {
    void SetEventHandler(IContentEventHandler eventHandler);
    void SetContent1(String content1);
    void SetContent2(String content2);

    String GetContent1();
    String GetContent2();

    void OnDeleteClicked();

    int GetLayout();
}
