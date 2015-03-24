package com.omar.deathnote;

import java.io.File;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class FileManager {

	public final String LOG_TAG = "MYLOG";
	public final String MainFolder = new String("DeathNote");
	public final String MusicFolder = new String("Music/DeathNote");
	public final String ImageFolder = new String("DeathNote/Images");
	public final String Sep = new String(File.separator);
	public File sdPath;

 

	public FileManager(Context context) {
	 
		sdPath = context.getExternalFilesDir(null);
	}

	class DeleteFile extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			delImages(params[0]);
			return null;
		}
	}

	 
		
		 

	public void startup() {

		replaceDir(MainFolder);
		replaceDir(MusicFolder);
		replaceDir(ImageFolder);
	}

	public void createDir(String folder) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.d("MEDIA ", " not mounted");
		} else {

			File f1 = new File(sdPath.getAbsolutePath() + "/" + folder);

			f1.mkdirs();
			Log.d("making dir ==>", f1.getAbsolutePath());

		}

	}

	public void replaceDir(String folder) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.d("MEDIA ", " not mounted");
		} else {

			File f1 = new File(sdPath.getAbsolutePath() + "/" + folder);
			if (f1.exists()) {
				if (f1.delete())
					Log.d("deleted", f1.getAbsolutePath());
			}
			f1.mkdirs();
			Log.d("making dir ==>", f1.getAbsolutePath());

		}

	}

	public File loadFile(long id, String type) {
		File f1 = null;

		switch (type) {

		case "IMG":
			f1 = new File(ImageFolder + "/" + id + ".png");

			break;

		}

		return f1;
	}

	public void delFile(String type, long name) {
		switch (type) {

		case "IMG":

			break;
		case "BTM":
			break;
		}

	}

	public void delImages(String imName) {

		try {

			File file = new File(sdPath + "/" + ImageFolder + "/" + imName
					+ ".png");

			if (file.delete()) {
				Log.d(file.getName(), " is deleted!");
			} else {
				Log.d(file.getName(), " Delete operation is failed.");
			}

			file = new File(sdPath + "/" + ImageFolder + "/" + imName + ".jpg");

			if (file.delete()) {
				Log.d(file.getName(), " is deleted!");
			} else {
				Log.d(file.getName(), " Delete operation is failed.");
			}

			file = new File(sdPath + "/" + ImageFolder + "/" + imName + ".jpeg");
			if (file.delete()) {
				Log.d(file.getName(), " is deleted!");
			} else {
				Log.d(file.getName(), " Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
