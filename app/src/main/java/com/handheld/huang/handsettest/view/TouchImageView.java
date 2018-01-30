package com.handheld.huang.handsettest.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 自定义imageview，用于测试触摸
 *
 * @author huang
 * @date 2017/11/17
 */

@SuppressLint("AppCompatCustomView")
public class TouchImageView extends ImageView {

    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap mBitmap;

    public TouchImageView(Context context) {
        super(context);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        int screenWidth = outSize.x;
        int screenHeight = outSize.y;
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.BLUE);
        mCanvas.drawColor(Color.WHITE);
        this.setImageBitmap(mBitmap);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        int screenWidth = outSize.x;
        int screenHeight = outSize.y;
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.BLUE);
        mCanvas.drawColor(Color.WHITE);
        this.setImageBitmap(mBitmap);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);
        int screenWidth = outSize.x;
        int screenHeight = outSize.y;
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.BLUE);
        mCanvas.drawColor(Color.WHITE);
        this.setImageBitmap(mBitmap);
    }

    int startX;
    int startY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int moveY = (int) event.getY();
                mCanvas.drawLine(startX, startY, moveX, moveY, mPaint);
                startX = (int) event.getX();
                startY = (int) event.getY();
                this.setImageBitmap(mBitmap);
                break;
            case MotionEvent.ACTION_UP:
                performClick();
                break;
            default:
                break;
        }
        /* 触摸点等于两个时退出隐藏，结束测试 */
        int pointerCount = 2;
        if (event.getPointerCount() == pointerCount) {
            this.setVisibility(View.GONE);
            mListener.gone();
        }
      return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private AbstractGoneListener mListener;
    public void setListener(AbstractGoneListener listener){
        mListener = listener;
    }
    public static abstract class AbstractGoneListener {
        /** 回调方法，用以监听本控件的消失事件，当本控件消失时，触摸测试界面显示结果确认 */
        public abstract void gone();
    }
}
