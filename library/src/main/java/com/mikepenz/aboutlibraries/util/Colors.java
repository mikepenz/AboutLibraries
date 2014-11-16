package com.mikepenz.aboutlibraries.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yoav.
 */
public class Colors implements Parcelable {
	public int appBarColor;
	public int statusBarColor;

	public Colors(int toolbarColor, int statusBarColor) {
		this.appBarColor = toolbarColor;
		this.statusBarColor = statusBarColor;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.appBarColor);
		dest.writeInt(this.statusBarColor);
	}

	private Colors(Parcel in) {
		this.appBarColor = in.readInt();
		this.statusBarColor = in.readInt();
	}

	public static final Parcelable.Creator<Colors> CREATOR = new Parcelable.Creator<Colors>() {
		public Colors createFromParcel(Parcel source) {
			return new Colors(source);
		}

		public Colors[] newArray(int size) {
			return new Colors[size];
		}
	};
}
