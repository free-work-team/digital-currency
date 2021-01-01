package com.jyt.bitcoinmaster.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.jyt.bitcoinmaster.R;

import java.util.List;

import android.serialport.SerailPortInfo;

public class MyAdapter extends BaseAdapter {
	private List<SerailPortInfo> dev ;
	private Context context;
	private LayoutInflater inflater;
	private List<String> devices;

	public MyAdapter(Context mcontext, LayoutInflater from,
                     List<SerailPortInfo> devices) {
		this.context = mcontext;
		this.dev = devices;
		this.inflater = from;	
		}

	@Override
	public int getCount() {
		return dev.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if(convertView==null) {
			holder = new ViewHolder();
			convertView=inflater.inflate(R.layout.spinner_item, null);
			holder.serialPort = (TextView) convertView.findViewById(R.id.serialPort);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		SerailPortInfo info = dev.get(position);
		if (info.isExist()) {
			holder.serialPort.setTextColor(Color.BLACK);
		}else{
			holder.serialPort.setTextColor(Color.RED);
		}
		if (info.getCom().equals("请选择")) {
			holder.serialPort.setTextColor(Color.BLACK);
		}
		holder.serialPort.setText(info.getCom());
		return convertView;
	}
}


class ViewHolder {
	TextView serialPort;//序号

}
