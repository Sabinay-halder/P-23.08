package com.widevision.prayergrid.model;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mercury-five on 24/02/16.
 */
public class CustomTextViewMedium extends TextView {


    public CustomTextViewMedium(Context context) {

        super(context);

    }

    public CustomTextViewMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);
    }

    public CustomTextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);

    }

    private void init(AttributeSet attrs) {
  if (attrs != null) {
            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/Lato-Medium.ttf");
            setTypeface(myTypeface);
        }
    }
}
