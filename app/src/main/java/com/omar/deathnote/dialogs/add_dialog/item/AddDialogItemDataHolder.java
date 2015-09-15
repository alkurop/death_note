package com.omar.deathnote.dialogs.add_dialog.item;

/**
 * Created by omar on 9/15/15.
 */
abstract public class AddDialogItemDataHolder implements IAddDialogItemDataHolder{

    private String name;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
