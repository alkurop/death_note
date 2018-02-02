package com.omar.deathnote.notes.content.add.bll;


import com.omar.deathnote.notes.content.add.ui.IAddDialogView;

/**
 * Created by omar on 9/15/15.
 */
public interface IAddDialogPresenter {
    void setView(IAddDialogView view);

    void displayView();

    void init(AddDialogPresenter.IAddDialogCallback callback);

    void generateBaseListItems();

    void generateAudioListItems();

    void generatePicListItems();

}
