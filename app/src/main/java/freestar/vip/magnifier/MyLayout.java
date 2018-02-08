package freestar.vip.magnifier;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import vip.freestar.mylogger.Logger;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2018/2/8
 * github：
 */

public class MyLayout extends FrameLayout {

    private ImageView mResouce;
    private CircleImageView mClip;
    private MyCircle mCircle;

    public MyLayout(@NonNull Context context) {
        this(context, null);
    }

    public MyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Logger.e("onDraw");
    }
}
