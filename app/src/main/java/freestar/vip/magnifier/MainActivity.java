package freestar.vip.magnifier;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vip.freestar.mylogger.Logger;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    @Bind(R.id.iv)
    ImageView iv;
    //    @Bind(R.id.fl)
//    FrameLayout fl;
    @Bind(R.id.save)
    Button save;
    ArrayList<ImageItem> images = null;
    @Bind(R.id.clip)
    ImageView mClip;
    @Bind(R.id.open)
    Button mOpen;
    @Bind(R.id.rl)
    RelativeLayout mRl;

    private ImagePicker imagePicker;

    private int lastX;
    private int lastY;
    private int currentX;
    private int currentY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Logger.init();

        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setMultiMode(false);

        imagePicker.setStyle(CropImageView.Style.CIRCLE);
        Integer radius = 100;
        radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, getResources().getDisplayMetrics());
        imagePicker.setFocusWidth(radius * 2);
        imagePicker.setFocusHeight(radius * 2);

        mClip.setOnTouchListener(this);
//        mClip.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        currentX = (int) event.getX();
//                        currentY = (int) event.getY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        int x2 = (int) event.getX();
//                        int y2 = (int) event.getY();
//                        mRl.scrollBy(currentX - x2, currentY - y2);
//                        currentY = y2;
//                        currentX = x2;
////                        xyValue.setText(x2+","+y2);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//                return true;
//            }
//        });

        Glide.with(this)
//                .load("http://img02.tooopen.com/images/20160509/tooopen_sy_161967094653.jpg")
//                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974003106&di=16528ba917b5872184445706ad48da6e&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3Dbba1bbca5d2c11dfcadcb7600b4e08a5%2Fa8ec8a13632762d02e7bd896aaec08fa513dc656.jpg")
//                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1517974095293&di=5d719eb88ab4eeeab5b128c7b1f62e88&imgtype=0&src=http%3A%2F%2Fpic30.photophoto.cn%2F20140106%2F0006018868664533_b.jpg")
                .load(R.drawable.love_tree)
                .into(iv);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                currentX = (int) event.getRawX();
                currentY = (int) event.getRawY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int x2 = (int) event.getRawX();
                int y2 = (int) event.getRawY();
//                mFl.scrollBy(currentX - x2, currentY - y2);
                currentX = x2;
                currentY = y2;
//                xyValue.setText(x2 + "," + y2);
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
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
                // SD卡根目录
                File sdRoot = Environment.getExternalStorageDirectory();
                File file = new File(sdRoot, Calendar.getInstance().getTimeInMillis() + ".png");
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

    @OnClick({R.id.save, R.id.open})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save:
//                viewSaveToImage(fl);
                break;
            case R.id.open:
                Intent intent = new Intent(this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
                //ImagePicker.getInstance().setSelectedImages(images);
                startActivityForResult(intent, 100);
                break;
        }
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
                        .into(mClip);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Logger.e("触摸了");
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

    /**
     * 是否移动过
     */
    private boolean isMove;

    private float mLastX;
    private float mLastY;
    private float mStartX;
    private float mStartY;
    private long mLastTime;
    private long mCurrentTime;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        int lastX;
        int lastY;
        int left;
        int top;
        int right;
        int bottom;
        int screenWidth;
        int screenHeight;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    mStartX = event.getRawX();
                    mStartY = event.getRawY();
                    mLastTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    screenWidth = mRl.getWidth();
                    screenHeight = mRl.getHeight();

                    if (dx != 0 || dy != 0) {
                        isMove = true;
                    }

                    left = v.getLeft() + dx;
                    top = v.getTop() + dy;
                    right = v.getRight() + dx;
                    bottom = v.getBottom() + dy;
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }
                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }
                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }
                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }
                    v.layout(left, top, right, bottom);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (left <= (screenWidth / 2)) {
                        left = 0;
                    } else {
                        left = screenWidth - mClip.getWidth();
                    }
                    v.layout(left, top, right, bottom);
                    Rect vRect = new Rect();
                    v.getHitRect(vRect);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v
                            .getLayoutParams();
                    lp.leftMargin = vRect.left;
                    lp.topMargin = vRect.top;
                    v.setLayoutParams(lp);

                    mLastX = event.getRawX();
                    mLastY = event.getRawY();
                    mCurrentTime = System.currentTimeMillis();
                    if (mCurrentTime - mLastTime < 800) {//长按不起作用
                        Log.d("kitchee", "开始Y=" + mStartY);
                        Log.d("kitchee", "最后Y=" + mLastY);
                        Log.d("kitchee", "移动Y=" + Math.abs(mStartY - mLastY));
                        if (Math.abs(mStartX - mLastX) < 10.0 && Math.abs(mStartY - mLastY) < 10.0) {//判断是否属于点击
                            Toast.makeText(MainActivity.this, "可以执行点击任务", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @OnClick(R.id.iv)
    public void onViewClicked() {
        Logger.e("图片点击事件");
    }

}