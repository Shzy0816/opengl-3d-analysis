package com.shenyutao.opengldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shenyutao.opengldemo.bean.MatrixState;
import com.shenyutao.opengldemo.bean.ObjModel;
import com.shenyutao.opengldemo.databinding.ActivityAnimationBinding;
import com.shenyutao.opengldemo.render.ObjAnimationRender;
import com.shenyutao.opengldemo.third.AnimationRender;
import com.shenyutao.opengldemo.tool.DialogUtils;
import com.shenyutao.opengldemo.tool.ScreenTools;

import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AnimationActivity extends AppCompatActivity {
    private ActivityAnimationBinding binding;
    private KProgressHUD loadingDialog;
    private KProgressHUD clearingDialog;
    private GLSurfaceView mCurGLSurfaceView;

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1000, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    public void createNewSurfaceView(List<ObjModel> objModelList) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.holder.getChildAt(0).getLayoutParams();
        mCurGLSurfaceView = new GLSurfaceView(getApplicationContext());
        mCurGLSurfaceView.setLayoutParams(layoutParams);
        mCurGLSurfaceView.setEGLContextClientVersion(2);
        ObjAnimationRender animationRender = new ObjAnimationRender(objModelList, getApplicationContext());
        Point point = ScreenTools.getWindow(getApplicationContext());
        animationRender.setMatrixState(MatrixState.getDefaultMatrixState(point.x, point.y));
        mCurGLSurfaceView.setRenderer(animationRender);
        mCurGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mCurGLSurfaceView.onResume();
        mCurGLSurfaceView.requestRender();

        if (objModelList != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.holder.removeAllViews();
                    binding.holder.addView(mCurGLSurfaceView);
                }
            });
        } else {
            binding.holder.removeAllViews();
            binding.holder.addView(mCurGLSurfaceView);
        }
    }


    private void initView() {
        clearingDialog = DialogUtils.getLoadingDialog(getApplicationContext(),"资源释放中，请稍后");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loadingDialog == null) {
            loadingDialog = DialogUtils.getLoadingDialog(this, "动画加载中，请稍后");
        }
        loadingDialog.show();
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<ObjModel> objModelList = ObjModel.loadObjModelFromAssets("obj_model/planet/planets.obj", getApplicationContext(),"obj_model/planet");
                createNewSurfaceView(objModelList);
                loadingDialog.dismiss();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}