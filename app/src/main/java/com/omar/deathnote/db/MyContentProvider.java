package com.omar.deathnote.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.omar.deathnote.Constants;
import com.omar.deathnote.models.NoteModel;
import com.omar.deathnote.notes.ContentType;
import com.omar.deathnote.utility.JsonHelper;

/**
 * Created by omar on 9/9/15.
 */
public class MyContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.omar.deathnote.contentprovider";
    public static final String MAIN_TABLE_PATH = "notes";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir" + AUTHORITY + "." + MAIN_TABLE_PATH;

    public static final Uri PROVIDER_URI = Uri.parse("content://" + AUTHORITY + "/" + MAIN_TABLE_PATH);

    public enum URI_MATCHER {
        GET_NOTES_STYLE,
        GET_NOTE,
        INSERT_NOTE,
        DELETE_NOTE
    }


    private DB db;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MAIN_TABLE_PATH + "/" + URI_MATCHER.GET_NOTES_STYLE.ordinal() +"/#", URI_MATCHER
                .GET_NOTES_STYLE.ordinal());
        uriMatcher.addURI(AUTHORITY, MAIN_TABLE_PATH + "/" + URI_MATCHER.INSERT_NOTE.ordinal() + "/#",
                URI_MATCHER.INSERT_NOTE.ordinal());
        uriMatcher.addURI(AUTHORITY, MAIN_TABLE_PATH + "/" + URI_MATCHER.GET_NOTE.ordinal() + "/#",
                URI_MATCHER.GET_NOTE.ordinal());
        uriMatcher.addURI(AUTHORITY, MAIN_TABLE_PATH + "/" + URI_MATCHER.DELETE_NOTE.ordinal() + "/#",
                URI_MATCHER.DELETE_NOTE.ordinal());
    }


    @Override
    public boolean onCreate() {
        db = DB.getInstance(getContext());
        return db.openForProvider();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (URI_MATCHER.values()[uriMatcher.match(uri)]) {
            case GET_NOTES_STYLE:
                String s = uri.getLastPathSegment();
                int style = Integer.parseInt(s);
                cursor = style == 0 ? db.getAllData() : db.getByStyle(style);
                break;
            case GET_NOTE:
                int id = Integer.parseInt(uri.getLastPathSegment());
                cursor = db.getAllNoteData(id);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.values()[uriMatcher.match(uri)]) {
            default:
                return CONTENT_TYPE;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri resultUri ;
        switch (URI_MATCHER.values()[uriMatcher.match(uri)]) {
            case INSERT_NOTE:
                NoteModel noteModel = JsonHelper.getObject(values.getAsString(Constants.DATA));
                db.beginTransaction();
                int id = noteModel.getId();
                if (id == -1) id = addNote(noteModel);
                else editNoteRecord(noteModel);
                saveNote(id, noteModel);
                db.setTransactionSuccessful();
                db.endTransaction();
                resultUri = ContentUris.withAppendedId(PROVIDER_URI, 0);
                getContext().getContentResolver().notifyChange(PROVIDER_URI, null);
                return resultUri;

        }
        return null;
    }

    private int addNote(NoteModel noteModel) {
        Cursor cursor;
       // db.addRec(noteModel.getStyle(), noteModel.getContentList().get(0).getContent1());
        cursor = db.fetchLast();
        int id = cursor.getInt(cursor.getColumnIndex(DB.COLUMN_ID));
        cursor.close();
        return id;
    }


    private void editNoteRecord(NoteModel noteModel) {
       // db.editRec(noteModel.getId(), noteModel.getStyle(), noteModel.getContentList().get(0).getContent1());
    }

    private void saveNote(int id, NoteModel noteModel) {

        db.createNoteTable(id);
        for (ContentType item : noteModel.getContentList()) {
          //  db.addContentItem(id, item.getType().ordinal(),noteModel.getStyle(), item.getContent1(), item.getContent2
           //         ());
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.values()[uriMatcher.match(uri)]) {

            case DELETE_NOTE:
                int id = Integer.parseInt(uri.getLastPathSegment());
                db.beginTransaction();
                db.delRec(id);
                db.deleteNoteTable(id);
                db.setTransactionSuccessful();
                db.endTransaction();
                getContext().getContentResolver().notifyChange(PROVIDER_URI, null);
                break;
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
