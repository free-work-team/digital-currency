package com.jyt.hardware.cashoutmoudle.utils;

import java.util.Comparator;
//文件按ming排序
public class CompratorByLastModified  implements Comparator<String> {

	@Override
	public int compare(String lhs, String rhs) {
		int result=lhs.compareTo(rhs);
		if (result>0) {
			return 1;
		}else if (result<0) {
			return -1;
		}else{
			return 0;
		}
	
	}
	 
 
}
