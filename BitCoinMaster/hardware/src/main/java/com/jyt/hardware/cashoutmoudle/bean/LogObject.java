package com.jyt.hardware.cashoutmoudle.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class LogObject implements Parcelable {

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Creator<LogObject> CREATOR = new Creator<LogObject>() {
		@Override
		public LogObject[] newArray(int size) {
			return new LogObject[size];
		}

		@Override
		public LogObject createFromParcel(Parcel source) {
			LogObject obj = new LogObject();
			obj._id = source.readInt();
			obj.type = source.readInt();
			obj.log = source.readString();
			obj.time = source.readString();
			obj.date=source.readString();
			return obj;
		}
	};

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeInt(_id);
		parcel.writeInt(type);
		parcel.writeString(log);
		parcel.writeString(time);
		parcel.writeString(date);
	}

	@Override
	public String toString() {
		return "_id=" + _id + ",type=" + type + ",date="+date+",time=" + time + ",log=" + log;
	}
	
	public int _id;
	public int type;
	public String log;
	public String time;
	public String date;

}
