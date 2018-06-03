package com.tokoku.pos.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.tokoku.pos.util.CommonUtil;

/**
 * Created by Radix on 29/3/2018.
 */

public class BoldTextView extends AppCompatTextView {


    public BoldTextView(Context context) {
        super(context);
        this.setTypeface(CommonUtil.getBoldTypeFace());
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(CommonUtil.getBoldTypeFace());
    }

    public BoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(CommonUtil.getBoldTypeFace());
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }

}
