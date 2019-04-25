package com.cugkuan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author kuan
 * 五角星的进度显示条<br>
 * 请注意，一个五角星是无法装入一个正方形的容器中的；因此宽高比例为1：1的矩形内部五角星是绘制不全的；
 * 因此，如果一个五角星的高度确定，那么宽度需要进行计算；以便五角星能绘制完全
 * 同理，一个五角星的宽度确定，那么高度需要进行动态计算。
 * 所以在使用的使用，如果想要五角星能够恰好撑满，那么只需要指定一个宽度或高度就行了，其它需要进行计算。
 * 五角星的外接圆确定了五角星的大小，内接圆确定了五角星胖瘦
 */
public class PentagramBarView extends View {

    /**
     * 以宽度为测量标准
     */
    public static final int MEASURE_WIDTH = 1;
    /**
     * 以高度为测量标准
     */
    public static final int MEASURE_HEIGHT = 2;


    @IntDef(value = {
            MEASURE_HEIGHT,
            MEASURE_WIDTH
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

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
     * 默认的以宽度为测量标准
     */
    private int mMeasureStyle = MEASURE_HEIGHT;

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


    public PentagramBarView(Context context) {
        super(context);
        init(context, null);
    }

    public PentagramBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PentagramBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //给一个默认的宽度和高度
        setMinimumHeight(20);
        setMinimumWidth(25);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PentagramBarView);
        mMeasureStyle = a.getInt(R.styleable.PentagramBarView_measure_style, MEASURE_HEIGHT);
        mMax = a.getFloat(R.styleable.PentagramBarView_max, 100);
        mProgress = a.getFloat(R.styleable.PentagramBarView_progress, 50);
        if (mProgress >= mMax) {
            mProgress = mMax;
        }
        mCrRatio = a.getFloat(R.styleable.PentagramBarView_CrRatio, 0.5f);
        if (mCrRatio >= MAX_RC) {
            mCrRatio = MAX_RC;
        }

        mStrokeWidth = a.getDimensionPixelSize(R.styleable.PentagramBarView_lineWidth, 2);
        mLineColor = a.getColor(R.styleable.PentagramBarView_lineColor, Color.RED);
        mFillColor = a.getColor(R.styleable.PentagramBarView_fillColor, Color.TRANSPARENT);
        mProgressColor = a.getColor(R.styleable.PentagramBarView_progressColor, Color.RED);

        if (mStrokeWidth >0) {
            mPain = new Paint();
            mPain.setStrokeWidth(mStrokeWidth);
            mPain.setColor(mLineColor);
            mPain.setAntiAlias(true);
            mPain.setStyle(Paint.Style.STROKE);
        }
        mPain2 = new Paint();
        mPain2.setColor(mFillColor);
        mPain2.setStyle(Paint.Style.FILL);
        mPain2.setAntiAlias(true);
    }

    public void setMax(float max) {
        this.mMax = max;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        if (mProgress > mMax){
            mProgress = mMax;
        }
        if (mProgress< 0){
            mProgress = 0;
        }
        invalidate();
    }

    public void setCrRatio(float ratio) {
        mCrRatio = ratio;
        if (mCrRatio >= MAX_RC) {
            mCrRatio = MAX_RC;
        }
        invalidate();
    }

    /**
     * 绘制的依据
     * 如果以高度为准，设置的数值为：MEASURE_HEIGHT;以宽度为准，那么设置的数值为：MEASURE_HEIGHT
     *
     * @param measureStyle
     */
    public void setMeasureStyle(@Duration int measureStyle) {
        mMeasureStyle = measureStyle;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        int width = 0;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //预处理五角星的处理模式
        if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY){
            mMeasureStyle = MEASURE_HEIGHT;
        }else if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY){
            mMeasureStyle = MEASURE_WIDTH;
        }
        if (mMeasureStyle == MEASURE_HEIGHT) {
            if (heightMode == MeasureSpec.EXACTLY) {
                height = MeasureSpec.getSize(heightMeasureSpec);
            } else {
                height = getMinimumHeight();
            }
            CR = getCRbyHeight(height);
            Cr = CR * mCrRatio;
            if (widthMode == MeasureSpec.EXACTLY) {
                width = MeasureSpec.getSize(widthMeasureSpec);
            } else {
                width = (int) (Math.cos(Math.toRadians(18)) * CR * 2) + (mStrokeWidth << 1)
                        + getPaddingLeft() + getPaddingRight();
                width = Math.max(width, getMinimumWidth());
            }

        } else {
            if (widthMode == MeasureSpec.EXACTLY) {
                width = MeasureSpec.getSize(widthMeasureSpec);
            } else {
                width = getMinimumWidth();
            }
            CR = getCRbyWidth(width);
            Cr = CR * mCrRatio;
            if (heightMode == MeasureSpec.EXACTLY) {
                height = MeasureSpec.getSize(heightMeasureSpec);
            } else {
                height = (int) (CR + Math.cos(Math.toRadians(36)) * CR) + (mStrokeWidth << 1)
                        + getPaddingTop() + getPaddingBottom();
                height = Math.max(height, getMinimumHeight());
            }
        }
        mPath = getPoints(CR, Cr);
        setMeasuredDimension(width, height);
    }

    /**
     * 根据给定的高度，计算五角星的的外接圆
     *
     * @param height
     * @return
     */
    private double getCRbyHeight(int height) {
        return (height - (mStrokeWidth << 1) - getPaddingTop() - getPaddingBottom()) / (1 + Math.cos(Math.toRadians(36)));
    }

    /**
     * 根据宽度，计算五角星外接圆
     *
     * @param width
     * @return
     */
    private double getCRbyWidth(int width) {
        return (width - (mStrokeWidth << 1) - getPaddingRight() - getPaddingLeft()) / 2 / (Math.cos(Math.toRadians(18)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mStrokeWidth + getPaddingTop(),
                mStrokeWidth + getPaddingBottom());
        canvas.save();
        //绘制五角星的外
        if (mPain != null) {
            canvas.drawPath(mPath, mPain);
        }
        //比例的绘制填充
        mPain2.setColor(mFillColor);
        canvas.drawPath(mPath, mPain2);
        canvas.save();

        mPain2.setColor(mProgressColor);
        int right = (int) (mProgress / mMax *
                (getMeasuredWidth() - (mStrokeWidth<<1) - getPaddingLeft() - getPaddingRight()));
        canvas.clipRect(0, 0, right, getMeasuredHeight());
        canvas.drawPath(mPath, mPain2);
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

}
