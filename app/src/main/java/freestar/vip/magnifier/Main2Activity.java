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

public class Main2Activity extends AppCompatActivity implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

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

    private RectF mFocusRect = new RectF();
    ArrayList<ImageItem> images = null;
    private boolean visible = true;
    private int radius;
    //获取控件在屏幕的位置
    int[] location = new int[2];
    private PointF point = new PointF();

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

        mFl.setOnTouchListener(this);

        mSmall.setOnSeekBarChangeListener(this);
        mBig.setOnSeekBarChangeListener(this);

        radius = PreferencesUtils.getInt(this, PreferencesUtils.BIG_CIRCLE, 150);
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
                startActivityForResult(intent, 100);
                break;
            case R.id.custom:
                viewVisible();
                break;
        }
        return true;
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
//                // SD卡根目录
                String path = Environment.getExternalStorageDirectory() + "/小仙女";
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }

                file = new File(path, Calendar.getInstance().getTimeInMillis() + ".png");

//                File sdRoot = Environment.getExternalStorageDirectory();
//                File file = new File(sdRoot, Calendar.getInstance().getTimeInMillis() + ".png");

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
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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

        // 获取当前触摸的绝对坐标
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();

        Logger.e("", mFl.getTop(), mFl.getLeft(), mFl.getRight(), mFl.getBottom(), event.getY(), event.getX(), event.getRawX(), event.getRawY());

        mClip.getLocationOnScreen(location);

        //半径
        int r = (mClip.getRight() - mClip.getLeft()) / 2;

        //圆心坐标
        int vCenterX = location[0] + r;
        int vCenterY = location[1] + r;

        //点击位置x坐标与圆心的x坐标的距离
        int distanceX = Math.abs(vCenterX - rawX);
        //点击位置y坐标与圆心的y坐标的距离
        int distanceY = Math.abs(vCenterY - rawY);
        //点击位置与圆心的直线距离
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        // 触摸大圆
        if (distanceZ < r) {
            Logger.e("触摸大圆");

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 上一次离开时的坐标
                    lastX = rawX;
                    lastY = rawY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    mFl.getLocationOnScreen(location);
                    // 判断 圆是否会 超出 图片的范围
                    if (rawX > location[0] + radius && rawX < location[0] + mFl.getWidth() - radius && rawY > location[1] + radius && rawY < location[1] + mFl.getHeight() - radius) {
                        // 两次的偏移量
                        moveView(rawX - lastX, rawY - lastY);
                        // 不断修改上次移动完成后坐标
                        lastX = rawX;
                        lastY = rawY;
                    }

                    break;
                default:
                    break;
            }
        } else {  // 非大圆部分
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    mFl.getLocationOnScreen(location);
                    // 判断 圆是否会 超出 图片的范围
                    if (rawX > location[0] + mCircle.getRadius() && rawX < location[0] + mFl.getWidth() - mCircle.getRadius() && rawY > location[1] + mCircle.getRadius() && rawY < location[1] + mFl.getHeight() - mCircle.getRadius()) {
                        //圆心点的计算
                        setCircleCenter(rawX, rawY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    setCircleCenter(rawX, rawY);
                    point.x = event.getX();
                    point.y = event.getY();
                    create();
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private void setCircleCenter(int rawX, int rawY) {
//        int centerX = rawX - location[0];
//        int centerY = rawY - location[1];
        mCircle.getLocationOnScreen(location);
        Logger.e("", rawX, rawY, location[0], location[1]);
        mCircle.setCenter(new PointF(rawX - location[0], rawY - location[1]));
    }

    private void create() {

        mFocusRect.left = point.x - mCircle.getRadius();
        mFocusRect.right = point.x + mCircle.getRadius();
        mFocusRect.top = point.y - mCircle.getRadius();
        mFocusRect.bottom = point.y + mCircle.getRadius();

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
                .override(radius * 2, radius * 2)
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
        rectF.set(mIv.getLeft(), mIv.getTop(), mIv.getRight(), mIv.getBottom());
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

    private void viewVisible() {
        mText.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        mSeek.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        visible = !visible;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar.getId() == R.id.small) { // 小圆半径
            mCircle.setRadius(seekBar.getProgress());
        } else { //大圆半径
            radius = 150 + (seekBar.getProgress() - 5) * 10;
        }
        create();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.small) { // 小圆半径
            PreferencesUtils.putInt(this, PreferencesUtils.SMALL_CIRCLE, mCircle.getRadius());
        } else { //大圆半径
            PreferencesUtils.putInt(this, PreferencesUtils.BIG_CIRCLE, radius);
        }
    }
}
