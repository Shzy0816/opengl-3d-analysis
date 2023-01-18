package com.shenyutao.opengldemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.shenyutao.opengldemo.R;

/**
 * TODO: 用于当背景的View.
 */
public class BackgroundView extends View {
    private static final String POSITION_TOP_LEFT = "top_left";
    private static final String POSITION_TOP_MID = "top_mid";
    private static final String POSITION_TOP_RIGHT = "top_right";


    private String mLabelPosition = POSITION_TOP_MID;

    private Paint mPaint;
    private Paint mShadowPaint;
    private Paint mBorderPaint;
    private Paint mTextPaint;
    private Paint mLabelRectPaint;
    private Paint mLabelRectShadowPaint;


    private Rect mTextRect;
    private RectF mRectF;
    private RectF mShadowRectF;
    private RectF mLabelRectF;
    private RectF mLabelShadowRectF;


    private int mPaddingTop = 20;
    private int mPaddingLeft = 10;
    private int mTextMarginLeft = 50;
    private int mTextMarginTop = 25;

    private int mShadowDeviation;
    private int mTextSize;
    private String mText = "";

    public BackgroundView(Context context) {
        super(context);
        init(null, 0);
    }

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BackgroundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BackgroundView, defStyle, 0);
        int color = a.getColor(R.styleable.BackgroundView_view_color, Color.WHITE);
        int shadowColor = a.getColor(R.styleable.BackgroundView_shadow_color, color + 100);
        int alpha = a.getInt(R.styleable.BackgroundView_view_alpha, 255);
        int shaderAlpha = a.getInt(R.styleable.BackgroundView_shader_alpha, 100);
        mShadowDeviation = a.getInt(R.styleable.BackgroundView_shadow_deviation, 20);
        mTextSize = a.getInt(R.styleable.BackgroundView_label_text_size, 50);
        mText = a.getString(R.styleable.BackgroundView_text);
        if (mText == null) {
            mText = "空白标签";
        }
        mLabelPosition = a.getString(R.styleable.BackgroundView_label_position);
        if (mLabelPosition == null) {
            mLabelPosition = POSITION_TOP_RIGHT;
        }

        initPaint(color, shadowColor, alpha, shaderAlpha);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initRectF(getMeasuredHeight() - mShadowDeviation, getMeasuredWidth() - mShadowDeviation, mShadowDeviation, mTextSize);
    }

    private void initRectF(int height, int width, int shadowDeviation, int textSize) {
        // 根据文字的size以及文字字符串获取文字矩形
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        mTextRect = new Rect();
        paint.getTextBounds(mText, 0, mText.length(), mTextRect);

        switch (mLabelPosition) {
            case POSITION_TOP_LEFT:
                mLabelRectF = new RectF(mPaddingLeft,
                        mPaddingTop,
                        mPaddingLeft + mTextRect.width() + mTextMarginLeft * 2,
                        mPaddingTop + mTextRect.height() + mTextMarginTop * 2);
                break;
            case POSITION_TOP_MID:
                float d = (width - mPaddingLeft - mTextRect.width()) / 2f - mTextMarginLeft;
                mLabelRectF = new RectF(
                        mPaddingLeft + d,
                        mPaddingTop,
                        mPaddingLeft + d + mTextRect.width() + mTextMarginLeft * 2,
                        mPaddingTop + mTextRect.height() + mTextMarginTop * 2);
                break;
            case POSITION_TOP_RIGHT:
                mLabelRectF = new RectF(mPaddingLeft + (width - mPaddingLeft) - mTextRect.width() - 2 * mTextMarginLeft,
                        mPaddingTop,
                        width,
                        mPaddingTop + mTextRect.height() + mTextMarginTop * 2);
                break;
        }

        // 标签阴影
        mLabelShadowRectF = new RectF(mLabelRectF.left + shadowDeviation,
                mLabelRectF.top + shadowDeviation,
                mLabelRectF.right + shadowDeviation,
                mLabelRectF.bottom + shadowDeviation);
        // 两个背景对应的Rect
        mRectF = new RectF(mPaddingLeft, mLabelRectF.bottom - 12, width, height);
        mShadowRectF = new RectF(mPaddingLeft + shadowDeviation, mLabelRectF.bottom - 5 + shadowDeviation, width + shadowDeviation, height + shadowDeviation);
    }

    private void initPaint(int color, int shadowColor, int alpha, int shadowAlpha) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setAlpha(alpha);
        mPaint.setStyle(Paint.Style.FILL);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStrokeWidth(1);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAlpha(50);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(shadowColor);
        mShadowPaint.setAlpha(shadowAlpha);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setAlpha(50);
        mTextPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(mLabelShadowRectF, 10f, 10f, mShadowPaint);
        canvas.drawRoundRect(mLabelRectF, 10f, 10f, mBorderPaint);

        canvas.drawRoundRect(mShadowRectF, 10f, 10f, mShadowPaint);
        canvas.drawRoundRect(mRectF, 10f, 10f, mPaint);
        canvas.drawRoundRect(mRectF, 10f, 10f, mBorderPaint);

        canvas.drawRoundRect(mLabelRectF, 10f, 10f, mPaint);
        canvas.drawText(mText, mLabelRectF.left + mTextMarginLeft, mLabelRectF.top + mLabelRectF.height() / 3 + mTextMarginTop, mTextPaint);
    }

}