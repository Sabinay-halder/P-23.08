package com.widevision.prayergrid.model;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by newtrainee on 7/3/15.
 */
public class CustomButton extends Button {

    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
           /* Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/ProximaNova-Regular.otf");
            setTypeface(myTypeface,Typeface.BOLD);*/
        }
    }
}
