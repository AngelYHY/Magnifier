package vip.freestar.enlarge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2018/2/7
 * github：
 */

public class ClipView extends View {

    private float circleCenterPX;
    private float circleCenterPY;
    private float radius;
    private Paint paint;
    private Paint borderPaint;
    private int clipHeight;
    private int clipWidth;

    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        borderPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        //画矩形region1
        canvas.clipRect(0, 0, width, height);

        //竖屏的时候width<height,取width的1/3作为半径
        // 横屏的时候width>height,取height的1/3作为半径
        int shortWidth = width < height ? width : height;

        //画圆形region2
        Path path = new Path();
        circleCenterPX = (float) width / 2.0f;
        circleCenterPY = (float) height / 2.0f;

        radius = shortWidth / 3.0f;
        path.addCircle(circleCenterPX, circleCenterPY, radius, Path.Direction.CCW);
        Log.i("ClipView", "onDraw()--circleCenterPX : " + circleCenterPX
                + ", circleCenterPY : " + circleCenterPY + ", radius : " + radius);
        Log.i("ClipView", "onDraw()--width : " + width + ", height : " + height);
        //path.addCircle(150,150,100, Path.Direction.CCW);
        //XOP表示补集就是全集的减去交集剩余部分,这剩余部分不用遮罩
        //也就相当于从遮罩里抠出一个圆形来
        canvas.clipPath(path, Region.Op.XOR);
        //canvas.clipRect(0,0,400,400);
        paint.setAlpha(((int) (255 * 0.4f)));
        canvas.drawRect(0, 0, width, height, paint);
        canvas.save();
        canvas.restore();

        // 画圆形边框
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(5);
        canvas.drawCircle(circleCenterPX, circleCenterPY, radius, borderPaint);

        clipWidth = clipHeight = (int) (radius * 2);

        if (listenerComplete != null) {
            listenerComplete.onDrawComplete();
        }
    }

    void addOnDrawCompleteListener(OnDrawListenerComplete listenner) {
        listenerComplete = listenner;
    }

    void removeOnDrawCompleteListener() {
        listenerComplete = null;
    }

    OnDrawListenerComplete listenerComplete;

    interface OnDrawListenerComplete {
        void onDrawComplete();
    }

    public float getCircleCenterPX() {
        return circleCenterPX;
    }

    public void setCircleCenterPX(float circleCenterPX) {
        this.circleCenterPX = circleCenterPX;
    }

    public float getCircleCenterPY() {
        return circleCenterPY;
    }

    public void setCircleCenterPY(float circleCenterPY) {
        this.circleCenterPY = circleCenterPY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
