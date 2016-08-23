package com.widevision.prayergrid.model;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by newtrainee on 7/3/15.
 */
public class CustomTextView extends TextView {


    public CustomTextView(Context context) {

        super(context);

    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);

    }

    private void init(AttributeSet attrs) {
     if (attrs != null) {
            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Lato-Regular.ttf");
            setTypeface(myTypeface);
        }
    }
}
