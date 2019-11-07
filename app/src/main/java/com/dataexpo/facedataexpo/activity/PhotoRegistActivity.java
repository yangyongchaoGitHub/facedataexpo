package com.dataexpo.facedataexpo.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.LogUtils;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.callback.CameraDataCallback;
import com.dataexpo.facedataexpo.callback.FaceDetectCallBack;
import com.dataexpo.facedataexpo.camera.AutoTexturePreviewView;
import com.dataexpo.facedataexpo.camera.CameraPreviewManager;
import com.dataexpo.facedataexpo.manager.FaceSDKManager;
import com.dataexpo.facedataexpo.model.BaseConfig;
import com.dataexpo.facedataexpo.model.LivenessModel;
import com.dataexpo.facedataexpo.model.SingleBaseConfig;

public class PhotoRegistActivity extends BaseActivity implements View.OnClickListener {
    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
    private static final int PREFER_WIDTH = 640;
    private static final int PERFER_HEIGH = 480;

    private static final int INIT = 0;
    private static final int IN_CAMERA = 1;
    private static final int IN_PHOTO = 2;

    private static final String TAG = PhotoRegistActivity.class.getSimpleName();
    private AutoTexturePreviewView mAutoCameraPreviewView;
    private int mLiveType;
    private byte[] regist_image;

    private int in_get_image = INIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_regist);
        initView();
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTestCloseDebugRegisterFunction();
        in_get_image = IN_CAMERA;
    }

    private void startTestCloseDebugRegisterFunction() {
        // TODO ： 临时放置
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        CameraPreviewManager.getInstance().startPreview(this, mAutoCameraPreviewView,
                PREFER_WIDTH, PERFER_HEIGH, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {

                        if (in_get_image == IN_CAMERA) {
                            regist_image = data;
                        }
//                        // 摄像头预览数据进行人脸检测
//                        FaceSDKManager.getInstance().onDetectCheck(data, null, null,
//                                height, width, mLiveType, new FaceDetectCallBack() {
//                                    @Override
//                                    public void onFaceDetectCallback(LivenessModel livenessModel) {
//                                        // 输出结果
//                                        //checkCloseResult(livenessModel);
//                                    }
//
//                                    @Override
//                                    public void onTip(int code, String msg) {
//                                        //displayTip(code, msg);
//                                    }
//
//                                    @Override
//                                    public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
//                                        //showFrame(livenessModel);
//                                    }
//                                });
                    }
                });
    }

    private void initView() {
        mAutoCameraPreviewView = findViewById(R.id.auto_camera_preview_view);
        mAutoCameraPreviewView.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_cat_photo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cat_photo:
                LogUtils.i(TAG, "cat_photo!!");
                in_get_image = IN_PHOTO;
                CameraPreviewManager.getInstance().stopPreview();
                break;
                default:
        }
    }
}
