package com.cugkuan.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 以高度为标准，必须设置一个指定的高度;
 * 这个PentagramBarDrawable 只负责绘制足量大小的五角星
 * 如果想 padding 等效果，请使用其他间接的方法，如 将⭐️放在其他 Drawable 中
 */
public class PentagramBarDrawable extends Drawable {


    private Paint mPain;
    private Paint mPain2;
    /**
     * 五角星的外接圆半径
     */
    private double CR;
    /**
     * 五角星的内切圆半径，内切圆的半径，决定了五角星的胖瘦
     */
    private double Cr;
    /**
     * 五角星的路径
     */
    private Path mPath;
    /**
     * 进度条的最高值
     */
    private float mMax = 100;
    /**
     * 目前的进度
     */
    private float mProgress = 50;

    /**
     * 外接圆与内切圆的半径比例，决定了 五角星的胖瘦
     */
    private float mCrRatio = 0.5f;


    /**
     * 五角星线条的宽度
     */
    private int mStrokeWidth = 2;
    /**
     * 五角星线条的颜色
     */
    private int mLineColor = Color.BLACK;
    /**
     * 五角星填充的颜色
     */
    private int mFillColor = Color.TRANSPARENT;

    private int mProgressColor = Color.RED;

    /**
     * 内切圆与外接圆的最大比例值，不能大于这个比例值
     */
    private float MAX_RC = (float) (Math.cos(Math.toRadians(36)));

    /**
     * 正五角星的比例
     */
    public static final float REGULAR_RATIO =
            (float) (Math.sin(Math.toRadians(18)) / Math.sin(Math.toRadians(54)));


    private int mHeight;

    /**
     * 这个值必须进行设定
     */
    private int mWidth;


    public PentagramBarDrawable(int height) {
        CR = getCRbyHeight(height);
        Cr = CR * mCrRatio;
        mHeight = height;
        mWidth = (int) (Math.cos(Math.toRadians(18)) * CR * 2) + (mStrokeWidth << 1);
    }


    public PentagramBarDrawable commit(Options options) {

        setCrRatio(options.mCrRatio);
        setStrokeWidth(options.mStrokeWidth);
        setLineColor(options.mLineColor);
        setProgressColor(options.mProgressColor);
        setFillColor(options.mFillColor);

        //计算
        Cr = CR * mCrRatio;
        mWidth = (int) (Math.cos(Math.toRadians(18)) * CR * 2) + (mStrokeWidth << 1);
        mPath = getPoints(CR, Cr);
        setBounds(0, 0, mWidth, mHeight);

        mPain = options.mPain;
        mPain2 = options.mPain2;



        return this;
    }


    public void setProgress(float progress) {
        mProgress = progress;
    }

    public void setMax(float max) {
        mMax = max;
    }

    public void setCrRatio(float ratio) {
        if (ratio <= 0.0f) {
            return;
        }
        mCrRatio = ratio;
        if (mCrRatio >= MAX_RC) {
            mCrRatio = MAX_RC;
        }
    }

    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    public void setLineColor(int color) {
        mLineColor = color;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
    }

    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    /**
     * 此方法必须调用，否则，得不到正确的结果
     *
     * @return
     */
    public PentagramBarDrawable commit() {

        mPain = new Paint();
        mPain.setStrokeWidth(mStrokeWidth);
        mPain.setColor(mLineColor);
        mPain.setAntiAlias(true);
        mPain.setStyle(Paint.Style.STROKE);
        mPain2 = new Paint();
        mPain2.setColor(mFillColor);
        mPain2.setStyle(Paint.Style.FILL);
        mPain2.setAntiAlias(true);
        return this;
    }


    @Override
    public void draw(@NonNull Canvas canvas) {

        canvas.save();
        canvas.translate(mStrokeWidth, mStrokeWidth);

        //绘制五角星的外面的的线条
        if (mStrokeWidth > 0) {
            canvas.drawPath(mPath, mPain);
        }
        //比例的绘制填充
        mPain2.setColor(mFillColor);
        canvas.drawPath(mPath, mPain2);

        mPain2.setColor(mProgressColor);
        int right = (int) (mProgress / mMax *
                (mWidth - (mStrokeWidth << 1)));
        canvas.clipRect(0, 0, right, mHeight);
        canvas.drawPath(mPath, mPain2);

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
        return PixelFormat.UNKNOWN;
    }

    /**
     * 根据给定的高度，计算五角星的的外接圆
     *
     * @param height
     * @return
     */
    private double getCRbyHeight(int height) {
        return (height - (mStrokeWidth << 1)) / (1 + Math.cos(Math.toRadians(36)));
    }

    /**
     * 五角星路径生成的核心代码
     *
     * @param R
     * @param r
     * @return
     */
    private Path getPoints(double R, double r) {
        Path path = new Path();
        float perDeg = 360 / 5;
        float degA = perDeg / 2 / 2;
        float degB = 360 / (5 - 1) / 2 - degA / 2 + degA;
        path.moveTo((float) (Math.cos((degA + perDeg * 0) / 180 * Math.PI) * R + R * Math.cos(degA / 180 * Math.PI)),
                (float) (-Math.sin((degA + perDeg * 0) / 180 * Math.PI) * R + R));
        for (int i = 0; i < 5; i++) {
            path.lineTo((float) (Math.cos((degA + perDeg * i) / 180 * Math.PI) * R + R * Math.cos(degA / 180 * Math.PI)),
                    (float) (-Math.sin((degA + perDeg * i) / 180 * Math.PI) * R + R));
            path.lineTo((float) (Math.cos((degB + perDeg * i) / 180 * Math.PI) * r + R * Math.cos(degA / 180 * Math.PI)),
                    (float) (-Math.sin((degB + perDeg * i) / 180 * Math.PI) * r + R));
        }
        path.close();
        return path;
    }

    public static class Options {


        private float mCrRatio = 0.5f;
        private int mStrokeWidth;
        private int mLineColor;
        private int mProgressColor;
        private int mFillColor;

        private Paint mPain;
        private Paint mPain2;


        public Options() {


            mPain = new Paint();
            mPain.setAntiAlias(true);
            mPain.setStyle(Paint.Style.STROKE);


            mPain2 = new Paint();
            mPain2.setStyle(Paint.Style.FILL);
            mPain2.setAntiAlias(true);

        }


        public Options setCrRatio(float ratio) {
            mCrRatio = ratio;
            return this;
        }

        public Options setStrokeWidth(int strokeWidth) {
            mStrokeWidth = strokeWidth;
            mPain.setStrokeWidth(mStrokeWidth);
            return this;
        }

        public Options setLineColor(int color) {
            mLineColor = color;
            mPain.setColor(mLineColor);
            return this;
        }

        public Options setProgressColor(int progressColor) {
            mProgressColor = progressColor;
            return this;
        }

        public Options setFillColor(int fillColor) {
            mFillColor = fillColor;
            mPain2.setColor(mFillColor);
            return this;
        }


    }
}
