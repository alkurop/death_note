package com.omar.deathnote.models;

import com.omar.deathnote.models.Content;

/**
 * Created by omar on 9/15/15.
 */
public interface IContentFactory {


    Content addContent(Content.ContentType type, boolean fromMedia);
}
