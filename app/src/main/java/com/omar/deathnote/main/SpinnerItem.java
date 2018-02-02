package com.omar.deathnote.main;

/**
 * Created by omar on 9/10/15.
 */
public class SpinnerItem {
    private int imageId;
    private String name;

    public SpinnerItem(int imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }
}
