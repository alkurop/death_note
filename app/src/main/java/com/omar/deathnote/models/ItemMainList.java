package com.omar.deathnote.models;

import android.database.Cursor;
import com.omar.deathnote.Constants;
import com.omar.deathnote.db.DB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 8/29/15.
 */
public class ItemMainList {

    public String title, timedate;
    public int id;
    public int img;


    public static ItemMainList Create(Cursor _cursor) {
        ItemMainList item = new ItemMainList();

        {
            item.title = _cursor.getString(_cursor.getColumnIndex(DB.COLUMN_TITLE));
            item.timedate = _cursor.getString(_cursor.getColumnIndex(DB.COLUMN_TIMEDATE));
            item.id = _cursor.getInt(_cursor.getColumnIndex(DB.COLUMN_ID));
            item.img = Constants.select_images[_cursor.getInt(_cursor.getColumnIndex(DB.COLUMN_STYLE))];

        }
        return item;

    }

    public static List<ItemMainList> CreateList(Cursor cursor) {
        List<ItemMainList> data = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                data.add(Create(cursor));
            }
            while(cursor.moveToNext());

        }
        return data;
    }

}