package com.cugkuan.editor.starview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.cugkuan.widget.PentagramBarDrawable;

public class ScoreDrawable extends Drawable {


    public PentagramBarDrawable pentagramBarDrawable;

    private Paint mPaint;


    private String text = "2.5 分";

    private int width;

    private float mTextHeight;

    private int baseLineY;


    private int mHeight;


    public ScoreDrawable(PentagramBarDrawable drawable) {

        pentagramBarDrawable = drawable;
        mPaint = new Paint();
        mPaint.setTextSize(32);
        mPaint.setColor(Color.WHITE);

        float w = mPaint.measureText(text);
        width = pentagramBarDrawable.getIntrinsicWidth() + (int) w;
        setBounds(0, 0, width, pentagramBarDrawable.getIntrinsicWidth());

        mTextHeight = getFontHeight(mPaint);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mHeight = pentagramBarDrawable.getMinimumHeight();



    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        pentagramBarDrawable.draw(canvas);

        int tY = (int) (pentagramBarDrawable.getIntrinsicHeight() - getFontHeight(mPaint))/2;

        canvas.save();
        canvas.translate(pentagramBarDrawable.getIntrinsicWidth(), tY);
        canvas.drawText(text, 0, getFontHeight(mPaint), mPaint);
        canvas.restore();

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return pentagramBarDrawable.getOpacity();
    }

    private float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return ((float) Math.ceil(fm.descent - fm.top) + 2) / 2;
    }
}
