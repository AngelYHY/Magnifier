package freestar.vip.magnifier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2018/2/8
 * github：
 */

public class MyCircle extends View {

    private Paint mPaint;

    private int radius;
    private PointF center = new PointF();

    public MyCircle(Context context) {
        this(context, null);
    }

    public MyCircle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initOther();
    }

    private void initOther() {
        radius = PreferencesUtils.getInt(getContext(), PreferencesUtils.SMALL_CIRCLE, 50);

        mPaint = new Paint();
        // 设置画笔为抗锯齿
        mPaint.setAntiAlias(true);
        // 设置颜色为红色
        mPaint.setColor(Color.RED);
        /**
         * 画笔样式分三种： 1.Paint.Style.STROKE：描边 2.Paint.Style.FILL_AND_STROKE：描边并填充
         * 3.Paint.Style.FILL：填充
         */
        mPaint.setStyle(Paint.Style.STROKE);
        /**
         * 设置描边的粗细，单位：像素px 注意：当setStrokeWidth(0)的时候描边宽度并不为0而是只占一个像素
         */
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(center.x, center.y, radius, mPaint);
    }

    public void setRadius(int radius) {
        this.radius = 50 + (radius - 5) * 5;
        invalidate();
    }

    public void setCenter(PointF center) {
        this.center = center;
        invalidate();
    }

    public PointF getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }
}
