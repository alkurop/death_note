package com.omar.deathnote.dialogs.add_dialog.ui;

import android.widget.ListView;
import com.omar.deathnote.dialogs.add_dialog.bll.IAddDialogPresenter;

/**
 * Created by omar on 9/15/15.
 */
public interface IAddDialogView {

    void setTitleLabel(String label);

    ListView getListView();

    void setEventPresenter(IAddDialogPresenter presenter);
}
