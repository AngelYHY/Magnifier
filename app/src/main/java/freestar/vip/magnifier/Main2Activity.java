package freestar.vip.magnifier;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vip.freestar.mylogger.Logger;

public class Main2Activity extends AppCompatActivity implements View.OnTouchListener {

    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.fl)
    FrameLayout mFl;
    boolean visible;
    @Bind(R.id.clip)
    ImageView mClip;
    private int lastX;
    private int lastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        Logger.init();
        Glide.with(this)
//                .load("http://img02.tooopen.com/images/20160509/tooopen_sy_161967094653.jpg")
//                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974003106&di=16528ba917b5872184445706ad48da6e&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3Dbba1bbca5d2c11dfcadcb7600b4e08a5%2Fa8ec8a13632762d02e7bd896aaec08fa513dc656.jpg")
//                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974095293&di=5d719eb88ab4eeeab5b128c7b1f62e88&imgtype=0&src=http%3A%2F%2Fpic30.photophoto.cn%2F20140106%2F0006018868664533_b.jpg")
                .load(R.drawable.love_tree)
                .into(mIv);

        mClip.setOnTouchListener(this);
    }

    @OnClick(R.id.fl)
    public void onViewClicked() {

        if (!visible) {
            mClip.setVisibility(View.VISIBLE);
        }

        Glide.with(this)
//                .load("http://img02.tooopen.com/images/20160509/tooopen_sy_161967094653.jpg")
//                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974003106&di=16528ba917b5872184445706ad48da6e&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3Dbba1bbca5d2c11dfcadcb7600b4e08a5%2Fa8ec8a13632762d02e7bd896aaec08fa513dc656.jpg")
//                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974095293&di=5d719eb88ab4eeeab5b128c7b1f62e88&imgtype=0&src=http%3A%2F%2Fpic30.photophoto.cn%2F20140106%2F0006018868664533_b.jpg")
                .load(R.mipmap.ic_launcher)
                .override(500, 500)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Bitmap bitmap = ((GlideBitmapDrawable) resource).getBitmap();
                        // 方法三
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mClip.getLayoutParams();
                        params.leftMargin = mIv.getWidth() / 2 - bitmap.getWidth() / 2;
                        params.topMargin = mIv.getHeight() / 2 - bitmap.getHeight() / 2;
                        mClip.setLayoutParams(params);
                        return false;
                    }
                })
                .into(mClip);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // 获取当前触摸的绝对坐标
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 上一次离开时的坐标
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                // 两次的偏移量
                int offsetX = rawX - lastX;
                int offsetY = rawY - lastY;
                moveView(offsetX, offsetY);
                // 不断修改上次移动完成后坐标
                lastX = rawX;
                lastY = rawY;
                break;
            default:
                break;
        }
        return true;
    }

    private void moveView(int offsetX, int offsetY) {
        // 方法一
        // layout(getLeft() + offsetX, getTop() + offsetY, getRight() +
        // offsetX, getBottom() + offsetY);

        // 方法二
        // offsetLeftAndRight(offsetX);
        // offsetTopAndBottom(offsetY);

        // 方法三
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mClip.getLayoutParams();
        params.leftMargin = mClip.getLeft() + offsetX;
        params.topMargin = mClip.getTop() + offsetY;
        mClip.setLayoutParams(params);

        // 方法四
        // ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams)
        // getLayoutParams();
        // layoutParams.leftMargin = getLeft() + offsetX;
        // layoutParams.topMargin = getLeft() + offsetY;
        // setLayoutParams(layoutParams);

        // 方法五
//        mClip.scrollTo(-offsetX, -offsetY);
    }

}
