package com.omar.deathnote.notes.legacy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
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

import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.omar.deathnote.db.DB;
import com.omar.deathnote.notes.legacy.ui.NoteActivity_old;
import com.omar.deathnote.picview.SingleViewActivity;
import com.omar.deathnote.utility.FileManager;
import com.omar.deathnote.utility.OnDeleteFragment;
import com.omar.deathnote.utility.SaveNote;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.TreeMap;

@SuppressLint("InflateParams")
public class PicFragment extends Fragment {

	private LinearLayout picLayout;
	private     String fragId;
	private   String noteId;
	private   String imName;

	private static OnDeleteFragment OnDeleteFragment;

	private View v;

	private   Context thiscontext;

	private FileManager sc;

	private int w, h;
	private String cam;

	private SaveNote sX;
	private ImageView del;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (fragId != null)
			outState.putString(Constants.FRAGMENT_ID, fragId);

		if (noteId != null)
			outState.putString(Constants.NOTE_ID, noteId);
		if (imName != null)
			outState.putString(Constants.IMAGE_NAME, imName);

	}

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
				Log.d("imname", "imname == null");
				del.setVisibility(View.INVISIBLE);

				if (cam == Constants.CAMERA) {
					loadImageFromCam();
				} else {
					loadUnscaledImage();
				}

			} else {

				Log.d("loading saved", imName);
				if (new File(imName).exists()) {
					setSavedImageToLayout(imName);

				}

				else {
					OnDeleteFragment.delete(fragId, false);
				}

			}

		} else {

			fragId = savedInstanceState.getString(Constants.FRAGMENT_ID);
			noteId = savedInstanceState.getString(Constants.NOTE_ID);
			imName = savedInstanceState.getString(Constants.IMAGE_NAME);

			setSavedImageToLayout(imName);

		}

		return v;

	}

	@Override
	public void onAttach(Activity activity) {

		if (NoteActivity_old.class.isInstance(activity)) {
			sX = NoteActivity_old.getSaveNote();

			OnDeleteFragment = NoteActivity_old.getOnDeleteFragment();
		} else {
			throw new IllegalArgumentException(
					"Activity must implement OnDeleteFragment interface ");
		}

		thiscontext = activity.getApplicationContext();
		super.onAttach(activity);

	}

	public void loadContent(TreeMap<String, String> temp) {
		if (temp.get(Constants.Flags.Cont1.name()) != null) {
			imName = temp.get(Constants.Flags.Cont1.name());
		}

		if (temp.get(Constants.Flags.Cont2.name()) != null) {
			cam = temp.get(Constants.Flags.Cont2.name());
		}

	}

	public void loadFragId(String str) {
		fragId = str;
		Log.d("fragid", str);
	}

	public void loadNoteId(String str) {
		noteId = str;
		Log.d("noteid", str);
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

		File photo = new File(imName);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		startActivityForResult(intent, 2);
	}

	public void setSavedImageToLayout(String imName) {

		BitmapWorkerTask task = new BitmapWorkerTask(picLayout);
		task.execute(imName);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {

			if (requestCode == 1) {

				Uri photoUri = data.getData();

				if (photoUri != null) {

					if (photoUri.toString().startsWith(
							"content://com.google.android.gallery3d")) {

						LoadPicasaImage loadPicasaImage = new LoadPicasaImage();
						loadPicasaImage.execute(photoUri);

					} else {

						LoadFilePathFromUri loadFilePathFromUri = new LoadFilePathFromUri();
						loadFilePathFromUri.execute(photoUri);

					}
					Log.d("Result Code", String.valueOf(resultCode));
					 
					del.setVisibility(View.VISIBLE);
				}

			}
			if (requestCode == 2) {

				BitmapWorkerTask task = new BitmapWorkerTask(picLayout);
				task.execute(imName);

				del.setVisibility(View.VISIBLE);
				ContentValues values = new ContentValues();

				values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
				values.put(Images.Media.MIME_TYPE, "image/jpeg");
				values.put(MediaStore.MediaColumns.DATA, imName);

				InsertImageToMediaStore imageToMediaStore = new InsertImageToMediaStore();
				imageToMediaStore.execute(values);
				sX.saveRun();

			}

		} else {

			OnDeleteFragment.delete(fragId, false);
			Log.d("deleteng        ======>", fragId);

		}

	}

	public void openViewer() {
		GetPositionToViewer getPositionToViewer = new GetPositionToViewer(imName);
		getPositionToViewer.execute();

	}

	public TreeMap<String, String> saveContent() {

		TreeMap<String, String> content = new TreeMap<String, String>();
		content.put(Constants.Flags.Cont1.name(), imName);

		return content;

	}

	private class LoadPicasaImage extends AsyncTask<Uri, Void, Void> {
		@Override
		protected void onPreExecute() {

			imName = sc.sdPath + "/" + sc.ImageFolder + "/"

			+ "Deathnotes" + noteId + "_" + fragId + ".jpg";

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Uri... params) {
			try {
				pipe(params[0], imName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (new File(imName).exists()) {
				BitmapWorkerTask task = new BitmapWorkerTask(picLayout);
				task.execute(imName);
			} else {
				OnDeleteFragment.delete(fragId, false);
			}
			super.onPostExecute(result);
		}

		private void pipe(Uri uri, String fileName) throws IOException {
			// Use only in a separate thread

			InputStream is = thiscontext.getContentResolver().openInputStream(
					uri);

			File f = new File(fileName);
			OutputStream os = new FileOutputStream(f);

			int n;
			byte[] buffer = new byte[1024];
			while ((n = is.read(buffer)) > -1) {
				os.write(buffer, 0, n);

			}
			os.close();

			is.close();
		}
	}

	private class LoadFilePathFromUri extends AsyncTask<Uri, Void, String> {

		@Override
		protected String doInBackground(Uri... params) {

			String path = getFilePathFromUri(params[0]);
			return path;
		}

		@Override
		protected void onPostExecute(String result) {

			imName = result;
			Log.d("imname", imName);
			BitmapWorkerTask task = new BitmapWorkerTask(picLayout);
			task.execute(imName);

			super.onPostExecute(result);
		}

		private String getFilePathFromUri(Uri uri) {
			// Use only in a separate thread

			String pathFromUri = null;
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = thiscontext.getContentResolver().query(uri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			pathFromUri = cursor.getString(columnIndex);

			cursor.close();

			return pathFromUri;
		}
	}

	private class InsertImageToMediaStore extends
			AsyncTask<ContentValues, Void, Void> {

		@Override
		protected Void doInBackground(ContentValues... params) {
			thiscontext.getContentResolver().insert(
					Images.Media.EXTERNAL_CONTENT_URI, params[0]);
			return null;
		}
	}

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
		@SuppressWarnings("deprecation")
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

		private Bitmap decodeSampledBitmapFromFilepath(String filepath,
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

		private int calculateInSampleSize(BitmapFactory.Options options,
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

				// Calculate the largest inSampleSize value that is a power of 2
				// and
				// keeps both
				// height and width larger than the requested height and width.
				while ((halfHeight / inSampleSize) > reqHeight
						|| (halfHeight / inSampleSize) > reqWidth) {
					inSampleSize *= 2;
				}
			}

			return inSampleSize;
		}

		private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels)
				throws RuntimeException {

			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
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

	}

	class GetPositionToViewer extends AsyncTask<Void, Void, ArrayList<String>> {
		String path;
		GetPositionToViewer(String path){
			this.path = path;
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ArrayList<String> images;

			images = loadImagesToViewer();

			

			return images;
		}

		@Override
		protected void onPostExecute(ArrayList<String> images) {
			Intent i = new Intent(thiscontext, SingleViewActivity.class);
			
			i.putExtra(Constants.ID, getPositionToViewer(images, path));

			i.putStringArrayListExtra(Constants.LIST, images);
			
			startActivity(i);
			super.onPostExecute(images);
		}

		private ArrayList<String> loadImagesToViewer() {
			DB db = DB.getInstance(thiscontext);
			ArrayList<String> values = new ArrayList<String>();
			db.open();
			Cursor cursor = db.getAllNoteData(noteId);
			while (cursor.moveToNext()) {
				if (cursor.getString(cursor.getColumnIndex(DB.COLUMN_TYPE))
						.equalsIgnoreCase(Constants.Frags.PicFragment.name())) {

					values.add(cursor.getString(cursor
							.getColumnIndex(DB.COLUMN_CONT1)));

				}

			}
			cursor.close();
			db.close();

			return values;
		}

		private int getPositionToViewer(ArrayList<String> values, String path) {
			int position = 0;

			for (int i = 0; i < values.size(); i++) {
				if (values.get(i).equalsIgnoreCase(imName)) {
					position = i;
					Log.d("i",String.valueOf(i));
					Log.d("imName",imName);
				}
			}
			Log.d("curretn image position", String.valueOf(position));
			return position;
		}

	}

}
