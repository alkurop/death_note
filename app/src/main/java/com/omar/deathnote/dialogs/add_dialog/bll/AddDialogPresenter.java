package com.omar.deathnote.dialogs.add_dialog.bll;

import com.omar.deathnote.dialogs.add_dialog.item.IAddDialogItemDataHolder;
import com.omar.deathnote.dialogs.add_dialog.ui.IAddDialogView;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.models.ContentFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 9/15/15.
 */
public class AddDialogPresenter implements IAddDialogPresenter {
    private List<IAddDialogItemDataHolder> items;
    private IAddDialogCallback callback;
    private  IAddDialogView view;
    private ContentFactory contentFactory;
    private String dialogTitle;

    @Override
    public void setView(IAddDialogView view) {
        this.view = view;
    }

    @Override
    public void displayView() {

    }

    @Override
    public void listItemClicked(int pos) {
        items.get(pos).doAction();
    }

    @Override
    public void init(IAddDialogCallback callback) {
        this.callback = callback;
        contentFactory = new ContentFactory();
    }






    @Override
    public void generateBaseListItems() {
        items = new ArrayList<>();
    }

    @Override
    public void generateAudioListItems() {
        items = new ArrayList<>();
    }

    @Override
    public void generatePicListItems() {
        items = new ArrayList<>();
    }


    public interface IAddDialogCallback{
        void addContent (Content content);
    }
}
