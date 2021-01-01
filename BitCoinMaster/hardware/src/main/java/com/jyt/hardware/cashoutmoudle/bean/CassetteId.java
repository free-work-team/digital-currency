package com.jyt.hardware.cashoutmoudle.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CassetteId implements Parcelable {
	
	public static final Creator<CassetteId> CREATOR = new Creator<CassetteId>(){
		@Override
		public CassetteId[] newArray(int size) {
			return new CassetteId[size];
		}

		@Override
		public CassetteId createFromParcel(Parcel source) {
			CassetteId CassetteID = new CassetteId();
			CassetteID.nHopper = source.readInt();
			CassetteID.nID = source.readInt();
			CassetteID.cID = source.readString();
			CassetteID.cCurrency = source.readString();
			CassetteID.nDenomination = source.readInt();
			CassetteID.nBillHorizontal = source.readInt();
			CassetteID.nBillVertical = source.readInt();
			CassetteID.nBillVertical = source.readInt();
			return CassetteID;
		}
	};
    
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(nHopper);
		dest.writeInt(nID);
		dest.writeString(cID);
		dest.writeString(cCurrency);
		dest.writeInt(nDenomination);
		dest.writeInt(nBillHorizontal);
		dest.writeInt(nBillVertical);
		dest.writeInt(nBillThickness);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "CassettID [nHopper=" + nHopper + ", nID=" + nID + ", cID=" + cID+ ", cCurrency=" + cCurrency+ ", nDenomination=" + nDenomination+ ", nBillHorizontal=" + nBillHorizontal+ ", nBillVertical=" + nBillVertical+ ", nBillThickness=" + nBillThickness+ "";
	}
    
	public int nHopper;					//槽位
	public int nID;      				//钞箱ID
	public String cID;					//币种ID
	public String cCurrency;    		//币种代码
	public int nDenomination;    		//币种额度
	public int nBillHorizontal;			//钞票尺寸（横向）
	public int nBillVertical;      		//钞票尺寸（纵向）
	public int nBillThickness;     		//钞票尺寸（厚度）
}