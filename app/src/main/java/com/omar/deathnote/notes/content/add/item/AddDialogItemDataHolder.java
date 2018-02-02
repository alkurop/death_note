package com.omar.deathnote.notes.content.add.item;

abstract public class AddDialogItemDataHolder implements IAddDialogItemDataHolder {
    private final String name;


    protected AddDialogItemDataHolder(String titles) {
        this.name = titles;
    }


    @Override
    public String getTitle() {
        return name;
    }
}
