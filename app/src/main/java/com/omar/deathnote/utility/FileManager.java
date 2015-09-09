package com.omar.deathnote.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileManager {

	public final String LOG_TAG = "MYLOG";
	public final String MainFolder = new String("DeathNote");
	public final String MusicFolder = new String("Music/DeathNote");
	public final String ImageFolder = new String("DeathNote/Images");
	public final String Sep = new String(File.separator);
	public File sdPath;

	public FileManager(Context context) {

		sdPath = Environment.getExternalStorageDirectory();
	}

	public void startup() {
		ReplaceFolder replaceFolder = new ReplaceFolder();
		replaceFolder.execute(MainFolder, MusicFolder, ImageFolder);

	}

	private void replaceDir(String folder) {
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

	private void delImages(String imName) {

		try {

			File file = new File(sdPath + "/" + ImageFolder + "/" + imName
					+ ".png");

			if (file.delete()) {
				Log.d(file.getName(), " is deleted!");
			} else {
				Log.d(file.getName(), " delete operation is failed.");
			}

			file = new File(sdPath + "/" + ImageFolder + "/" + imName + ".jpg");

			if (file.delete()) {
				Log.d(file.getName(), " is deleted!");
			} else {
				Log.d(file.getName(), " delete operation is failed.");
			}

			file = new File(sdPath + "/" + ImageFolder + "/" + imName + ".jpeg");
			if (file.delete()) {
				Log.d(file.getName(), " is deleted!");
			} else {
				Log.d(file.getName(), " delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	class DeleteFile extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			delImages(params[0]);
			return null;
		}
	}

	class ReplaceFolder extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			for (String par : params) {

				replaceDir(par);

			}
			return null;
		}

	}

}
