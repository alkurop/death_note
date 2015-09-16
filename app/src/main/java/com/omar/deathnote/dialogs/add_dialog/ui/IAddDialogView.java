package com.omar.deathnote.dialogs.add_dialog.ui;

import com.omar.deathnote.dialogs.add_dialog.bll.IAddDialogPresenter;

/**
 * Created by omar on 9/15/15.
 */
public interface IAddDialogView {

    void setTitleLabel(String label);

    <T> T getRecyclerView();

    void setEventHandler(IAddDialogPresenter presenter);

    void dismiss();
}
