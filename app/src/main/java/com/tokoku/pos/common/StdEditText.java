package com.tokoku.pos.common;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.tokoku.pos.util.CommonUtil;

/**
 * Created by Radix on 29/3/2018.
 */

public class StdEditText extends AppCompatEditText {


    public StdEditText(Context context) {
        super(context);
        this.setTypeface(CommonUtil.getStdTypeFace());
    }

    public StdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(CommonUtil.getStdTypeFace());
    }

    public StdEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(CommonUtil.getStdTypeFace());
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }

}
