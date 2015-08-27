package com.omar.deathnote.utility;

import android.os.Parcel;
import android.os.Parcelable;

public class FragContent implements Parcelable {

	
	private String type;
	private String cont1;
	private String cont2;

	public FragContent( String type, String cont1, String cont2) {
		
		this.type = type;
		this.cont1 = cont1;
		this.cont2 = cont2;

	}

	public FragContent(Parcel in) {
	
		String[] data = new String[3];

		in.readStringArray(data);
		this.type = data[0];
		this.cont1 = data[1];
		this.cont2 = data[2];
	}

	

	public String getType() {
		return type;
	}

	public String getCont1() {
		return cont1;
	}

	public String getCont2() {
		return cont2;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeStringArray(new String[] { type, cont1, cont2 });

	}

	public static final Parcelable.Creator<FragContent> CREATOR = new Parcelable.Creator<FragContent>() {
		public FragContent createFromParcel(Parcel in) {
			return new FragContent(in);
		}

		public FragContent[] newArray(int size) {
			return new FragContent[size];
		}
	};

}
