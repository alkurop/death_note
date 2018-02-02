package com.omar.deathnote.lagacy_db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.alkurop.database.Content1;
import com.alkurop.database.ContentDao;
import com.alkurop.database.Note;
import com.alkurop.database.NoteDao;
import com.omar.deathnote.ComponentContainer;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("SimpleDateFormat")
public class DB {

    private static final String DB_TABLE = "maintab";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_STYLE = "style";
    private static final String COLUMN_TIMEDATE = "timedate";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_CONT1 = "cont1";
    private static final String COLUMN_CONT2 = "cont2";
    private static final String NOTE = "NOTE";


    public static class DBHelper extends SQLiteOpenHelper {
        @Inject
        ContentDao mContentDao;
        @Inject
        NoteDao mNoteDao;

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
            ComponentContainer.getInstance().get(LegacyDbComponent.class).inject(this);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase mDB, int oldVersion, int newVersion) {
            if (oldVersion < 2) {
                Cursor cursor = mDB.query(DB_TABLE, null, null, null, null, null,
                                          null);
                if (cursor != null) {

                    boolean haseItem = false;
                    haseItem = cursor.moveToFirst();
                    while (haseItem) {
                        long noteId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                        int style = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STYLE));
                        String timedate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMEDATE));
                        final Note note = new Note();
                        note.setId(noteId);
                        note.setStyle(style);
                        note.setTimedate(timedate);

                        Completable.fromAction(new Action() {
                            @Override
                            public void run() throws Exception {
                                mNoteDao.addOrUpdate(note);
                            }
                        }).subscribeOn(Schedulers.io())
                                .subscribe();

                        Cursor noteCursor = mDB.query(NOTE + noteId, null, null, null, null, null,
                                                   null);
                        boolean hasNext1 = noteCursor.moveToFirst();
                        while (hasNext1) {
                            long contentId = noteCursor.getLong(noteCursor.getColumnIndexOrThrow(COLUMN_ID));
                            int contentType = noteCursor.getInt(noteCursor.getColumnIndexOrThrow(COLUMN_TYPE));
                            String stringContent = noteCursor.getString(noteCursor.getColumnIndexOrThrow(COLUMN_CONT1));
                            String stringContentAdditional = noteCursor.getString(noteCursor.getColumnIndexOrThrow(COLUMN_CONT2));
                            final Content1 contentNote = new Content1();
                            contentNote.setParentNoteId(noteId);
                            contentNote.setId(contentId);
                            contentNote.setType(contentType);
                            contentNote.setContent(stringContent);
                            contentNote.setAdditionalContent(stringContentAdditional);
                            Completable.fromAction(new Action() {
                                @Override
                                public void run() throws Exception {
                                    mContentDao.addOrUpdate(contentNote);
                                }
                            }).subscribeOn(Schedulers.io())
                                    .subscribe();
                            mDB.execSQL("DROP TABLE IF EXISTS " + NOTE + String.valueOf(noteId));
                            hasNext1 = noteCursor.moveToNext();
                        }
                        mDB.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
                        noteCursor.close();
                        haseItem = cursor.moveToNext();
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
}
