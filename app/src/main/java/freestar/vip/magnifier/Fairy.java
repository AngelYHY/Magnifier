package freestar.vip.magnifier;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2018/2/7
 * github：
 */

public class Fairy extends FrameLayout {

    private int mBorderColor = Color.RED; //焦点框的边框颜色
    private int mBorderWidth = 2;         //焦点边框的宽度（画笔宽度）
    private Paint mBorderPaint = new Paint();
    private Path mFocusPath = new Path();

    public Fairy(@NonNull Context context) {
        this(context, null);
    }

    public Fairy(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Fairy(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
