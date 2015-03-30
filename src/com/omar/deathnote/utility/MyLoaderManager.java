package com.omar.deathnote.utility;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.omar.deathnote.DB;
import com.omar.deathnote.MainActivity;
import com.omar.deathnote.NoteActivity;
import com.omar.deathnote.Namespace;

public class MyLoaderManager implements LoaderManager.LoaderCallbacks<Cursor> {
	private static MyLoaderManager myLoaderManager;

	public static final int SAVE_NOTE = 0;
	public static final int LOAD_NOTE = 1;
	public static final int EDIT_REC_TITLE = 2;
	public static final int LOAD_STYLE = 3;
	public static final int ADD_NEW_NOTE = 4;
	public static final int LOAD_LIST = 5;
	public static final int DELETE_SOME_NOTE = 6;

	private Context context;

	public static MyLoaderManager getInstance(Context context) {

		if (myLoaderManager == null) {
			myLoaderManager = new MyLoaderManager(context);
		}

		return myLoaderManager;
	}

	private MyLoaderManager(Context context) {
		this.context = context;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		DB db = new DB(context);
		db.open();

		switch (id) {

		case LOAD_NOTE:

			return new NoteLoader(context, db);

		case EDIT_REC_TITLE:
			return new EditNoteTitleLoader(context, db, bundle);

		case LOAD_STYLE:
			return new StyleLoader(context, db);

		case ADD_NEW_NOTE:
			return new NewNoteLoader(context, db);

		case SAVE_NOTE:
			return new SaveNoteLoader(context, db, bundle);

		case LOAD_LIST:

			return new MainListCursorLoader(context, db);

		case DELETE_SOME_NOTE:

			int some = bundle.getInt(Namespace.SOME);

			return new DeleteSomeNoteCursorLoader(context, db, some);

		default:
			return null;

		}

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int loaderId = loader.getId();
		switch (loaderId) {
		case ADD_NEW_NOTE:
			NoteActivity.setId(cursor.getInt(cursor
					.getColumnIndex(DB.COLUMN_ID)));

			break;

		case LOAD_NOTE:
			if (cursor != null) {

				while (cursor.moveToNext()) {
					String type = cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_TYPE));
					String cont1 = cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_CONT1));
					String cont2 = cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_CONT2));

					Namespace.Frags eType = NoteActivity.setFragType(type);

					NoteActivity.createFragment(cont1, cont2, eType);

				}
			} else {
				/* Log.d ("loading saved note  ", "cursor == null"); */
				NoteActivity.createFragment("", null,
						Namespace.Frags.DefaultFragment);
				NoteActivity.createFragment(" ", null,
						Namespace.Frags.NoteFragment);
			}

			break;

		case LOAD_STYLE:
			NoteActivity.setStyle(cursor.getInt(cursor
					.getColumnIndex(DB.COLUMN_STYLE)));

		case LOAD_LIST:
			MainActivity.swapCursor(cursor);
			break;

		case DELETE_SOME_NOTE:
			Log.d("Some note", "finish delete some note");
			MainActivity.setOrderStatus(0);

			MainActivity.reloadList();
			break;

		default:
			break;

		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int loaderId = loader.getId();
		switch (loaderId) {
		case LOAD_LIST:
			MainActivity.swapCursor(null);
			break;

		case DELETE_SOME_NOTE:

			MainActivity.swapCursor(null);
		}

	}

	private static class NoteLoader extends CursorLoader {
		DB db;

		public NoteLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;
			cursor = db.getAllNoteData(NoteActivity.getId());
			return cursor;
		}

	}

	private static class StyleLoader extends CursorLoader {
		DB db;

		public StyleLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;
			cursor = db.fetchRec(NoteActivity.getId());
			return cursor;
		}

	}

	private static class NewNoteLoader extends CursorLoader {
		DB db;

		public NewNoteLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;
			db.addRec(NoteActivity.getStyle(), "");
			cursor = db.fetchLast();
			return cursor;
		}

	}

	private static class EditNoteTitleLoader extends CursorLoader {
		DB db;
		Bundle bundle;

		public EditNoteTitleLoader(Context context, DB db, Bundle bundle) {
			super(context);
			this.db = db;
			this.bundle = bundle;
		}

		@Override
		public Cursor loadInBackground() {

			db.editRec(NoteActivity.getId(), NoteActivity.getStyle(),
					bundle.getString(Namespace.Flags.Cont1.name()));

			return null;
		}

	}

	private static class SaveNoteLoader extends CursorLoader {
		DB db;
		ArrayList<FragContent> fragsArrayList;

		public SaveNoteLoader(Context context, DB db, Bundle bundle) {
			super(context);
			this.db = db;
			this.fragsArrayList = bundle
					.getParcelableArrayList(Namespace.FRAGMENT_ARRAY_LIST);

		}

		@Override
		public Cursor loadInBackground() {

			db.deleteNoteTable(NoteActivity.getId());
			db.createNoteTable(NoteActivity.getId());
			for (FragContent listItem : fragsArrayList) {

				db.addFragment(NoteActivity.getId(), listItem.getType(),
						listItem.getCont1(), listItem.getCont2());

			}

			return null;
		}
	}

	private static class MainListCursorLoader extends CursorLoader {

		DB db;

		public MainListCursorLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;
			if (MainActivity.getOrderStatus() == 0) {

				cursor = db.getAllData();

			} else {

				cursor = db.getByStyle(MainActivity.getOrderStatus());

			}

			return cursor;
		}
	}

	private static class DeleteSomeNoteCursorLoader extends CursorLoader {

		private DB db;
		private int id;

		public DeleteSomeNoteCursorLoader(Context context, DB db, int id) {
			super(context);
			this.db = db;
			this.id = id;

		}

		@Override
		public Cursor loadInBackground() {
			db.delRec(id);
			db.deleteNoteTable(id);

			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}

}
