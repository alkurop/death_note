package com.omar.deathnote.main.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.dialogs.DialogOnDelete;
import com.omar.deathnote.dialogs.DialogOnDelete.DeleteDialog;
import com.omar.deathnote.notes.ui.NoteActivity;
import com.omar.deathnote.pref.PrefActivity;
import com.omar.deathnote.rate.RateMeMaybe;
import com.omar.deathnote.utility.LoaderCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressLint("NewApi")
@SuppressWarnings({ "deprecation" })
public class MainActivity_old extends AppCompatActivity implements OnNavigationListener {

	private SimpleAdapter selectAdapter;
	private ArrayList<Map<String, Object>> dataSelect;
	private Map<String, Object> m;
	private ListView lvData;
	private LinearLayout mainLayout;
	private ImageView addNote;

	private static MainListCursorAdapter scAdapter;
	private static LoaderManager loaderManager;
	private static LoaderCallback callbacks;
	private static DeleteDialog deleteDialog;

	private static final int addItem = 1;
	private static final int editItem = 2;
	private static int orderStatus;
	private int recToDel = 0;

	public static DeleteDialog getDeleteDialog() {
		return deleteDialog;
	}

	public static int getOrderStatus() {
		return orderStatus;
	}

	public static void setOrderStatus(int orderStatus) {
		MainActivity_old.orderStatus = orderStatus;
	}

	private void setupMyListeners() {
		deleteDialog = new DeleteDialog() {

			@Override
			public void del() {

				deleteSomeNote(recToDel);
				orderStatus = 0;
				reloadList();

			}
		};
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayShowTitleEnabled(false);

		setContentView(R.layout.activity_main_old);

		naviSelect();
		setupMyListeners();

		loaderManager = getSupportLoaderManager();
		callbacks = LoaderCallback.getInstance(this);
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

		if (getResources().getConfiguration().orientation == 1) {
			mainLayout.setBackgroundDrawable(getApplicationContext()
					.getResources().getDrawable(Constants.bg_images_main[0]));
		} else {
			mainLayout.setBackgroundDrawable(getApplicationContext()
					.getResources().getDrawable(Constants.bg_images_main_2[0]));
		}

		LayoutInflater inf = getLayoutInflater();
		ListView mainList = (ListView) findViewById(R.id.mainList);

		View addNoteView = inf.inflate(R.layout.add, mainList, false);
		mainList.addHeaderView(addNoteView);

		addNote = (ImageView) addNoteView.findViewById(R.id.addNote);
		addNote.setOnClickListener(new OnClickListener(

		) {

			@Override
			public void onClick(View arg0) {
				addNote();

			}
		});

		Log.d("Orientation", "" + getResources().getConfiguration().orientation);

		setUpRatingModule();

		if (savedInstanceState != null) {

			orderStatus = savedInstanceState.getInt(Constants.ORDER_STATUS);
			recToDel = savedInstanceState.getInt(Constants.RECORD_TO_DELETE);
			getActionBar().setSelectedNavigationItem(orderStatus);

		}  

		setupMainList();

	
		reloadList();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putInt(Constants.ORDER_STATUS, orderStatus);

		outState.putInt(Constants.RECORD_TO_DELETE, recToDel);

	}

	@Override
	protected void onDestroy() {
		Intent destroyMusic = new Intent(Constants.BROADCAST_PAUSESONG);
		destroyMusic.putExtra(Constants.FLAG, Constants.DESTROY);
		sendBroadcast(destroyMusic);
		super.onDestroy();

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

			addNote();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int pos, long id) {

		orderStatus = pos;
		reloadList();
		return false;

	}

	public static void swapCursor(Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent bundle) {
		orderStatus = 0;
		getActionBar().setSelectedNavigationItem(orderStatus);
		reloadList();

	}

	private void addNote() {
		Bundle bundle = new Bundle();
		int style;
		if (orderStatus != 0) {
			style = orderStatus;
		} else {
			style = randInt(1, 6);
		}
		bundle.putInt(Constants.STYLE, style);
		Intent intent;

		intent = new Intent(this, NoteActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, addItem);

	}

	public void naviSelect() {

		dataSelect = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < Constants.select_images.length; i++) {

			m = new HashMap<String, Object>();
			m.put(Constants.ATTRIBUTE_NAME_TEXT,
					getResources().getString(Constants.select_names[i]));
			m.put(Constants.ATTRIBUTE_NAME_STYLE, Constants.select_images[i]);
			dataSelect.add(m);
		}

		String[] fromSel = { Constants.ATTRIBUTE_NAME_TEXT,
				Constants.ATTRIBUTE_NAME_STYLE };
		int[] toSel = { R.id.itemName, R.id.itemImg };
		selectAdapter = new SimpleAdapter(this, dataSelect, R.layout.select,
				fromSel, toSel);
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		selectAdapter.setDropDownViewResource(R.layout.select);

		bar.setListNavigationCallbacks(selectAdapter, this);

	}

	private void setupMainList() {
		String[] fromMain = new String[] { DB.COLUMN_STYLE, DB.COLUMN_TITLE,
				DB.COLUMN_TIMEDATE };
		int[] toMain = new int[] { R.id.itemImg, R.id.itemTitle, R.id.itemDate };

		scAdapter = new MainListCursorAdapter(this, R.layout.main_list_item, null,
				fromMain, toMain, 0);
		lvData = (ListView) findViewById(R.id.mainList);

		lvData.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Bundle bundle = new Bundle();

				bundle.putLong(Constants.ID, id);

				Intent intent;
				intent = new Intent(getApplicationContext(), NoteActivity.class);
				intent.putExtras(bundle);

				startActivityForResult(intent, editItem);

			}
		});
		
		lvData.setAdapter(scAdapter);
	}

	public static void reloadList() {

		loaderManager.restartLoader(LoaderCallback.LOAD_LIST, null, callbacks);

	}

	private void deleteSomeNote(int some) {

		Bundle bundle = new Bundle();
		Log.d("Some note", String.valueOf(some));
		bundle.putInt(Constants.SOME, some);
		loaderManager.restartLoader(LoaderCallback.DELETE_SOME_NOTE, bundle,
				callbacks);

	}

	public static int randInt(int min, int max) {

		Random rand = new Random();

		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	private void setUpRatingModule() {
		RateMeMaybe rmm = new RateMeMaybe(this);
		rmm.setPromptMinimums(10, 10, 10, 10);
		rmm.run();
	}

	private class MainListCursorAdapter extends SimpleCursorAdapter {

		public MainListCursorAdapter(Context _context, int _layout,
				Cursor _cursor, String[] _from, int[] _to, int flags) {

			super(_context, _layout, _cursor, _from, _to, flags);

		}

		@Override
		public void bindView(View view, Context _context, Cursor _cursor) {

			String title = _cursor.getString(_cursor
					.getColumnIndex(DB.COLUMN_TITLE));
			String date = _cursor.getString(_cursor
					.getColumnIndex(DB.COLUMN_TIMEDATE));
			final int id = _cursor.getInt(_cursor.getColumnIndex(DB.COLUMN_ID));
			int img = Constants.select_images[_cursor.getInt(_cursor
					.getColumnIndex(DB.COLUMN_STYLE))];

			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				holder = new ViewHolder();
				holder.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
				holder.itemDate = (TextView) view.findViewById(R.id.itemDate);
				holder.itemImg = (ImageView) view.findViewById(R.id.itemImg);
				holder.del = (ImageView) view.findViewById(R.id.del);
				view.setTag(holder);
			}

			holder.itemTitle.setText(title);
			holder.itemDate.setText(date);
			holder.itemImg.setImageResource(img);
			holder.del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					DialogFragment delDialog = new DialogOnDelete();
					recToDel = id;
					delDialog.show(getFragmentManager(), Constants.DIALOG);

				}

			});

		}

		private class ViewHolder {
			TextView itemTitle;
			TextView itemDate;
			ImageView itemImg;
			ImageView del;
		}

	}

}
