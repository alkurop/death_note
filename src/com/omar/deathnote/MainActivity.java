package com.omar.deathnote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.omar.deathnote.dialogs.DialogOnDelete;
import com.omar.deathnote.dialogs.DialogOnDelete.DeleteDialog;
import com.omar.deathnote.fragments.AudioFragment;
import com.omar.deathnote.pref.PrefActivity;
import com.omar.deathnote.rate.RateMeMaybe;
import com.omar.deathnote.utility.MyLoaderManager;

@SuppressWarnings({ "deprecation" })
public class MainActivity extends FragmentActivity implements
		OnNavigationListener  {

	private static final int addItem = 1;
	private static final int editItem = 2;
	private static int orderStatus;

	private int recToDel = 0;

	private static MainListCursorAdapter scAdapter;
	private static LoaderManager loaderManager;
	private static MyLoaderManager callbacks;
	private static DeleteDialog deleteDialog;

	

	private SimpleAdapter selectAdapter;
	private ArrayList<Map<String, Object>> dataSelect;
	private Map<String, Object> m;
	private ListView lvData;
	private LinearLayout mainLayout;
	private ImageView addNote;

	public static DeleteDialog getDeleteDialog() {
		return deleteDialog;
	}
	
	public static int getOrderStatus() {
		return orderStatus;
	}

	public static void setOrderStatus(int orderStatus) {
		MainActivity.orderStatus = orderStatus;
	}

	@Override
	protected void onResume() {
		reloadList();
		super.onResume();
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

		setContentView(R.layout.activity_main);

		naviSelect();
		setupMyListeners();

		loaderManager = getLoaderManager();
		callbacks = MyLoaderManager.getInstance(this);
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

		if (getResources().getConfiguration().orientation == 1) {
			mainLayout.setBackgroundDrawable(getApplicationContext()
					.getResources().getDrawable(Select.bg_images_main[0]));
		} else {
			mainLayout.setBackgroundDrawable(getApplicationContext()
					.getResources().getDrawable(Select.bg_images_main_2[0]));
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

			orderStatus = savedInstanceState.getInt("orderStatus");
			recToDel = savedInstanceState.getInt("recToDel");
			getActionBar().setSelectedNavigationItem(orderStatus);

		}
		setupMainList();

		reloadList();

		lvData.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Bundle bundle = new Bundle();

				bundle.putLong("id", id);

				Intent intent;
				intent = new Intent(getApplicationContext(), NoteActivity.class);
				intent.putExtras(bundle);

				startActivityForResult(intent, editItem);

			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putInt("orderStatus", orderStatus);
		outState.putInt("recToDel", recToDel);

	}

	@Override
	protected void onDestroy() {
		Intent destroyMusic = new Intent(AudioFragment.BROADCAST_PAUSESONG);
		destroyMusic.putExtra("flag", "destroy");
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
		bundle.putInt("style", style);
		Intent intent;

		intent = new Intent(this, NoteActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, addItem);

	}

	public void naviSelect() {

		dataSelect = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < Select.select_images.length; i++) {

			m = new HashMap<String, Object>();
			m.put(Select.ATTRIBUTE_NAME_TEXT,
					getResources().getString(Select.select_names[i]));
			m.put(Select.ATTRIBUTE_NAME_STYLE, Select.select_images[i]);
			dataSelect.add(m);
		}

		String[] fromSel = { Select.ATTRIBUTE_NAME_TEXT,
				Select.ATTRIBUTE_NAME_STYLE };
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

		scAdapter = new MainListCursorAdapter(this, R.layout.item, null,
				fromMain, toMain, 0);
		lvData = (ListView) findViewById(R.id.mainList);

		lvData.setAdapter(scAdapter);
	}

	public static void reloadList() {

		loaderManager.restartLoader(MyLoaderManager.LOAD_LIST, null, callbacks);

	}

	private void deleteSomeNote(int some) {

		Bundle bundle = new Bundle();
		Log.d("Some note", String.valueOf(some));
		bundle.putInt("some", some);
		loaderManager.restartLoader(MyLoaderManager.DELETE_SOME_NOTE, bundle,
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
			int img = Select.select_images[_cursor.getInt(_cursor
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
					delDialog.show(getFragmentManager(), "dialog");

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
