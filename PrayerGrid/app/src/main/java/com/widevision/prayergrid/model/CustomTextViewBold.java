package com.widevision.prayergrid.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by newtrainee on 7/3/15.
 */
public class CustomTextViewBold extends TextView {


    public CustomTextViewBold(Context context) {

        super(context);

    }

    public CustomTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);
    }

    public CustomTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);

    }

    private void init(AttributeSet attrs) {
      /*  if (attrs != null) {
            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/ProximaNova-Regular.otf");
            setTypeface(myTypeface);
        }*/
    }
}
