package com.jyt.bitcoinmaster.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jyt.bitcoinmaster.R;
import com.jyt.bitcoinmaster.adapter.SpinnerAdapter;

import java.util.List;


public class MySpinner extends LinearLayout  {

    private TextView tv_value;  
    private ImageView bt_dropdown;
    private int mNormalColor;  
    private int mSelectedColor;  
    private Context mcontext;
    private List<String> mItems;
    OnItemSelectedListener listener;
    private SpinnerPopWindow mSpinerPopWindow;
    private SpinnerAdapter mAdapter;
    private MySpinner spinner;
    View myView;
  
  
    public MySpinner(Context context) {  
        super(context);  
        mcontext = context;
        init();
    } 
  
    public MySpinner(Context context, AttributeSet attrs) {  
        super(context, attrs); 
        mcontext = context; 
        init();
    }  
  
    private void init(){
    	 LayoutInflater mInflater = LayoutInflater.from(mcontext);  
         myView = mInflater.inflate(R.layout.myspinner_layout, null);
         addView(myView);  
         
         tv_value = (TextView) myView.findViewById(R.id.tv_value);
         bt_dropdown = (ImageView) myView.findViewById(R.id.bt_dropdown);
         tv_value.setOnClickListener(onClickListener);
         bt_dropdown.setOnClickListener(onClickListener);
    }
    
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            bt_dropdown.setBackgroundResource(R.drawable.up_arrow);
        	startPopWindow();	
        }
    }; 
    
    public void setData(List<String> datas){
    	mItems = datas;
    }
    
    public void setOnItemSelectedListener(MySpinner spinner,OnItemSelectedListener listener){
    	this.listener = listener;
    	this.spinner = spinner;
    }

    public void setSelection(int position){
        tv_value.setText(mItems.get(position));
    }
    public void startPopWindow(){
    	mAdapter = new SpinnerAdapter(mcontext);
		mAdapter.refreshData(mItems, 0);

		mSpinerPopWindow = new SpinnerPopWindow(spinner,mcontext);
		mSpinerPopWindow.setAdatper(mAdapter);
		mSpinerPopWindow.setItemListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(MySpinner spinner,int pos) {
				// TODO Auto-generated method stub
                bt_dropdown.setBackgroundResource(R.drawable.down_arrow);
				tv_value.setText(mItems.get(pos));
				listener.onItemSelected(spinner,pos);
			}			
		});
        showSpinWindow();
    }

    private void showSpinWindow(){
        mSpinerPopWindow.setWidth(myView.getWidth());
        mSpinerPopWindow.showAsDropDown(myView);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(MySpinner spinner,int pos);
    }
}  