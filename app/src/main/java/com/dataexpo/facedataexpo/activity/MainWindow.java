package com.dataexpo.facedataexpo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.ConfigUtils;
import com.dataexpo.facedataexpo.Utils.DensityUtils;
import com.dataexpo.facedataexpo.Utils.FaceOnDrawTexturViewUtil;
import com.dataexpo.facedataexpo.Utils.FileUtils;
import com.dataexpo.facedataexpo.Utils.LogUtils;
import com.dataexpo.facedataexpo.Utils.ToastUtils;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.api.FaceApi;
import com.dataexpo.facedataexpo.callback.CameraDataCallback;
import com.dataexpo.facedataexpo.callback.FaceDetectCallBack;
import com.dataexpo.facedataexpo.camera.AutoTexturePreviewView;
import com.dataexpo.facedataexpo.camera.CameraPreviewManager;
import com.dataexpo.facedataexpo.listener.OnImportListener;
import com.dataexpo.facedataexpo.listener.SdkInitListener;
import com.dataexpo.facedataexpo.manager.FaceSDKManager;
import com.dataexpo.facedataexpo.manager.ImportFileManager;
import com.dataexpo.facedataexpo.model.LivenessModel;
import com.dataexpo.facedataexpo.model.SingleBaseConfig;
import com.dataexpo.facedataexpo.model.User;
import com.dataexpo.facedataexpo.view.CircleImageView;
import com.dataexpo.facedataexpo.view.LoginDialog;

public class MainWindow extends BaseActivity implements View.OnClickListener, LoginDialog.OnDialogClickListener {
    private static final String TAG = MainWindow.class.getSimpleName();
    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
//    private static final int PREFER_WIDTH = 640;
//    private static final int PERFER_HEIGH = 480;
    private static final int PREFER_WIDTH = 1280;
    private static final int PERFER_HEIGH = 720;

    private Context mContext;

    // 关闭Debug 模式
    private AutoTexturePreviewView mAutoCameraPreviewView;
    private TextView mDetectText;
    private CircleImageView mDetectImage;
    private TextView mTrackText;
    private TextureView mFaceDetectImageView;
    private Paint paint;
    private RectF rectF;
    private RelativeLayout relativeLayout;
    private int mLiveType;
    private float mRgbLiveScore;
    private RelativeLayout info_rl;
    private Button btn_login;
    private LoginDialog mDialog;
    private EditText et_login;
    private boolean isConfigExit;
    private boolean isInitConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_show);
        mContext = this;

        isConfigExit = ConfigUtils.isConfigExit();
        isInitConfig = ConfigUtils.initConfig();
        if (isInitConfig && isConfigExit) {
            Toast.makeText(mContext, "初始配置加载成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "初始配置失败,将重置文件内容为默认配置", Toast.LENGTH_SHORT).show();
            ConfigUtils.modityJson();
        }

        initView();
        initLicense();
        // 屏幕的宽
        int displayWidth = DensityUtils.getDisplayWidth(mContext);
        // 屏幕的高
        int displayHeight = DensityUtils.getDisplayHeight(mContext);
        // 当屏幕的宽大于屏幕宽时
        if (displayHeight < displayWidth) {
            // 获取高
            int height = displayHeight;
            // 获取宽
            int width = (int) (displayHeight * ((9.0f / 16.0f)));
            // 设置布局的宽和高
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            // 设置布局居中
            params.gravity = Gravity.CENTER;
            relativeLayout.setLayoutParams(params);
        }

        mDialog = new LoginDialog(this);
        mDialog.setDialogClickListener(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
    }

    private void initView() {
        // 活体状态
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
        // 活体阈值
        mRgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // 获取整个布局
        relativeLayout = findViewById(R.id.all_relative);

        //获取底部的界面布局
        info_rl = findViewById(R.id.layout_info);
        //获取注册按钮
        //btn_login = findViewById(R.id.btn_logig);
        findViewById(R.id.btn_login).setOnClickListener(this);

        // 画人脸框
        paint = new Paint();
        rectF = new RectF();
        mFaceDetectImageView = findViewById(R.id.draw_detect_face_view);
        mFaceDetectImageView.setOpaque(false);
        mFaceDetectImageView.setKeepScreenOn(true);

        // 返回
        //Button mButReturn = findViewById(R.id.btn_back);
        //mButReturn.setOnClickListener(this);
        // 设置
        //Button mBtSetting = findViewById(R.id.btn_setting);
        //mBtSetting.setOnClickListener(this);

        // 单目摄像头RGB 图像预览
        mAutoCameraPreviewView = findViewById(R.id.auto_camera_preview_view);
        mAutoCameraPreviewView.setVisibility(View.VISIBLE);

        mDetectText = findViewById(R.id.detect_text);
        mDetectImage = findViewById(R.id.detect_reg_image_item);
        mTrackText = findViewById(R.id.track_txt);

        // 调试按钮
        //findViewById(R.id.debug_btn).setOnClickListener(this);
    }

    private void initLicense() {
        LogUtils.i(TAG, "initLicense!!!!");
        if (FaceSDKManager.initStatus != FaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {
            FaceSDKManager.getInstance().init(mContext, new SdkInitListener() {
                @Override
                public void initStart() {
                    LogUtils.i(TAG, "init sdk start");
                }

                @Override
                public void initLicenseSuccess() {
                    LogUtils.i(TAG, "initLicenseSuccess");
                }

                @Override
                public void initLicenseFail(int errorCode, String msg) {
                    // 如果授权失败，跳转授权页面
                    ToastUtils.toast(mContext, errorCode + msg);
                    // startActivity(new Intent(mContext, FaceAuthActicity.class));
                    LogUtils.i(TAG, "initLicenseFail");
                }

                @Override
                public void initModelSuccess() {
                    LogUtils.i(TAG, "initModelSuccess");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    LogUtils.i(TAG, "initModelFail");
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTestCloseDebugRegisterFunction();
    }

    private void startTestCloseDebugRegisterFunction() {
        // TODO ： 临时放置
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);
        CameraPreviewManager.getInstance().startPreview(this, mAutoCameraPreviewView,
                PREFER_WIDTH, PERFER_HEIGH, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                        // 摄像头预览数据进行人脸检测
                        FaceSDKManager.getInstance().onDetectCheck(data, null, null,
                                height, width, mLiveType, new FaceDetectCallBack() {
                                    @Override
                                    public void onFaceDetectCallback(LivenessModel livenessModel) {
                                        // 输出结果
                                        checkCloseResult(livenessModel);
                                    }

                                    @Override
                                    public void onTip(int code, String msg) {
                                        displayTip(code, msg);
                                    }

                                    @Override
                                    public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                                        showFrame(livenessModel);
                                    }
                                });
                    }
                });
    }

    private void checkCloseResult(final LivenessModel livenessModel) {
        // 当未检测到人脸UI显示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livenessModel == null || livenessModel.getFaceInfo() == null) {
                    mTrackText.setVisibility(View.GONE);
                    mDetectText.setText("未检测到人脸");
                    mDetectImage.setImageResource(R.mipmap.icon_face_);
                    info_rl.setVisibility(View.GONE);
                    return;
                } else {
                    if (mLiveType == 1) {
                        User user = livenessModel.getUser();
                        if (user == null) {
                            mTrackText.setVisibility(View.VISIBLE);
                            mTrackText.setText("识别失败");
                            mTrackText.setBackgroundColor(Color.RED);
                            mDetectText.setText("搜索不到用户");
                            mDetectText.setVisibility(View.VISIBLE);
                            mDetectImage.setImageResource(R.mipmap.icon_face_);
                        } else {

                            String absolutePath = FileUtils.getBatchImportSuccessDirectory()
                                    + "/" + user.getImageName();
                            Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
                            mDetectImage.setImageBitmap(bitmap);
                            mTrackText.setVisibility(View.VISIBLE);
                            mTrackText.setText("识别成功");
                            mTrackText.setBackgroundColor(Color.rgb(66, 147, 136));
                            mDetectText.setText("欢迎您， " + user.getUserName());
                            info_rl.setVisibility(View.VISIBLE);
                        }
                    } else {
                        float rgbLivenessScore = livenessModel.getRgbLivenessScore();
                        if (rgbLivenessScore < mRgbLiveScore) {
                            mTrackText.setVisibility(View.VISIBLE);
                            mTrackText.setText("识别失败");
                            mTrackText.setBackgroundColor(Color.RED);
                            mDetectText.setText("活体检测未通过");
                            mDetectImage.setImageResource(R.mipmap.icon_face_);
                        } else {
                            User user = livenessModel.getUser();
                            if (user == null) {
                                mTrackText.setVisibility(View.VISIBLE);
                                mTrackText.setText("识别失败");
                                mTrackText.setBackgroundColor(Color.RED);
                                mDetectText.setText("搜索不到用户");
                                mDetectText.setVisibility(View.VISIBLE);
                                mDetectImage.setImageResource(R.mipmap.icon_face_);
                            } else {

                                String absolutePath = FileUtils.getBatchImportSuccessDirectory()
                                        + "/" + user.getImageName();
                                Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
                                mDetectImage.setImageBitmap(bitmap);
                                mTrackText.setVisibility(View.VISIBLE);
                                mTrackText.setText("识别成功");
                                mTrackText.setBackgroundColor(Color.rgb(66, 147, 136));
                                mDetectText.setText("欢迎您， " + user.getUserName());
                            }
                        }
                    }
                }
            }
        });
    }

    private void displayTip(final int code, final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (code == 0) {
                    mTrackText.setVisibility(View.GONE);
                } else {
                    mTrackText.setVisibility(View.VISIBLE);
                    mTrackText.setText("识别失败");
                    mTrackText.setBackgroundColor(Color.RED);
                    mDetectImage.setImageResource(R.mipmap.icon_face_);
                }
                mDetectText.setText(tip);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                mDialog.show();
                break;
                default:
        }
    }

    /**
     * 绘制人脸框。
     */
    private void showFrame(final LivenessModel model) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = mFaceDetectImageView.lockCanvas();
                if (canvas == null) {
                    mFaceDetectImageView.unlockCanvasAndPost(canvas);
                    return;
                }
                if (model == null) {
                    // 清空canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mFaceDetectImageView.unlockCanvasAndPost(canvas);
                    return;
                }
                FaceInfo[] faceInfos = model.getTrackFaceInfo();
                if (faceInfos == null || faceInfos.length == 0) {
                    // 清空canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mFaceDetectImageView.unlockCanvasAndPost(canvas);
                    return;
                }
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                FaceInfo faceInfo = faceInfos[0];

                rectF.set(FaceOnDrawTexturViewUtil.getFaceRectTwo(faceInfo));

                // 检测图片的坐标和显示的坐标不一样，需要转换。
                FaceOnDrawTexturViewUtil.mapFromOriginalRect(rectF,
                        mAutoCameraPreviewView, model.getBdFaceImageInstance());
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                // 绘制框
                canvas.drawRect(rectF, paint);
                mFaceDetectImageView.unlockCanvasAndPost(canvas);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImportFileManager.getInstance().release();
    }

    @Override
    public void onConfirmClick(View view) {
        LogUtils.i(TAG, mDialog.et_pswd.getText().toString());
        startActivity(new Intent(this, BasicSetActivity.class));
        mDialog.dismiss();
        finish();
    }

    @Override
    public void onModifierClick(View view) {
        mDialog.dismiss();
    }
}