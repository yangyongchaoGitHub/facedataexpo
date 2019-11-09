package com.dataexpo.facedataexpo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.BitmapUtils;
import com.dataexpo.facedataexpo.Utils.FileUtils;
import com.dataexpo.facedataexpo.Utils.ImageUtils;
import com.dataexpo.facedataexpo.Utils.LogUtils;
import com.dataexpo.facedataexpo.Utils.ToastUtils;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.api.FaceApi;
import com.dataexpo.facedataexpo.callback.CameraDataCallback;
import com.dataexpo.facedataexpo.callback.FaceDetectCallBack;
import com.dataexpo.facedataexpo.callback.FaceFeatureCallBack;
import com.dataexpo.facedataexpo.camera.AutoTexturePreviewView;
import com.dataexpo.facedataexpo.camera.CameraPreviewManager;
import com.dataexpo.facedataexpo.manager.FaceSDKManager;
import com.dataexpo.facedataexpo.manager.FaceTrackManager;
import com.dataexpo.facedataexpo.manager.UserInfoManager;
import com.dataexpo.facedataexpo.model.BaseConfig;
import com.dataexpo.facedataexpo.model.LivenessModel;
import com.dataexpo.facedataexpo.model.SingleBaseConfig;
import com.dataexpo.facedataexpo.view.LoginDialog;
import com.dataexpo.facedataexpo.view.RegistUserDialog;

import java.io.File;

import static com.dataexpo.facedataexpo.manager.UserInfoManager.NAME_EXIST;
import static com.dataexpo.facedataexpo.manager.UserInfoManager.NAME_NULL;

public class PhotoRegistActivity extends BaseActivity implements View.OnClickListener, LoginDialog.OnDialogClickListener {
    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
    private static final int PREFER_WIDTH = 1024;
    private static final int PERFER_HEIGH = 600;

    private static final int INIT = 0;
    private static final int IN_CAMERA = 1;
    private static final int IN_PHOTO = 2;

    private static final String TAG = PhotoRegistActivity.class.getSimpleName();
    private AutoTexturePreviewView mAutoCameraPreviewView;
    private int mLiveType;
    private byte[] regist_image;
    private int regist_width;
    private int regist_height;
    private RelativeLayout rl_photo_sensor;
    private Button btn_cancel;
    private Button btn_confirm;
    private Button btn_cat_photo;
    private Context mContext;
    private LivenessModel mModel;
    private RegistUserDialog mDialog;
    private Bitmap rgbBitmap = null;
    private String uName;

    private int in_get_image = INIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_regist);
        initView();
        mContext = this;
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTestCloseDebugRegisterFunction();
        in_get_image = IN_CAMERA;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cat_photo:
                LogUtils.i(TAG, "cat_photo!!");
                in_get_image = IN_PHOTO;
                CameraPreviewManager.getInstance().stopPreview();
                rl_photo_sensor.setVisibility(View.VISIBLE);
                btn_cat_photo.setVisibility(View.INVISIBLE);
                break;

            case R.id.btn_regist_cancel:
                LogUtils.i(TAG, "restart camera!!!!!!");
                //startTestCloseDebugRegisterFunction();
                //in_get_image = IN_CAMERA;
//                onResume();
//                btn_cat_photo.setVisibility(View.VISIBLE);
//                rl_photo_sensor.setVisibility(View.INVISIBLE);
//                mAutoCameraPreviewView.invalidate();
                finish();
                break;

            case R.id.btn_regist_ok:
                faceDetect();
                //finish();
                break;
            default:
        }
    }

    @Override
    public void onConfirmClick(View view) {
        mDialog.dismiss();
        uName = mDialog.et_name.getText().toString().trim();

        if (UserInfoManager.getInstance().checkNameExist(uName) == NAME_NULL) {
            register(mModel);
        }
    }

    @Override
    public void onModifierClick(View view) {
        mDialog.dismiss();
        //finish();
    }

    private void initView() {
        mAutoCameraPreviewView = findViewById(R.id.auto_camera_preview_view);
        mAutoCameraPreviewView.setVisibility(View.VISIBLE);
        btn_cat_photo = findViewById(R.id.btn_cat_photo);
        btn_cat_photo.setOnClickListener(this);

        findViewById(R.id.btn_regist_cancel).setOnClickListener(this);
        findViewById(R.id.btn_regist_ok).setOnClickListener(this);
        rl_photo_sensor = findViewById(R.id.rl_photo_censor);

        mDialog = new RegistUserDialog(this);
        mDialog.setDialogClickListener(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
    }

    //进行注册
    private void register(LivenessModel model) {
        if (model == null) {
            ToastUtils.toast(mContext, TAG + "model is null!!");
            return;
        }

        BDFaceImageInstance image = model.getBdFaceImageInstance();
        rgbBitmap = BitmapUtils.getInstaceBmp(image);
        // 获取选择的特征抽取模型
        int modelType = SingleBaseConfig.getBaseConfig().getActiveModel();
        if (modelType == 1) {
            // 生活照
            LogUtils.i(TAG, "in statr regist!!!!");
            FaceSDKManager.getInstance().onFeatureCheck(model.getBdFaceImageInstance(), model.getLandmarks(),
                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO, new FaceFeatureCallBack() {
                        @Override
                        public void onFaceFeatureCallBack(float featureSize, byte[] feature) {
                            displayCompareResult(featureSize, feature);
                            LogUtils.i(TAG, String.valueOf(feature.length));
                        }
                    });

        } else if (modelType == 2) {
            // 证件照
            FaceSDKManager.getInstance().onFeatureCheck(model.getBdFaceImageInstance(), model.getLandmarks(),
                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO, new FaceFeatureCallBack() {
                        @Override
                        public void onFaceFeatureCallBack(float featureSize, byte[] feature) {
                            displayCompareResult(featureSize, feature);
                        }
                    });
        }
    }

    // 根据特征抽取的结果 注册人脸
    private void displayCompareResult(float ret, byte[] faceFeature) {

        // 特征提取成功
        if (ret == 128) {

            String imageName = "default-" + uName + ".jpg";
            // 注册到人脸库
            boolean isSuccess = FaceApi.getInstance().registerUserIntoDBmanager("default", uName, imageName,
                    "", faceFeature);
            if (isSuccess) {
                // 关闭摄像头
                //CameraPreviewManager.getInstance().stopPreview();
                ToastUtils.toast(mContext, "注册成功: " + uName);

                // 压缩、保存人脸图片至300 * 300
                File faceDir = FileUtils.getBatchImportSuccessDirectory();
                File file = new File(faceDir, imageName);
                ImageUtils.resize(rgbBitmap, file, 300, 300);

                // 数据变化，更新内存
                FaceApi.getInstance().initDatabases(true);

                finish();
            } else {
                ToastUtils.toast(mContext, "特征值提取成功，录入数据库失败");
            }

        } else if (ret == -1) {
            ToastUtils.toast(mContext, "特征值提取失败");
        } else {
            ToastUtils.toast(mContext, "特征值提取失败");
        }
    }

    private void faceDetect() {
        int liveType = SingleBaseConfig.getBaseConfig().getType();
        if (liveType == 1) { // 无活体检测
            FaceTrackManager.getInstance().setAliving(false);
        } else if (liveType == 2) { // 活体检测
            FaceTrackManager.getInstance().setAliving(true);
        }

        LogUtils.i(TAG, "Detect image: " + regist_image[1] + regist_image[2]);

        FaceTrackManager.getInstance().faceTrack(regist_image, regist_width, regist_height, new FaceDetectCallBack() {
            @Override
            public void onFaceDetectCallback(LivenessModel livenessModel) {
                // 做过滤
                //boolean isFilterSuccess = faceSizeFilter(livenessModel.getFaceInfo(), regist_width, regist_height);
                //if (isFilterSuccess) {
                    // 展示model
                    checkResult(livenessModel);
                //}
            }

            @Override
            public void onTip(int code, final String msg) {

            }

            @Override
            public void onFaceDetectDarwCallback(LivenessModel livenessModel) {

            }
        });
    }

    private void checkResult(LivenessModel livenessModel) {
        if (livenessModel == null) {
            ToastUtils.toast(mContext, "检测异常");
            return;
        }

        int liveType = SingleBaseConfig.getBaseConfig().getType();
        // 无活体
        if (liveType == 1) {
            ToastUtils.toast(mContext, "采集成功！！！！");
            mModel = livenessModel;
            //displayResult(model, null);
            // 注册
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDialog.show();
                }
            });
        }
    }

    private void startTestCloseDebugRegisterFunction() {
        // TODO ： 临时放置
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);
        CameraPreviewManager.getInstance().startPreview(this, mAutoCameraPreviewView,
                PREFER_WIDTH, PERFER_HEIGH, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {

                        if (in_get_image == IN_CAMERA) {
                            regist_image = data;
                            regist_width = width;
                            regist_height = height;
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

    // 人脸大小顾虑
    public boolean faceSizeFilter(FaceInfo faceInfo, int bitMapWidth, int bitMapHeight) {

        // 判断人脸大小，若人脸超过屏幕二分一，则提示文案“人脸离手机太近，请调整与手机的距离”；
        // 若人脸小于屏幕三分一，则提示“人脸离手机太远，请调整与手机的距离”
        float ratio = (float) faceInfo.width / (float) bitMapHeight;
//        if (ratio > 0.6) {
//
//            displayTip("人脸离屏幕太近，请调整与屏幕的距离");
//            return false;
//        } else if (ratio < 0.2) {
//            displayTip("人脸离屏幕太远，请调整与屏幕的距离");
//            return false;
//        } else if (faceInfo.centerX > bitMapWidth * 3 / 4) {
//            displayTip("人脸在屏幕中太靠右");
//            return false;
//        } else if (faceInfo.centerX < bitMapWidth / 4) {
//            displayTip("人脸在屏幕中太靠左");
//            Log.e("qing", "--------屏幕中太靠左--------");
//            return false;
//        } else if (faceInfo.centerY > bitMapHeight * 3 / 4) {
//            displayTip("人脸在屏幕中太靠下");
//            return false;
//        } else if (faceInfo.centerY < bitMapHeight / 4) {
//            displayTip("人脸在屏幕中太靠上");
//            return false;
//        }

        return true;
    }
}
