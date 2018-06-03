package com.tokoku.pos.common;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.tokoku.pos.util.CommonUtil;

/**
 * Created by Radix on 29/3/2018.
 */

public class BoldButton extends AppCompatButton {

    public BoldButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setTypeface(CommonUtil.getBoldTypeFace());
    }

}

