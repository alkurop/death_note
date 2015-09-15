package com.omar.deathnote.models;

/**
 * Created by omar on 9/15/15.
 */
public class ContentFactory implements IContentFactory{

    public static ContentFactory I(){
        return new ContentFactory();
    }

     private Content addPicEmptyContent() {
        return new Content(Content.ContentType.PICTURE);
    }

    private Content addPicGalleryContent() {
        return null;
    }

    private Content addAudioEmptyContent() {
        return new Content(Content.ContentType.AUDIO);
    }

    private Content addAudioMediaStoreCotent() {
        return null;
    }

    private Content addNoteContent() {
        return new Content(Content.ContentType.NOTE);
    }

    private Content addLinkContent() {
        return  new Content(Content.ContentType.LINK);
    }

    @Override
    public Content addContent(Content.ContentType type, boolean fromMedia) {
        switch (type){
            case AUDIO:
                return fromMedia ? addAudioMediaStoreCotent() : addAudioEmptyContent();
            case LINK:
                return addLinkContent();
            case NOTE:
                return addNoteContent();
            case PICTURE:
                return fromMedia ? addPicGalleryContent() : addPicEmptyContent();

            default: throw new IllegalArgumentException("No such content type");
        }
    }
}
