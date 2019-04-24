package com.cugkuan.editor.starview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.IntDef;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author kuan
 * 五角星的进度显示条<br>
 * 请注意，一个五角星是无法装入一个正方形的容器中的；因此宽高比例为1：1的矩形内部五角星是绘制不全的；
 * 因此，如果一个五角星的高度确定，那么宽度需要进行计算；以便五角星能绘制完全
 * 同理，一个五角星的宽度确定，那么高度需要进行动态计算。
 * 所以在使用的使用，如果想要五角星能够恰好撑满，那么只需要指定一个宽度或高度就行了，其它需要进行计算。
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
     * 五角星的外接圆
     */
    private double CR;
    /**
     * 五角星的内接圆
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
    private int mFillColor = Color.BLACK;

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
        mFillColor = a.getColor(R.styleable.PentagramBarView_fillColor, Color.YELLOW);

        mPain = new Paint();
        mPain.setStrokeWidth(mStrokeWidth);
        mPain.setColor(mLineColor);
        mPain.setAntiAlias(true);
        mPain.setStyle(Paint.Style.STROKE);
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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int width = 0;
        if (mMeasureStyle == MEASURE_HEIGHT) {
            switch (MeasureSpec.getMode(heightMeasureSpec)) {
                case MeasureSpec.UNSPECIFIED:
                case MeasureSpec.EXACTLY:
                    height = MeasureSpec.getSize(heightMeasureSpec);
                    break;
            }
            //为了屏幕有个可见的，代码删除了也没关系
            if (height == 0) {
                height = 40;
            }

            CR = height / (1 + Math.cos(Math.toRadians(36)));
            Cr = CR * mCrRatio;
            switch (MeasureSpec.getMode(widthMeasureSpec)) {
                case MeasureSpec.AT_MOST:
                    width = (int) (Math.cos(Math.toRadians(18)) * CR * 2);
                    break;
                default:
                    width = MeasureSpec.getSize(widthMeasureSpec);
                    break;
            }
        } else {
            switch (MeasureSpec.getMode(widthMeasureSpec)) {
                case MeasureSpec.UNSPECIFIED:
                case MeasureSpec.EXACTLY:
                    width = MeasureSpec.getSize(widthMeasureSpec);
                    break;
            }
            //为了屏幕有一个可见的，代码删除了也没关系
            if (width == 0) {
                width = 40;
            }

            CR = width / 2 / (Math.cos(Math.toRadians(18)));
            Cr = CR * mCrRatio;
            switch (MeasureSpec.getMode(heightMeasureSpec)) {
                case MeasureSpec.AT_MOST:
                    height = (int) (CR + Math.cos(Math.toRadians(36)) * CR);
                    break;
                default:
                    height = MeasureSpec.getSize(heightMeasureSpec);
                    break;
            }
        }
        mPath = getPoints(CR, Cr);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制五角星的外
        canvas.drawPath(mPath, mPain);
        canvas.save();
        //比例的绘制填充
        int right = (int) (mProgress / mMax * getMeasuredWidth());
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
