package vip.freestar.enlarge;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import vip.freestar.mylogger.Logger;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.fl)
    FrameLayout mFl;
    @Bind(R.id.iv)
    ImageView mIv;
    private ClipView clipview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Logger.init();

        final Matrix matrix = new Matrix();

        //初始化截图区域自定义view
        clipview = new ClipView(this);
        clipview.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {

            public void onDrawComplete() {
                clipview.removeOnDrawCompleteListener();
                int radius = (int) clipview.getRadius();
                int midX = (int) clipview.getCircleCenterPX();
                int midY = (int) clipview.getCircleCenterPY();

                int imageWidth = mIv.getWidth();
                int imageHeight = mIv.getHeight();
//                // 按裁剪框求缩放比例
//                float scale = (radius * 3.0f) / imageWidth;
//
//                // 起始中心点
//                float imageMidX = imageWidth * scale / 2;
//                float imageMidY = imageHeight * scale / 2;
//                srcPic.setScaleType(ImageView.ScaleType.MATRIX);
//
//                // 缩放
//                matrix.postScale(scale, scale);
//                // 平移
//                matrix.postTranslate(midX - imageMidX, midY - imageMidY);
//
//                srcPic.setImageMatrix(matrix);
//                srcPic.setImageBitmap(bitmap);
            }
        });

        matrix.reset();
        mFl.addView(clipview, new ViewGroup.LayoutParams(
                500, 500));

    }
}
