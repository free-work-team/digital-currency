package com.jyt.hardware.cashoutmoudle.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CassetteInfo implements Parcelable {
	private static final String TAG = "CassetteInfo";
	public static final Creator<CassetteInfo> CREATOR = new Creator<CassetteInfo>() {
		@Override
		public CassetteInfo[] newArray(int size) {
			return new CassetteInfo[size];
		}

		@Override
		public CassetteInfo createFromParcel(Parcel source) {
			CassetteInfo CassetteInfo = new CassetteInfo();
			
			CassetteInfo.sCassetteID = source.readParcelable(CassetteId.class.getClassLoader());
			CassetteInfo.eType = source.readInt();
			CassetteInfo.eStatus = source.readInt();
			CassetteInfo.nTotalCount = source.readInt();
			CassetteInfo.nRemainCount = source.readInt();
			CassetteInfo.nOutCount = source.readInt();
			Log.d(TAG, "nOutCount:" + CassetteInfo.nOutCount);
			CassetteInfo.nRejectCount = source.readInt();
			Log.d(TAG, "nRejectCount:" + CassetteInfo.nRejectCount);
			CassetteInfo.nInvalidCount = source.readInt();
			CassetteInfo.nLogicID = source.readInt();
			Log.d(TAG, "nLogicID:" + CassetteInfo.nLogicID);
			return CassetteInfo;
		}
	};
    
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(sCassetteID, PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeInt(eType);
		dest.writeInt(eStatus);
		dest.writeInt(nTotalCount);
		dest.writeInt(nRemainCount);
		Log.d(TAG, "writeToParcel nRemainCount:" + nRemainCount);
		dest.writeInt(nOutCount);
		Log.d(TAG, "writeToParcel nOutCount:" + nOutCount);
		dest.writeInt(nRejectCount);
		Log.d(TAG, "writeToParcel nRejectCount:" + nRejectCount);
		dest.writeInt(nInvalidCount);
		Log.d(TAG, "writeToParcel nInvalidCount:" + nInvalidCount);
		dest.writeInt(nLogicID);
		Log.d(TAG, "writeToParcel nLogicID:" + nLogicID);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "CassettInfo ["+"sCassetteID"+sCassetteID+"eType=" + eType + ", eStatus=" + eStatus + ", nTotalCount=" + nTotalCount+ ", nRemainCount=" + nRemainCount+ ", nOutCount=" + nOutCount+ ", nRejectCount=" + nRejectCount+ ", nInvalidCount=" + nInvalidCount+ ", nLogicID=" + nLogicID+ "]";
	}
	
	public CassetteId sCassetteID;				// 钞箱ID
	public int eType;   						// 钞箱类型
	public int eStatus;    						// 钞箱状态
	public int	nTotalCount;  					// 总张数
	public int	nRemainCount;					// 剩余张数
	public int	nOutCount;						// 当前出钞张数(或总出钞数量)
  	public int	nRejectCount;					// 回收数
  	public int	nInvalidCount;					// 废钞数
  	public int	nLogicID;						// 逻辑钞箱ID
}
