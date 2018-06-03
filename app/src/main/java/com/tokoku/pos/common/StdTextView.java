package com.tokoku.pos.common;

import android.support.v7.widget.AppCompatTextView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.tokoku.pos.util.CommonUtil;

/**
 * Created by Radix on 29/3/2018.
 */

public class StdTextView extends AppCompatTextView {


    public StdTextView(Context context) {
        super(context);
        this.setTypeface(CommonUtil.getStdTypeFace());
    }

    public StdTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(CommonUtil.getStdTypeFace());
    }

    public StdTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(CommonUtil.getStdTypeFace());
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }

}
