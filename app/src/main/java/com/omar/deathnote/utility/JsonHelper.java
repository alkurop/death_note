package com.omar.deathnote.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omar.deathnote.models.NoteModel;

/**
 * Created by njakawaii on 07.05.2015.
 */
public abstract class JsonHelper {


    public static <T>  String makeJson(T file) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(file);
    }

    public static NoteModel getObject(String file) {
        Gson gson = new GsonBuilder().create();

        return   gson.fromJson(file, NoteModel.class);
    }

}
