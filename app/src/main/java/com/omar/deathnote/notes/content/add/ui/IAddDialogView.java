package com.omar.deathnote.notes.content.add.ui;

import com.omar.deathnote.notes.content.add.bll.IAddDialogPresenter;

public interface IAddDialogView {

    void setTitleLabel(String label);

    <T> T getRecyclerView();

    void setEventHandler(IAddDialogPresenter presenter);

    void dismiss();
}
