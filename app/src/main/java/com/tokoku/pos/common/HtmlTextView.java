package com.tokoku.pos.common;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;

import com.tokoku.pos.util.CommonUtil;

/**
 * Created by Radix on 29/3/2018.
 */

public class HtmlTextView extends AppCompatTextView {

    public HtmlTextView(Context context) {
        super(context);
        init();
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setText(Html.fromHtml(getText().toString()));
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }

}
