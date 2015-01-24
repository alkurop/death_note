package com.omar.deathnote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;
import com.omar.deathnote.dialogs.DialogOnDelete;
import com.omar.deathnote.dialogs.DialogOnDelete.DeleteDialog;
import com.omar.deathnote.fragments.AudioFragment;
import com.omar.deathnote.pref.PrefActivity;

public class MainActivity extends Activity implements OnNavigationListener,
		LoaderCallbacks<Cursor>, DeleteDialog {

	SimpleAdapter selectAdapter;
	ArrayList<Map<String, Object>> dataSelect;
	Map<String, Object> m;
	Select sel;

	ListView lvData;
	LinearLayout mainLayout;
	DB db;
	MyCursorAdapter scAdapter;
	ImageButton del;
	int listItemId;

	final int addItem = 1;
	final int editItem = 2;
	final int delItem = 3;
	int recToDel = 0;
	FileManager sc;
	private static int orderStatus;

	// License checking
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// licencing
	 

		getActionBar().setDisplayShowTitleEnabled(false);
		/* requestWindowFeature(Window.FEATURE_NO_TITLE); */
		setContentView(R.layout.activity_main);

		sc = new FileManager();
		naviSelect();
		sel = new Select();

		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

		if (getResources().getConfiguration().orientation == 1) {
			mainLayout.setBackgroundDrawable(getApplicationContext()
					.getResources().getDrawable(sel.bg_images_main[0]));
		} else {
			mainLayout.setBackgroundDrawable(getApplicationContext()
					.getResources().getDrawable(sel.bg_images_main_2[0]));
		}

		LayoutInflater inf = getLayoutInflater();
		ListView channelsList = (ListView) findViewById(R.id.mainList);

		View snapshot = inf.inflate(R.layout.add, channelsList, false);
		channelsList.addHeaderView(snapshot);

		// < ---- DB --->
		Log.d("Orientation", "" + getResources().getConfiguration().orientation);
		db = new DB(this);

		db.open();

		if (savedInstanceState != null) {

			orderStatus = savedInstanceState.getInt("orderStatus");
			getActionBar().setSelectedNavigationItem(orderStatus);

			Cursor cursor = db.getByStyle(orderStatus);

			loadList(cursor);

		} else {
			 

			Cursor cursor = db.getAllData();
			loadList(cursor);
		}

		lvData.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				 
				Bundle bundle = new Bundle();

				bundle.putLong("id", id);
			 

				Intent intent;
				intent = new Intent(getApplicationContext(), Note.class);
				intent.putExtras(bundle);

				startActivityForResult(intent, editItem);

			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putInt("orderStatus", orderStatus);
		/*Log.d("orderStatus", String.valueOf((orderStatus)));*/
	}

	@Override
	protected void onDestroy() {
		Intent destroyMusic = new Intent(AudioFragment.BROADCAST_PAUSESONG);
		destroyMusic.putExtra("flag", "destroy");
		sendBroadcast(destroyMusic);
		super.onDestroy();

	}

	public void addNote(View v) {

		Bundle bundle = new Bundle();

		int style;
		if (orderStatus != 0) {
			style = orderStatus;
		} else {
			style = randInt(1, 6);
		}
		bundle.putInt("style", style);
		Intent intent;

		intent = new Intent(this, Note.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, addItem);

	}

	public void doNata(View v) {
	}

	public void naviSelect() {
		sel = new Select();

		dataSelect = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < sel.select_images.length; i++) {

			m = new HashMap<String, Object>();
			m.put(sel.ATTRIBUTE_NAME_TEXT,
					getResources().getString(sel.select_names[i]));
			m.put(sel.ATTRIBUTE_NAME_STYLE, sel.select_images[i]);
			dataSelect.add(m);
		}

		String[] fromSel = { sel.ATTRIBUTE_NAME_TEXT, sel.ATTRIBUTE_NAME_STYLE };
		int[] toSel = { R.id.itemName, R.id.itemImg };
		selectAdapter = new SimpleAdapter(this, dataSelect, R.layout.select,
				fromSel, toSel);
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		selectAdapter.setDropDownViewResource(R.layout.select);

		bar.setListNavigationCallbacks(selectAdapter, this);
		// </ --- SELECT --- >
	}

	public void loadList(Cursor cursor) {

		String[] fromMain = new String[] { DB.COLUMN_STYLE, DB.COLUMN_TITLE,
				DB.COLUMN_TIMEDATE };
		int[] toMain = new int[] { R.id.itemImg, R.id.itemTitle, R.id.itemDate };

		scAdapter = new MyCursorAdapter(this, R.layout.item, cursor, fromMain,
				toMain, 0);
		lvData = (ListView) findViewById(R.id.mainList);

		// < --- on item click listener --- >
		lvData.setAdapter(scAdapter);
		getLoaderManager().initLoader(0, null, this);

	}

	public void reloadList() {

		getLoaderManager().restartLoader(0, null, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_about:

			Intent pref = new Intent(getApplicationContext(),
					PrefActivity.class);
			startActivity(pref);

			return true;
		case R.id.add:

			Bundle bundle = new Bundle();

			int style;
			if (orderStatus != 0) {
				style = orderStatus;
			} else {
				style = randInt(1, 6);
				bundle.putInt("style", style);
				Intent intent;

				intent = new Intent(this, Note.class);
				intent.putExtras(bundle);

				startActivityForResult(intent, addItem);

			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int pos, long id) {

		orderStatus = pos;
		reloadList();
		return false;

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {

		return new MyCursorLoader(this, db);

	}

	static class MyCursorLoader extends CursorLoader {

		DB db;

		public MyCursorLoader(Context context, DB db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor;
			if (orderStatus == 0) {
				db.open();

				cursor = db.getAllData();

			} else {
				db.open();

				cursor = db.getByStyle(orderStatus);

			}

			return cursor;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		scAdapter.swapCursor(cursor);
		db.close();

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

		scAdapter.swapCursor(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {

			return;
		}

		if (resultCode == RESULT_CANCELED && requestCode == addItem) {
			db.open();
			Cursor cursor = db.fetchLast();
			long toDel = cursor.getLong(cursor.getColumnIndex(DB.COLUMN_ID));

			db.delRec(toDel);
			db.deleteNoteTable(toDel);
			db.close();

			reloadList();

		}

		if (resultCode == RESULT_OK) {

			int style = 1;

			Bundle extras = data.getExtras();

			style = extras.getInt("style");

			/* getActionBar().setSelectedNavigationItem(style); */

			reloadList();

		}
	}

	public class MyCursorAdapter extends SimpleCursorAdapter {

		public MyCursorAdapter(Context _context, int _layout, Cursor _cursor,
				String[] _from, int[] _to, int flags) {

			super(_context, _layout, _cursor, _from, _to, flags);

		}

		@Override
		public void bindView(View view, Context _context, Cursor _cursor) {
			String title = _cursor.getString(_cursor
					.getColumnIndex(DB.COLUMN_TITLE));
			String date = _cursor.getString(_cursor
					.getColumnIndex(DB.COLUMN_TIMEDATE));

			final int id = _cursor.getInt(_cursor.getColumnIndex(DB.COLUMN_ID));
			int img = sel.select_images[_cursor.getInt(_cursor
					.getColumnIndex(DB.COLUMN_STYLE))];

			ImageView del = (ImageView) view.findViewById(R.id.del);
			del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					DialogFragment delDialog = new DialogOnDelete();
					recToDel = id;
					delDialog.show(getFragmentManager(), "dialog");

				}

			});
			TextView itemTitle = (TextView) view.findViewById(R.id.itemTitle);
			TextView itemDate = (TextView) view.findViewById(R.id.itemDate);

			itemTitle.setText(title);
			itemDate.setText(date);
			ImageView itemImg = (ImageView) view.findViewById(R.id.itemImg);
			itemImg.setImageResource(img);

		}

	}

	@Override
	public void del(String dialogId, String s) {
		switch (dialogId) {
		case "delItem":
			switch (s) {
			case "Yes":
				if (recToDel != 0)
					sc.delFile("XML", recToDel);
				db.open();

				db.delRec(recToDel);
				db.deleteNoteTable(recToDel);
				db.close();

				reloadList();

			}
			break;
		}

	}

	public static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		/*Log.d("rand", String.valueOf(randomNum));*/
		return randomNum;
	}

	// licencing
	 

}
