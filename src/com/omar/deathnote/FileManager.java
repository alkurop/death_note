package com.omar.deathnote;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class FileManager {

	 
	public final String LOG_TAG = "MYLOG";
	public final String MainFolder = new String("DeathNote");
/*	public final String XMLFolder = new String("DeathNote/XML");*/
	public final String MusicFolder = new String("Music/DeathNote");

	public final String ImageFolder = new String("DeathNote/Images");
/*	public final String TNFolder = new String("DeathNote/Images/Thumbnails");*/

/*	public final String LinkFloder = new String("DeathNote/LinkFloder");*/
	public final String Sep = new String (File.separator);
	/* final String sdLoc = new String("/mnt/sdcard/"); */
	public File sdPath;

	public FileManager() {
		sdPath = Environment.getExternalStorageDirectory();
	}

	public void startup() {

		replaceDir(MainFolder);
		replaceDir(MusicFolder);
		replaceDir(ImageFolder);
	/*	replaceDir(LinkFloder);
		replaceDir(TNFolder);*/

	}

	public void createDir(String folder) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.d("MEDIA "," not mounted");
		} else {

			File f1 = new File(sdPath.getAbsolutePath() + "/" + folder);
			 
				f1.mkdirs();
				Log.d("making dir ==>", f1.getAbsolutePath());
		 
		
		}

	}
	public void replaceDir(String folder) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.d("MEDIA "," not mounted");
		} else {

			File f1 = new File(sdPath.getAbsolutePath() + "/" + folder);
			 if(f1.exists()){
				 if(f1.delete())     Log.d("deleted", f1.getAbsolutePath());} 
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

	/*public String loadXML(long id) {
		File f1 = null;
		String s = null;
		String content = null;
		 

		try {
			f1 = new File(XMLFolder + "/" + id + ".xml");
			FileReader fileReader = new FileReader(f1);
			BufferedReader bufferedReader = new BufferedReader (fileReader);
			while((s = bufferedReader.readLine()) != null){
			
			content += s; }
			bufferedReader.close();
		} catch (FileNotFoundException e) {
				
			e.printStackTrace();
		} catch (IOException e) {
	 
			e.printStackTrace();
		}

		

		return content;
	}*/
	public void delFile(String type, long name){
		switch(type){
		/*case "XML":

        String tempFile = (sdPath.getAbsolutePath() + "/"+ XMLFolder + "/" + name + ".xml");
		try{ 
		        File fileTemp = new File(tempFile);
		          if (fileTemp.exists()){
		             fileTemp.delete();
		          }   
		      }catch(Exception e){
		         // if any error occurs
		         e.printStackTrace();
		      }
		   break;*/
		case "IMG":   
			
			
			
			
			
			
		break;
		case "BTM":
		break;
		}
		
		
		
	}
	///////////////////////  SAVERS
	 
	
	public void delImages(String s,long id) {
		String imName = String.valueOf(id) + "_" + s;
		 
		try {

			File file = new File( sdPath + "/" +  ImageFolder + "/"
					+ imName + ".png");

			if (file.delete()) {
				Log.d(file.getName(), " is deleted!");
			} else {
				Log.d(file.getName(), " Delete operation is failed.");
			}

			file = new File( sdPath + "/" +  ImageFolder + "/" + imName
					+ ".jpg");

			if (file.delete()) {
				Log.d(file.getName(), " is deleted!");
			} else {
				Log.d(file.getName(), " Delete operation is failed.");
			}
			
			file = new File( sdPath + "/" +  ImageFolder + "/" + imName
					+ ".jpeg");
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


