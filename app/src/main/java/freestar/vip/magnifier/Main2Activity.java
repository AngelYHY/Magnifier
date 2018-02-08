package freestar.vip.magnifier;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import vip.freestar.mylogger.Logger;

public class Main2Activity extends AppCompatActivity implements View.OnTouchListener {

    @Bind(R.id.iv)
    ImageView mIv;
    @Bind(R.id.fl)
    FrameLayout mFl;
    @Bind(R.id.clip)
    ImageView mClip;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.circle)
    MyCircle mCircle;
    @Bind(R.id.text)
    LinearLayout mText;
    @Bind(R.id.small)
    SeekBar mSmall;
    @Bind(R.id.big)
    SeekBar mBig;
    @Bind(R.id.seek)
    LinearLayout mSeek;

    private float lastX;
    private float lastY;
    private PointF mFocusMidPoint = new PointF();  //中间View的中间点

    private RectF mFocusRect = new RectF();
    ArrayList<ImageItem> images = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        mTitle.setText(getResources().getString(R.string.app_name));

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        Logger.init();

        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setMultiMode(false);
        imagePicker.setCrop(false);

//        Glide.with(this)
////                .load("http://img02.tooopen.com/images/20160509/tooopen_sy_161967094653.jpg")
////                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974003106&di=16528ba917b5872184445706ad48da6e&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3Dbba1bbca5d2c11dfcadcb7600b4e08a5%2Fa8ec8a13632762d02e7bd896aaec08fa513dc656.jpg")
////                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974095293&di=5d719eb88ab4eeeab5b128c7b1f62e88&imgtype=0&src=http%3A%2F%2Fpic30.photophoto.cn%2F20140106%2F0006018868664533_b.jpg")
//                .load(R.drawable.love_tree)
//                .into(mIv);

//        mClip.setOnTouchListener(this);
//        mFl.setOnTouchListener(this);

//        mFl.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                viewSaveToImage(mFl);
                break;
            case R.id.camera:
                Intent intent = new Intent(this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
                //ImagePicker.getInstance().setSelectedImages(images);
                startActivityForResult(intent, 100);
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        Logger.e("", event.getX(), event.getY());
        int[] position = new int[2];
        mFl.getLocationOnScreen(position);
        // 在 FL 范围内
        if (rawX > position[0] && rawX < position[0] + mFl.getWidth() && rawY > position[1] && rawY < position[1] + mFl.getHeight()) {
            mClip.getLocationOnScreen(position);
            // 在 大圆范围内
            if (rawX > position[0] && rawX < position[0] + mClip.getWidth() && rawY > position[1] && rawY < position[1] + mClip.getHeight()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 上一次离开时的坐标
                        lastX = rawX;
                        lastY = rawY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 两次的偏移量
                        moveView(rawX - lastX, rawY - lastY);
                        // 不断修改上次移动完成后坐标
                        lastX = rawX;
                        lastY = rawY;
                        break;
                    default:
                        break;
                }
            } else {  // 非大圆范围
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        viewVisible(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCircle.setCenter(new PointF(event.getX(), mFl.getTop()));
                        break;
                    case MotionEvent.ACTION_UP:
                        mCircle.setCenter(new PointF(mFl.getLeft(), mFl.getTop()));
                        create();
                        break;
                    default:
                        break;
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);

        FileOutputStream fos;
        String imagePath = "";
        try {
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                // SD卡根目录
                String path = Environment.getExternalStorageDirectory() + "/小仙女";
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }

                file = new File(file, Calendar.getInstance().getTimeInMillis() + ".png");
                fos = new FileOutputStream(file);
                imagePath = file.getAbsolutePath();
            } else
                throw new Exception("创建文件失败!");

            cachebmp.compress(Bitmap.CompressFormat.PNG, 90, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Logger.e("imagePath=" + imagePath);

        view.destroyDrawingCache();
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mFocusMidPoint.x = event.getX();
        mFocusMidPoint.y = event.getY();

        Logger.e("", mFl.getTop(), mFl.getLeft(), mFl.getRight(), mFl.getBottom(), "V_ID=", v.getId(), event.getY(), event.getX(), event.getRawX(), event.getRawY());

        if (mFocusMidPoint.x > mFl.getLeft() && mFocusMidPoint.x < mFl.getRight() && mFocusMidPoint.y > mFl.getTop() && mFocusMidPoint.y < mFl.getBottom()) {
            if (v.getId() == R.id.fl) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        viewVisible(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCircle.setCenter(new PointF(event.getX() + mFl.getLeft(), event.getY() + mFl.getTop()));
                        break;
                    case MotionEvent.ACTION_UP:
                        mCircle.setCenter(new PointF(event.getX() + mFl.getLeft(), event.getY() + mFl.getTop()));
                        create();
                        break;
                    default:
                        break;
                }
            } else {
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
                        moveView(rawX - lastX, rawY - lastY);
                        // 不断修改上次移动完成后坐标
                        lastX = rawX;
                        lastY = rawY;
                        break;
                    default:
                        break;
                }
            }
            return true;
        }
        return false;
    }

    private void create() {

        mFocusRect.left = mFocusMidPoint.x - 50;
        mFocusRect.right = mFocusMidPoint.x + 50;
        mFocusRect.top = mFocusMidPoint.y - 50;
        mFocusRect.bottom = mFocusMidPoint.y + 50;

        Bitmap bitmap = makeCropBitmap(((GlideBitmapDrawable) mIv.getDrawable()).getBitmap(), mFocusRect, getImageMatrixRect(), 100, 100, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        100 表示不压缩
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        Glide.with(this)
//                .load("http://img02.tooopen.com/images/20160509/tooopen_sy_161967094653.jpg")
//                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974003106&di=16528ba917b5872184445706ad48da6e&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3Dbba1bbca5d2c11dfcadcb7600b4e08a5%2Fa8ec8a13632762d02e7bd896aaec08fa513dc656.jpg")
//                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974095293&di=5d719eb88ab4eeeab5b128c7b1f62e88&imgtype=0&src=http%3A%2F%2Fpic30.photophoto.cn%2F20140106%2F0006018868664533_b.jpg")
                .load(bytes)
                .override(300, 300)
                .listener(new RequestListener<byte[], GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, byte[] model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, byte[] model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (mClip.getVisibility() == View.GONE) {

                            mClip.setVisibility(View.VISIBLE);

                            mCircle.setVisibility(View.VISIBLE);

                            Bitmap bitmap = ((GlideBitmapDrawable) resource).getBitmap();
                            // 方法三
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mClip.getLayoutParams();
                            params.leftMargin = mIv.getWidth() / 2 - bitmap.getWidth() / 2;
                            params.topMargin = mIv.getHeight() / 2 - bitmap.getHeight() / 2;
                            mClip.setLayoutParams(params);

                        }
                        return false;
                    }
                })
                .into(mClip);
    }

    private void moveView(float offsetX, float offsetY) {
        // 方法三
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mClip.getLayoutParams();
        params.leftMargin = (int) (mClip.getLeft() + offsetX);
        params.topMargin = (int) (mClip.getTop() + offsetY);
        mClip.setLayoutParams(params);
    }

    /**
     * @return 获取当前图片显示的矩形区域
     */
    private RectF getImageMatrixRect() {
        RectF rectF = new RectF();
        rectF.set(0, 0, mIv.getDrawable().getIntrinsicWidth(), mIv.getDrawable().getIntrinsicHeight());
        return rectF;
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                MyAdapter adapter = new MyAdapter(images);
//                gridView.setAdapter(adapter);
                Glide.with(this)
                        .load(Uri.fromFile(new File(images.get(0).path)))
                        .into(mIv);

            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        } else {
            Logger.e("执行了");
        }
    }

//    @OnClick(R.id.rl)
//    public void onViewClicked() {
//        Logger.e("rl click");
//        viewVisible(true);
//    }

    private void viewVisible(boolean visible) {
        mText.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mSeek.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

}
