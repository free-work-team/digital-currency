package com.jyt.bitcoinmaster.facerecognization.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class MyImageView extends ImageView {
    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            System.out
                    .println("MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }
}
