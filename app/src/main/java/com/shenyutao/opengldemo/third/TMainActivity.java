package com.shenyutao.opengldemo.third;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;
import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE;
import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE_3D_MODEL;
import static com.shenyutao.opengldemo.third.NativeRender.SAMPLE_TYPE_3D_MODEL_ANIM;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shenyutao.opengldemo.callback.AnimationCallback;
import com.shenyutao.opengldemo.databinding.TActivityMainBinding;
import com.shenyutao.opengldemo.tool.DialogUtils;
import com.shenyutao.opengldemo.tool.FileTools;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TMainActivity extends Activity implements AnimationRender.FPSListener, AnimationCallback {
    private static final String TAG = "TMainActivity";

    private KProgressHUD loadingDialog;
    private TActivityMainBinding binding;
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1000, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>());

    private static final String[] REQUEST_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };


    private static final int PERMISSION_REQUEST_CODE = 1;
    private AnimationSurfaceView mCurSurfaceView;
    private AnimationSurfaceView mBackSurfaceView;
    private AnimationRender mCurGLRender;
    private AnimationRender mBackRender;
    private final String animationFileName = "animation/fighting_animation/fight.dae";
    private final String backGroundFileName = "background/fighting_background/fightBackground.obj";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void createNewGlSurfaceView(String animateFileName, String backgroundFileName) {
        if (loadingDialog == null) {
            loadingDialog = DialogUtils.getLoadingDialog(this, "动画加载中，请稍后");
        }
        loadingDialog.show();
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.surfaceViewHolder.getChildAt(0).getLayoutParams();
        if (mCurGLRender != null) {
            mCurGLRender.unInit();
        }
        if (mBackRender != null) {
            mBackRender.unInit();
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

        mBackRender = new AnimationRender(AnimationRender.TYPE_BACKGROUND);
        mBackRender.init();

        mCurGLRender.setParams(SAMPLE_TYPE, SAMPLE_TYPE_3D_MODEL_ANIM, 0, animateFileName);
        mBackRender.setParams(SAMPLE_TYPE, SAMPLE_TYPE_3D_MODEL, 0, backgroundFileName);
        mBackSurfaceView = new AnimationSurfaceView(this, mBackRender, false);
        mBackSurfaceView.setLayoutParams(layoutParams);

        mCurGLRender.setViewMatrix(220,220,0,0,0,0,0,1,0);
        mCurGLRender.adjustPosition(0,-100,0);
        mBackRender.setViewMatrix(900,900,0,0,0,0,0,1,0);
        mBackRender.adjustPosition(0,550,0);


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
            FileTools.copyFromAssetsToExternal(getApplicationContext());
            createNewGlSurfaceView(animationFileName, backGroundFileName);
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
                Toast.makeText(this, "We need the permission: WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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


    protected boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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
            consumeTouchEvent(e);

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
            mBackRender.updateTransformMatrix(mXAngle, mYAngle, mCurScale, mCurScale);
            mCurSurfaceView.requestRender();
            mBackSurfaceView.requestRender();
        }


        return true;
    }


    public void consumeTouchEvent(MotionEvent e) {

        float touchX = -1, touchY = -1;
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                touchX = e.getX();
                touchY = e.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
                touchX = -1;
                touchY = -1;
                break;
            default:
                break;
        }

        //滑动、触摸
        mCurGLRender.setTouchLoc(touchX, touchY);
        mBackRender.setTouchLoc(touchX, touchY);
        mCurSurfaceView.requestRender();
        mBackSurfaceView.requestRender();

    }

    public void dealClickEvent(MotionEvent e) {
        float touchX = -1, touchY = -1;
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                touchX = e.getX();
                touchY = e.getY();
            {
                //点击
                mCurGLRender.setTouchLoc(touchX, touchY);
                mBackRender.setTouchLoc(touchX, touchY);
            }
            break;
            default:
                break;
        }
    }

}
