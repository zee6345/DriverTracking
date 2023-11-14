package com.app.drivertracking.presentation.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CustomVerticalProgressBar extends ProgressBar {

    private Paint paint;

    public CustomVerticalProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomVerticalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomVerticalProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.holo_blue_light)); // Set your desired color
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // Swap width and height to make it vertical
        float scale = getMax() > 0 ? getProgress() / (float) getMax() : 0;
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int progressBarWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        int progressHeight = (int) (height * scale);

        canvas.drawRect(getPaddingLeft(), height - progressHeight, getWidth() - getPaddingRight(), height, paint);
    }
}

