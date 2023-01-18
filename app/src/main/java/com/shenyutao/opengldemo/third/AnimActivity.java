package com.shenyutao.opengldemo.third;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;
import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE;
import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE_3D_MODEL;
import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE_3D_MODEL_ANIM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shenyutao.opengldemo.MyApplication;
import com.shenyutao.opengldemo.R;
import com.shenyutao.opengldemo.bean.MatrixState;
import com.shenyutao.opengldemo.bean.ObjModel;
import com.shenyutao.opengldemo.callback.AnimationCallback;
import com.shenyutao.opengldemo.databinding.ActivityAnimBinding;
import com.shenyutao.opengldemo.databinding.TActivityMainBinding;
import com.shenyutao.opengldemo.render.ObjFileRender;
import com.shenyutao.opengldemo.tool.DialogUtils;
import com.shenyutao.opengldemo.tool.FileTools;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AnimActivity extends AppCompatActivity implements AnimationRender.FPSListener, AnimationCallback {

    private KProgressHUD loadingDialog;
    private ActivityAnimBinding binding;
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1000, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>());
    private AnimationSurfaceView mCurSurfaceView;
    private BackGroundSurfaceView mBackSurfaceView;
    private AnimationRender mCurGLRender;
    private ObjFileRender mBackRender;
    private float mBackGroundAdjustX = 0f;
    private float mBackGroundAdjustY = 0f;
    private float mBackGroundAdjustZ = 0f;
    private final String animationFileName = "animation/dancing_animation/dance.dae";
    private Point screenPoint;

    {
        screenPoint = new Point();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initListener();

        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //获取屏幕的真实尺寸
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Rect bounds = windowManager.getCurrentWindowMetrics().getBounds();
            screenPoint.x = bounds.right;
            screenPoint.y = bounds.bottom;
        } else {
            windowManager.getDefaultDisplay().getSize(screenPoint);
        }
    }

    private void initListener() {
    }

    private void createNewGlSurfaceView(String animateFileName) {
        if (loadingDialog == null) {
            loadingDialog = DialogUtils.getLoadingDialog(this, "动画加载中，请稍后");
        }
        loadingDialog.show();
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.surfaceViewHolder.getChildAt(0).getLayoutParams();
        if (mCurGLRender != null) {
            mCurGLRender.unInit();
        }

        if (mCurSurfaceView != null) {
            mCurSurfaceView.onPause();
        }
        if (mBackSurfaceView != null) {
            mBackSurfaceView.onPause();
        }
        mCurGLRender = new AnimationRender(AnimationRender.TYPE_ANIMATION);
        mCurGLRender.setAnimationCallback(this);
        mCurGLRender.init();

        mBackRender = new ObjFileRender(ObjModel.loadObjModelFromAssets("obj_model/batch/batch.obj", getApplicationContext(),"obj_model/batch"), getApplicationContext());
        mBackRender.setMatrixState(MatrixState.getDefaultMatrixState(screenPoint.x, screenPoint.y));
        mBackRender.getMatrixState().setViewMatrix(0, 0, -50f);

        mBackRender.getMatrixState().setModelMatrix(mBackGroundAdjustX, mBackGroundAdjustY, mBackGroundAdjustZ, 0, 0);
        mCurGLRender.setParams(SAMPLE_TYPE, SAMPLE_TYPE_3D_MODEL_ANIM, 0, animateFileName);

        mBackSurfaceView = new BackGroundSurfaceView(getApplicationContext(), mBackRender, false);
        mBackSurfaceView.setLayoutParams(layoutParams);

        mCurGLRender.setViewMatrix(0, 0, -150, 0, 0, 0, 0, 1, 0);
        mCurGLRender.adjustPosition(0, -100, 0);


        mCurSurfaceView = new AnimationSurfaceView(this, mCurGLRender, true);
        mCurSurfaceView.setLayoutParams(layoutParams);
        binding.surfaceViewHolder.removeAllViews();
        binding.surfaceViewHolder.addView(mBackSurfaceView);
        binding.surfaceViewHolder.addView(mCurSurfaceView);
        mCurSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
        mBackSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //这里把createNewGlSurfaceView交给子线程，子线程再切换到主线程调用createNewGlSurfaceView
        //为了保证onResume回调不阻塞，点击跳转按钮之后Activity会马上弹出来
        threadPoolExecutor.execute(() -> runOnUiThread(() -> {
            // 讲Assets里面的文件拷贝到外存，方便C层读取（）
            FileTools.copyFromAssetsToExternal(this);
            createNewGlSurfaceView(animationFileName);
        }));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCurSurfaceView != null) {
            mCurSurfaceView.onPause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCurGLRender.unInit();
    }

    @Override
    public void onFpsUpdate(final int fps) {
        runOnUiThread(() -> binding.textInfo.setText("fps: " + fps));
    }

    @Override
    public void onAnimationStart() {
        mCurGLRender.setAnimationCallback(null);
        if (loadingDialog.isShowing()) {
            runOnUiThread(() -> loadingDialog.dismiss());
        }
    }

    private float mPreviousY;
    private float mPreviousX;
    private int mXAngle;
    private int mYAngle;
    private float mPreScale = 1.0f;
    private float mCurScale = 1.0f;
    private long mLastMultiTouchTime;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getPointerCount() == 1) {

            float y = e.getY();
            float x = e.getX();
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dy = y - mPreviousY;
                    float dx = x - mPreviousX;
                    dy /= 4;
                    dx /= 4;
                    mYAngle += dx * TOUCH_SCALE_FACTOR;
                    mXAngle += dy * TOUCH_SCALE_FACTOR;
            }
            mPreviousY = y;
            mPreviousX = x;
            mCurGLRender.updateTransformMatrix(mXAngle, mYAngle, mCurScale, mCurScale);
            mBackRender.getMatrixState().setModelMatrix(mBackGroundAdjustX, mBackGroundAdjustY, mBackGroundAdjustZ, mYAngle, 0);
            mCurSurfaceView.requestRender();
            mBackSurfaceView.requestRender();
        }


        return true;
    }

}