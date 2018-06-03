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

public class BoldEditText extends AppCompatEditText {


    public BoldEditText(Context context) {
        super(context);
        this.setTypeface(CommonUtil.getBoldTypeFace());
        setEnabled(true);
    }

    public BoldEditText(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
        this.setTypeface(CommonUtil.getBoldTypeFace());
        setEnabled(true);
    }

    public BoldEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(CommonUtil.getBoldTypeFace());
        setEnabled(true);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

}
