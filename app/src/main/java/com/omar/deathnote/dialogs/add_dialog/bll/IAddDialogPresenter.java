package com.omar.deathnote.dialogs.add_dialog.bll;

import com.omar.deathnote.dialogs.add_dialog.ui.IAddDialogView;
import com.omar.deathnote.models.Content;

/**
 * Created by omar on 9/15/15.
 */
public interface IAddDialogPresenter {
    void setView(IAddDialogView view);

    void displayView();

    void listItemClicked(int pos);

    void init(AddDialogPresenter.IAddDialogCallback callback);

    void generateBaseListItems();

    void generateAudioListItems();

    void generatePicListItems();


}
