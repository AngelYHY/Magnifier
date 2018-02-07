package freestar.vip.magnifier;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.lzy.imagepicker.view.CropImageView;

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
    private float radius = 50;
    private Path mFocusPath = new Path();
    private RectF mFocusRect = new RectF();

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
                .override(200, 200)
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
        // 方法三
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mClip.getLayoutParams();
        params.leftMargin = mClip.getLeft() + offsetX;
        params.topMargin = mClip.getTop() + offsetY;
        mClip.setLayoutParams(params);

        mFocusMidPoint.x=mIv.getWidth() / 2;
        mFocusMidPoint.y=mIv.getHeight() / 2;
//        mFocusPath.addCircle( mFocusMidPoint.x,  mFocusMidPoint.y, radius, Path.Direction.CCW);

        mFocusRect.left = mFocusMidPoint.x -20;
        mFocusRect.right = mFocusMidPoint.x + 20;
        mFocusRect.top = mFocusMidPoint.y - 20;
        mFocusRect.bottom = mFocusMidPoint.y + 20;

        makeCropBitmap(((BitmapDrawable) mIv.getDrawable()).getBitmap(),mFocusRect)
    }

    private PointF mFocusMidPoint = new PointF();  //中间View的中间点

    /**
     * @param bitmap          需要裁剪的图片
     * @param focusRect       中间需要裁剪的矩形区域
     * @param imageMatrixRect 当前图片在屏幕上的显示矩形区域
     * @param expectWidth     希望获得的图片宽度，如果图片宽度不足时，拉伸图片
     * @param exceptHeight    希望获得的图片高度，如果图片高度不足时，拉伸图片
     * @param isSaveRectangle 是否希望按矩形区域保存图片
     * @return 裁剪后的图片的Bitmap
     */
    private Bitmap makeCropBitmap(Bitmap bitmap, RectF focusRect, RectF imageMatrixRect, int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (imageMatrixRect == null || bitmap == null) {
            return null;
        }
        float scale = imageMatrixRect.width() / bitmap.getWidth();
        Log.e("FreeStar", "makeCropBitmap: " + scale);
        int left = (int) ((focusRect.left - imageMatrixRect.left) / scale);
        int top = (int) ((focusRect.top - imageMatrixRect.top) / scale);
        int width = (int) (focusRect.width() / scale);
        int height = (int) (focusRect.height() / scale);

        if (left < 0) left = 0;
        if (top < 0) top = 0;
        if (left + width > bitmap.getWidth()) width = bitmap.getWidth() - left;
        if (top + height > bitmap.getHeight()) height = bitmap.getHeight() - top;

        try {
            bitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
            if (expectWidth != width || exceptHeight != height) {
                bitmap = Bitmap.createScaledBitmap(bitmap, expectWidth, exceptHeight, true);
//                if (mStyle == CropImageView.Style.CIRCLE && !isSaveRectangle) {
                //如果是圆形，就将图片裁剪成圆的
                int length = Math.min(expectWidth, exceptHeight);
                int radius = length / 2;
                Bitmap circleBitmap = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(circleBitmap);
                BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(bitmapShader);
                canvas.drawCircle(expectWidth / 2f, exceptHeight / 2f, radius, paint);
                bitmap = circleBitmap;
//                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
