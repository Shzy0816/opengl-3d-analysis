package com.shenyutao.opengldemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shenyutao.opengldemo.bean.MatrixState;
import com.shenyutao.opengldemo.bean.ObjModel;
import com.shenyutao.opengldemo.callback.ObjLoadCallback;
import com.shenyutao.opengldemo.databinding.ActivityMainBinding;
import com.shenyutao.opengldemo.render.CubeRender;
import com.shenyutao.opengldemo.render.ImageRender;
import com.shenyutao.opengldemo.render.ObjFileRender;
import com.shenyutao.opengldemo.render.TriangleRender;
import com.shenyutao.opengldemo.task.ObjLoadTask;
import com.shenyutao.opengldemo.third.AnimActivity;
import com.shenyutao.opengldemo.third.TMainActivity;
import com.shenyutao.opengldemo.tool.DialogUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TYPE_CUBE = "cube";
    private static final String TYPE_CUBE_WITH_TEXTURE = "cube_with_texture";
    private static final String TYPE_OBJ = "obj";
    private static final String TYPE_TRIANGLE = "triangle";

    private ActivityMainBinding activityMainBinding;

    private KProgressHUD loadingDialog;

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1000, TimeUnit.SECONDS,
            new SynchronousQueue<>());


    private static final String[] REQUEST_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private static final int PERMISSION_REQUEST_CODE = 1;

    /**
     * String(RenderType - MatrixState)
     */
    private final Map<String, MatrixState> matrixStateMap = new HashMap<>();

    /**
     * ??????Render????????????MatrixState
     */
    private MatrixState mCurMatrixState;

    /**
     * ??????Render????????????????????????????????????Map
     */
    private final Map<String, float[]> angleMap = new HashMap<>();

    /**
     * ??????Render???????????????????????????
     */
    float[] mCurAngle;


    /**
     * ???????????????render??????
     */
    private String renderType = TYPE_OBJ;

    /**
     * ???????????????Render
     */
    private CubeRender mCubeRender;
    private ObjFileRender objFileRender;
    private ImageRender imageReader;
    private TriangleRender triangleRender;

    /**
     * ????????????????????????????????????Bitmap???????????????imageRender??????????????????????????????????????????recycle()??????
     */
    private Bitmap mCurBitmap;

    /**
     * ????????????????????????
     */
    private boolean isCameraUp = false;
    private boolean isCameraDown = false;

    /**
     * ???????????????GLSurfaceView,????????????GLSurfaceView????????????????????????requestRender???
     * ??????????????????Render????????????????????????????????????GLSurfaceView??????
     * ??????????????????????????????GLSurfaceView??????
     */
    private GLSurfaceView mCurGLSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
            requestPermissions(REQUEST_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        MyApplication.setWindow(getWindow());
        init();
    }

    public void loadObjModelAndResetGLSurfaceView() {
        if (mCurGLSurfaceView != null) {
            mCurGLSurfaceView.onPause();
        }
        if (renderType.equals(TYPE_OBJ)) {
            // ???????????????Obj??????
            ObjLoadTask objLoadTask = new ObjLoadTask(objModelPath + "/" + objFolder[checkedItem], objItems[checkedItem], new ObjLoadCallback() {
                @Override
                public void loadStart() {
                    runOnUiThread(() -> showLoadingDialog());
                }

                @Override
                public void loadEnd(List<ObjModel> objModelList) {
                    createNewSurfaceView(objModelList);
                    runOnUiThread(() -> dismissLoadingDialog());
                }
            }, getApplicationContext());
            threadPoolExecutor.execute(objLoadTask);
        } else {
            createNewSurfaceView(null);
        }
    }

    /**
     * ???????????????GLSurfaceView
     * ????????????GLSurfaceView????????????Holder????????????Holder??????????????????LinearLayout
     */
    public void createNewSurfaceView(List<ObjModel> objModelList) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) activityMainBinding.surfaceViewHolder.getChildAt(0).getLayoutParams();
        mCurGLSurfaceView = new GLSurfaceView(getApplicationContext());
        mCurGLSurfaceView.setLayoutParams(layoutParams);
        mCurGLSurfaceView.setEGLContextClientVersion(2);

        // ??????Render
        addRender(objModelList);
        mCurGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCurGLSurfaceView.onResume();
        mCurGLSurfaceView.requestRender();

        if (objModelList != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activityMainBinding.surfaceViewHolder.removeAllViews();
                    activityMainBinding.surfaceViewHolder.addView(mCurGLSurfaceView);
                }
            });
        } else {
            activityMainBinding.surfaceViewHolder.removeAllViews();
            activityMainBinding.surfaceViewHolder.addView(mCurGLSurfaceView);
        }
    }

    /**
     * ?????????
     */
    public void init() {
        initMatrixStateMap();
        initAngleMap();
        initCameraController();
        initRadioGroup();
        initListener();
    }


    private void initMatrixStateMap() {
        matrixStateMap.put(TYPE_CUBE, MatrixState.getDefaultMatrixState(1000, 1000));
        matrixStateMap.put(TYPE_OBJ, MatrixState.getDefaultMatrixState(1000, 1000));
        matrixStateMap.put(TYPE_TRIANGLE, MatrixState.getDefaultMatrixState(1000, 1000));
        matrixStateMap.put(TYPE_CUBE_WITH_TEXTURE, MatrixState.getDefaultMatrixState(1000, 1000));
    }

    private void initAngleMap() {
        angleMap.put(TYPE_CUBE, new float[]{0f, 0f, 0f, 0f});
        angleMap.put(TYPE_TRIANGLE, new float[]{0f, 0f, 0f, 0f});
        angleMap.put(TYPE_OBJ, new float[]{0f, 0f, 0f, 0f});
        angleMap.put(TYPE_CUBE_WITH_TEXTURE, new float[]{0f, 0f, 0f, 0f});
    }

    /**
     * ?????????????????????
     */
    public void initListener() {
        activityMainBinding.buttonTextureChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemAlbum();
            }
        });

        activityMainBinding.buttonObjChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChoiceDialog();
            }
        });

        activityMainBinding.intentToAnimationActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AnimationActivity.class));
            }
        });

        activityMainBinding.intentToBoneAnimationActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoseAnimationDialog();
            }
        });
    }

    private final String objModelPath = "obj_model";
    private final String[] objItems = {"ToyPlane.obj", "IronMan.obj", "batch.obj", "earth.obj", "redcar.obj", "teapot.obj", "DMC4_God_Room.obj"};
    private final String[] objFolder = {"toy_plane", "iron_man", "batch", "earth", "red_car", "teapot", "church"};
    private int checkedItem = 3;

    private void showSingleChoiceDialog() {
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(MainActivity.this);
        singleChoiceDialog.setTitle("??????Obj??????");
        // ????????????????????????????????????????????????0
        singleChoiceDialog.setSingleChoiceItems(objItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem = which;
            }
        });
        singleChoiceDialog.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        objFileRender = null;
                        matrixStateMap.put(TYPE_OBJ, MatrixState.getDefaultMatrixState(1000, 1000));
                        loadObjModelAndResetGLSurfaceView();
                    }
                });

        singleChoiceDialog.setNegativeButton("??????", null);
        singleChoiceDialog.show();
    }

    final String[] animationStringItems = {"????????????", "????????????"};
    final Class<?>[] animationItems = {TMainActivity.class, AnimActivity.class};
    int checkedAnimationItem = 0;

    private void showChoseAnimationDialog() {
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(MainActivity.this);
        singleChoiceDialog.setTitle("??????????????????");
        // ????????????????????????????????????????????????0
        singleChoiceDialog.setSingleChoiceItems(animationStringItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedAnimationItem = which;
            }
        });
        singleChoiceDialog.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(MainActivity.this, animationItems[checkedAnimationItem]));
                    }
                });

        singleChoiceDialog.setNegativeButton("??????", null);
        singleChoiceDialog.show();
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = DialogUtils.getLoadingDialog(MainActivity.this, "???????????????????????????");
        }
        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        loadingDialog.dismiss();
    }

    /**
     * ????????????GLSurfaceView??????Render
     */
    private void addRender(List<ObjModel> objModelList) {
        switch (renderType) {
            case TYPE_OBJ:
                if (objFileRender == null) {
                    objFileRender = new ObjFileRender(objModelList, getApplicationContext());
                    objFileRender.setMatrixState(matrixStateMap.get(TYPE_OBJ));
                }
                mCurGLSurfaceView.setRenderer(objFileRender);
                mCurMatrixState = matrixStateMap.get(TYPE_OBJ);
                mCurAngle = angleMap.get(TYPE_OBJ);
                break;
            case TYPE_CUBE:
                if (mCubeRender == null) {
                    mCubeRender = new CubeRender();
                    mCubeRender.setMatrixState(matrixStateMap.get(TYPE_CUBE));
                }
                mCurGLSurfaceView.setRenderer(mCubeRender);
                mCurMatrixState = matrixStateMap.get(TYPE_CUBE);
                mCurAngle = angleMap.get(TYPE_CUBE);
                break;
            case TYPE_CUBE_WITH_TEXTURE:
                if (imageReader == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image, options);
                    imageReader = new ImageRender(bitmap);
                    imageReader.setMatrixState(matrixStateMap.get(TYPE_CUBE_WITH_TEXTURE));
                    Log.i("Test", "onActivityResult: null");
                }
                Log.i("Test", "onActivityResult: ");
                mCurGLSurfaceView.setRenderer(imageReader);
                mCurMatrixState = matrixStateMap.get(TYPE_CUBE_WITH_TEXTURE);
                mCurAngle = angleMap.get(TYPE_CUBE_WITH_TEXTURE);
                break;
            case TYPE_TRIANGLE:
                if (triangleRender == null) {
                    triangleRender = new TriangleRender();
                }
                mCurGLSurfaceView.setRenderer(triangleRender);
                mCurMatrixState = null;
                break;
        }
    }

    /**
     * ?????????????????????
     */
    @SuppressLint("ClickableViewAccessibility")
    public void initCameraController() {
        activityMainBinding.buttonDown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isCameraDown = true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isCameraDown = false;
            }
            return true;
        });


        activityMainBinding.buttonUp.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isCameraUp = true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isCameraUp = false;
            }
            return true;
        });


        threadPoolExecutor.execute(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                if (isCameraUp) {
                    mCurMatrixState.setCameraPositionZ(mCurMatrixState.getCameraZ() + 0.5f);
                    mCurGLSurfaceView.requestRender();
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        threadPoolExecutor.execute(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                if (isCameraDown) {
                    mCurMatrixState.setCameraPositionZ(mCurMatrixState.getCameraZ() - 0.5f);
                    mCurMatrixState.setCameraPositionZ(mCurMatrixState.getCameraZ());
                    mCurGLSurfaceView.requestRender();
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void initRadioGroup() {
        activityMainBinding.renderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                CharSequence text = radioButton.getText();
                if ("?????????".contentEquals(text)) {
                    renderType = TYPE_CUBE;
                } else if ("Obj??????".contentEquals(text)) {
                    renderType = TYPE_OBJ;
                } else if ("????????????????????????".contentEquals(text)) {
                    renderType = TYPE_CUBE_WITH_TEXTURE;
                } else if ("?????????".contentEquals(text)) {
                    renderType = TYPE_TRIANGLE;
                }
                loadObjModelAndResetGLSurfaceView();
            }
        });
    }


    /**
     * ??????????????????
     */
    private void openSystemAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    if (mCurBitmap != null && !mCurBitmap.isRecycled()) {
                        mCurBitmap.recycle();
                    }
                    mCurBitmap = BitmapFactory.decodeStream(inputStream);
                    imageReader.setBitmap(mCurBitmap);
                    mCurGLSurfaceView.requestRender();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
                Toast.makeText(this, "We need the permission: WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadObjModelAndResetGLSurfaceView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurBitmap != null && !mCurBitmap.isRecycled()) {
            mCurBitmap.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCurMatrixState == null) {
            return super.onTouchEvent(event);
        }
        float x = event.getX() / 10;
        float y = event.getY() / 10;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mCurAngle[0];
                float dy = y - mCurAngle[1];
                mCurAngle[2] += dx;
                mCurAngle[3] += dy;
                mCurAngle[2] %= 360;
                mCurAngle[3] %= 360;
                // ??????Model???????????????????????????????????????????????????
                mCurMatrixState.setModelMatrix(0f, 0f, 0f, mCurAngle[2], mCurAngle[3]);
                mCurGLSurfaceView.requestRender();
                mCurAngle[0] = x;
                mCurAngle[1] = y;
                break;
            case MotionEvent.ACTION_DOWN:
                mCurAngle[0] = x;
                mCurAngle[1] = y;
                break;
            default:
        }
        return super.onTouchEvent(event);
    }
}