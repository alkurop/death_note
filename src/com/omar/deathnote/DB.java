package com.omar.deathnote;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

	private static final String DB_NAME = "deathnote";
	private static final int DB_VERSION = 1;
	private static final String DB_TABLE = "maintab";
	private static final String LICENSE_TABLE = "licensetab";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_STYLE = "style";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TIMEDATE = "timedate";
	public static final String COLUMN_XML = "xml";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_CONT1 = "cont1";
	public static final String COLUMN_CONT2 = "cont2";
	public static final String COLUMN_LICENSE = "license";

	private static final String DB_CREATE = "create table " + DB_TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, " + COLUMN_STYLE
			+ " integer, " + COLUMN_TITLE + " text, " + COLUMN_TIMEDATE
			+ " text  "  +

			");";
	
 
	
	

	private final Context mCtx;

	private DBHelper mDBHelper;
	private SQLiteDatabase mDB;

	public DB(Context ctx) {
		mCtx = ctx;
	}

	Select sel = new Select();
	public void beginTransaction(){
	mDB.beginTransaction();
	}
	public void endTransaction(){
		mDB.endTransaction() ;
		}
	public void setTransactionSuccessful(){
		mDB.setTransactionSuccessful();
	}
	public void open() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getWritableDatabase();
		Log.d("db", "open");
	}

	public void close() {
			
			mDBHelper.close();
		Log.d("db", "close");
	}

	public Cursor getAllData() {
		return mDB.query(DB_TABLE, null, null, null, null, null,
				COLUMN_TIMEDATE + " DESC");

	}

	public Cursor getAllDataSort(String sort, String order) {

		return mDB.query(DB_TABLE, null, null, null, null, null, sort + order);

	}

	public Cursor getByStyle(int style) {

		Cursor mCursor = mDB.query(DB_TABLE, null, COLUMN_STYLE + "=" + style,
				null, null, null, COLUMN_TIMEDATE + " DESC");

		return mCursor;
	}

	public void addRec(int style, String title) {

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_STYLE, style);
		cv.put(COLUMN_TITLE, title);
		cv.put(COLUMN_TIMEDATE,
				new SimpleDateFormat("dd  MMMM  HH:mm:ss  ").format(new Date()));

		mDB.insert(DB_TABLE, null, cv);
		
	}

	public void delRec(long id) {
		mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
	}

	public Cursor fetchRec(long rowId) {

		Cursor mCursor = mDB.query(DB_TABLE, new String[] { COLUMN_ID,
				COLUMN_STYLE, COLUMN_TITLE, COLUMN_TIMEDATE  },
				COLUMN_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchLast() {

		Cursor mCursor = mDB.query(DB_TABLE, null, null, null, null, null,
				COLUMN_ID);

		if (mCursor != null) {
			mCursor.moveToLast();
		}
		return mCursor;
	}

 

	
	
	public boolean editRec(long rowId, int style, String title ) {

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_STYLE, style);
		cv.put(COLUMN_TITLE, title);
		/*cv.put(COLUMN_TIMEDATE,
				new SimpleDateFormat("dd  MMMM  HH:mm  ").format(new Date()));*/
		 
		return mDB.update(DB_TABLE, cv, COLUMN_ID + "=" + rowId, null) > 0;
		
		
		
	}
	 

	
	
	/*note table*/
	
	
	public void createNoteTable(Long id) {
		String TABLE_CREATE =
				"create table " + "NOTE" + String.valueOf(id) + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_TYPE
				+ " text, " + COLUMN_CONT1 + " text, " + COLUMN_CONT2
				+ " text  " +

				");";
		mDB.execSQL("DROP TABLE IF EXISTS "+ "NOTE"+ String.valueOf(id));
		mDB.execSQL(TABLE_CREATE);
		

	}
	public void deleteNoteTable(Long id){
		mDB.execSQL("DROP TABLE IF EXISTS "+ "NOTE" + String.valueOf(id));
	}
 
	public void deleteNoteTable(int id){
		mDB.execSQL("DROP TABLE IF EXISTS "+ "NOTE" + String.valueOf(id));
	}
	public void addFragment(Long id, String type, String cont1, String cont2) {

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TYPE, type);
		cv.put(COLUMN_CONT1, cont1);
		cv.put(COLUMN_CONT2,	cont2 );

		mDB.insert("NOTE" + String.valueOf(id), null, cv);
	}
	
	
	
	
	public Cursor getAllNoteData(Long id) {
		String TABLE_CREATE =
				"create table if not exists " + "NOTE" + String.valueOf(id) + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_TYPE
				+ " text, " + COLUMN_CONT1 + " text, " + COLUMN_CONT2
				+ " text  " +

				");";
	 
		mDB.execSQL(TABLE_CREATE);
		
		
		
		
		
		return mDB.query( "NOTE" + String.valueOf(id), null, null, null, null, null,
			null);

	}

	public Cursor getAllNoteData(String id) {
		return mDB.query( "NOTE" + id, null, null, null, null, null,
			null);

	}
	
	/*note table*/
	

	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			FileManager sc = new FileManager();
			Log.d("db", "creating");
			sc.startup();
			db.execSQL(DB_CREATE);
			
		 
			
	/*		firstNote();*/
		 
		  
			 
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
	
	 

}