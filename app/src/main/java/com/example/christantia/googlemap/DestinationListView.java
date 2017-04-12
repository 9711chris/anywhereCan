package com.example.christantia.googlemap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Christantia on 3/21/2017.
 */

public class DestinationListView extends LinearLayout {
    public final static int PLUS_INT = 0;
    public final static int ARROW_INT = 1;
    public DestinationListView(Context context, DestinationInfo info) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(new LayoutParams(layoutParams));
        layoutParams = new LayoutParams(600, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(30, 30, 30, 30);

        LinearLayout temp = new LinearLayout(context);
        temp.setOrientation(LinearLayout.VERTICAL);
        temp.setLayoutParams(layoutParams);
        TextView tv = new TextView(context);
        tv.setText(info.getName());
        TextView tv2 = new TextView(context);
        tv2.setText(info.getAddress());
        temp.addView(tv);
        temp.addView(tv2);
        addView(temp);
        layoutParams = new LayoutParams(90,90);
        layoutParams.setMargins(30, 65, 30, 65);

        ImageView plus = new ImageView(context);
        plus.setId(PLUS_INT);
        plus.setImageResource(R.drawable.plus);
        plus.setLayoutParams(layoutParams);
        addView(plus);
        ImageView arrow = new ImageView(context);
        arrow.setId(ARROW_INT);
        arrow.setImageResource(R.drawable.navigation);
        arrow.setLayoutParams(layoutParams);
        addView(arrow);
    }
}
