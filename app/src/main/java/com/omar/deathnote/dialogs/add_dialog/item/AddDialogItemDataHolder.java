package com.omar.deathnote.dialogs.add_dialog.item;

/**
 * Created by omar on 9/16/15.
 */
abstract public class AddDialogItemDataHolder implements IAddDialogItemDataHolder{
    String name;


    public AddDialogItemDataHolder(String titles){
        this.name = titles;
    }



    @Override
    public String getTitle() {
        return name;
    }
}
