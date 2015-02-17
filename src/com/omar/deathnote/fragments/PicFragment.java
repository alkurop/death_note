package com.omar.deathnote.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.omar.deathnote.R;
import com.omar.deathnote.DB;
import com.omar.deathnote.FileManager;
import com.omar.deathnote.picview.SingleViewActivity;
import com.omar.deathnote.utility.OnDeleteFragment;
import com.omar.deathnote.utility.SaveNote;

public class PicFragment extends Fragment implements OnDeleteFragment {

	ImageView img;
	LinearLayout picLayout;
	static String fragId;
	String noteId;
	String imName;

	BitmapWorkerTask bw;
	static OnDeleteFragment OnDeleteFragment;
	Activity SaveNote;
	View v;
	Drawable icon;
	static/* static Activity act; */
	Context thiscontext;
	String filePath;
	FileManager sc;
	int prop;
	int w, h;
	String cam;
	DB db;
	Cursor cursor;
	SaveNote sX;
	ImageView del;

	

	/* new SimpleDateFormat("dd  MMMM  HH:mm   ").format(new Date()) */
	// ////////// SAVERS
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (fragId != null)
			outState.putString("fragId", fragId);

		if (noteId != null)
			outState.putString("noteId", noteId);
		if (imName != null)
			outState.putString("imName", imName);

	/*	Log.d("fragId =  ", fragId);*/

	}

	public TreeMap<String, String> saveContent() {

		TreeMap<String, String> content = new TreeMap<String, String>();
		content.put("cont1", imName);
		content.put("cont2", "hello");

		return content;

	}

	// ///////// LOADERS
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.note_elem_pic, null);
		sc = new FileManager();
		thiscontext = container.getContext();
		picLayout = (LinearLayout) v.findViewById(R.id.picLayout);
		picLayout.setClickable(true);
		picLayout.setFocusable(true);
		picLayout.setFocusableInTouchMode(true);
		picLayout.requestFocus();

		picLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sX.saveRun();
				openViewer();
			}
		});
		del = (ImageView) v.findViewById(R.id.del);
		del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OnDeleteFragment.delete(fragId, true);

			}
		});
		if (savedInstanceState == null) {

		
			if (imName == null) {

				del.setVisibility(View.INVISIBLE);

				if (cam == "cam") {
					loadImageFromCam();
				} else {
					loadUnscaledImage();
				}

			} else {
				if (new File(imName).exists()) {
					setSavedImageToLayout(imName);

				}

				else {
					OnDeleteFragment.delete(fragId, false);
				}

			}

		} else {

			fragId = savedInstanceState.getString("fragId");
			noteId = savedInstanceState.getString("noteId");
			imName = savedInstanceState.getString("imName");
			/* filePath = savedInstanceState.getString("filePath" ); */

			setSavedImageToLayout(imName);

		}
		db = new DB(thiscontext);
		return v;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {

			sX = (SaveNote) activity;
			OnDeleteFragment = (OnDeleteFragment) activity;
		/*	thiscontext = ((Activity) OnDeleteFragment).getApplicationContext();*/
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onSomeEventListener");
		}

	}

	public void loadContent(TreeMap<String, String> temp) {
		if (temp.get("cont1") != null) {
			imName = temp.get("cont1");
		}

		if (temp.get("cont2") != null) {
			cam = temp.get("cont2");
		}

	}

	public void loadFragId(String str) {
		fragId = str;
	}

	public void loadNoteId(String str) {
		noteId = str;
	}

	public void loadUnscaledImage() {

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, 1);

	}

	private void loadImageFromCam() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		imName = sc.sdPath + "/" + "DCIM" + "/" + "Camera" + "/" + "Deathnotes"
				+ noteId + "_" + fragId + ".jpg";
		/*Log.d("image dir", imName);*/
		File photo = new File(imName);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		startActivityForResult(intent, 2);
	}

	public void setSavedImageToLayout(String imName) {

		BitmapWorkerTask task = new BitmapWorkerTask(picLayout);
		task.execute(imName);
	}

	// //////////// COMMUTICATORS

	@Override
	public void delete(String s, boolean dialog) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {

			if (requestCode == 1) {

				Uri photoUri = data.getData();
				/*Log.d("uri", photoUri.toString());*/
				if (photoUri != null) {
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Cursor cursor = thiscontext.getContentResolver().query(
							photoUri, filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					filePath = cursor.getString(columnIndex);

					cursor.close();
					/*Log.d("Load Image", "Gallery File Path=====>>>" + filePath);*/

					if (photoUri.toString().startsWith(
							"content://com.google.android.gallery3d")) {
						Log.d("picasa", photoUri.getPath());

						try {
							PicDL pDL = new PicDL();
							pDL.execute(photoUri);
							InputStream is = thiscontext.getContentResolver()
									.openInputStream(photoUri);
							imName = sc.sdPath + "/" + sc.ImageFolder + "/"

							+ "Deathnotes" + noteId + "_" + fragId + ".jpg";
							File f = new File(imName);
							OutputStream os = new FileOutputStream(f);
							pipe(is, os);
							os.close();

							is.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							OnDeleteFragment.delete(fragId, false);
						} catch (IOException e) {
							e.printStackTrace();
							OnDeleteFragment.delete(fragId, false);
						}
					 
						if (new File(imName).exists()) {
							BitmapWorkerTask task = new BitmapWorkerTask(
									picLayout);
							task.execute(imName);
						} else {
							OnDeleteFragment.delete(fragId, false);
						}
						/*
						 * 
						 * 
						 * OnDeleteFragment.delete(fragId);
						 */

					} else {

						imName = filePath;
						BitmapWorkerTask task = new BitmapWorkerTask(picLayout);
						task.execute(filePath);

					}
					Log.d("Result Code", String.valueOf(resultCode));
					del.setVisibility(View.VISIBLE);
				}

			}
			if (requestCode == 2) {
				sc = new FileManager();
				BitmapWorkerTask task = new BitmapWorkerTask(picLayout);
				task.execute(imName);

				del.setVisibility(View.VISIBLE);
				ContentValues values = new ContentValues();

				values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
				values.put(Images.Media.MIME_TYPE, "image/jpeg");
				values.put(MediaStore.MediaColumns.DATA, imName);

				thiscontext.getContentResolver().insert(
						Images.Media.EXTERNAL_CONTENT_URI, values);

			}

		} else {

			OnDeleteFragment.delete(fragId, false);
			Log.d("deleteng        ======>",fragId);

			
		}
	}

	// //////////DECODERS

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<LinearLayout> imageViewReference;
		private String data = null;

		public BitmapWorkerTask(LinearLayout linearLayout) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<LinearLayout>(linearLayout);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bm = null;
			data = params[0];
			try {
				bm = decodeSampledBitmapFromFilepath(data, 270, 270);
			} catch (NullPointerException ne) {
				ne.printStackTrace();

				OnDeleteFragment.delete(fragId, false);

			}

			return bm;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final LinearLayout linearLayout = imageViewReference.get();
				if (linearLayout != null) {
					BitmapDrawable background = new BitmapDrawable(
							thiscontext.getResources(), bitmap);

					w = bitmap.getWidth();
					h = bitmap.getHeight();

					Log.d("Width", String.valueOf(w));
					Log.d("height", String.valueOf(h));
					float c = (float) w / (float) h;
					Log.d("height", String.valueOf(c));
					Math.round(linearLayout.getLayoutParams().height /= c);
					/* linearLayout.getLayoutParams().width = 300; */
					linearLayout.requestLayout();

					linearLayout.setBackgroundDrawable(background);
					background = null;
					Log.d("image", "setting image");
					/* saveThumbNail(bitmap); */
					bitmap = null;
				}
			}
		}
	}

	public static Bitmap decodeSampledBitmapFromFilepath(String filepath,
			int reqWidth, int reqHeight) throws NullPointerException {

		
		
		
		
		Log.d("image pass", "start");
		Bitmap btm = null;
		Bitmap tmp = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();

		BitmapFactory.decodeFile(filepath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		btm = BitmapFactory.decodeFile(filepath, options);
		try {
			tmp = getRoundedCornerBitmap(btm, 20);
		} catch (RuntimeException re) {
			re.printStackTrace();
			OnDeleteFragment.delete(fragId, false);

		}
		btm = null;
		return tmp;

		// Decode bitmap with inSampleSize set

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		Log.d("height", String.valueOf(height));
		final int width = options.outWidth;
		Log.d("width", String.valueOf(width));
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			/* final int halfWidth = width / 2; */

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					|| (halfHeight / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	// ////////// VIEWER
	public ArrayList<String> loadImagesToViewer() {

		ArrayList<String> values = new ArrayList<String>();
		db.open();
		cursor = db.getAllNoteData(noteId);
		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndex(db.COLUMN_TYPE))
					.equalsIgnoreCase("PicFragment")) {

				values.add(cursor.getString(cursor
						.getColumnIndex(db.COLUMN_CONT1)));

			}

		}
		cursor.close();
		db.close();

		return values;
	}

	public int getPositionToViewer(ArrayList<String> values, String path) {
		int position = 0;

		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).equals(path)) {
				position = i;
			}
		}
		Log.d("curretn image position", String.valueOf(position));
		return position;
	}

	public void openViewer() {

		Intent i = new Intent(thiscontext, SingleViewActivity.class);
		i.putExtra("id", getPositionToViewer(loadImagesToViewer(), imName));

		i.putStringArrayListExtra("list", loadImagesToViewer());
		/* loadImagesToViewer(); */
		Log.d("current image position", String.valueOf(getPositionToViewer(
				loadImagesToViewer(), imName)));

		startActivity(i);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels)
			throws RuntimeException {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public void pipe(InputStream is, OutputStream os) throws IOException {
		int n;
		byte[] buffer = new byte[1024];
		while ((n = is.read(buffer)) > -1) {
			os.write(buffer, 0, n); // Don't allow any extra bytes to creep in,
									// final write
		}
		os.close();
	}

	
	class PicDL extends AsyncTask<Uri, Integer, String> {
	    
	    @Override
	    protected void onPreExecute() {
	      super.onPreExecute();
	    
	    }

	    @Override
	    protected String doInBackground(Uri... uri) {
	       String s = null;
	      return s;
	    }


	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      super.onProgressUpdate(values);
	       
	    }

	}
	
	
}
