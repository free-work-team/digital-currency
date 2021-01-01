package com.jyt.hardware.printer.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	public static void showShort(Context context,String Message){
		Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
	}
}
