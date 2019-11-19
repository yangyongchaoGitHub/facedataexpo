package com.dataexpo.facedataexpo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.activity.set.CameraDisplayAngleActivity;
import com.dataexpo.facedataexpo.activity.set.FaceAuthActivity;

public class SettingMainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SettingMainActivity.class.getSimpleName();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
        mContext = this;
        initView();

    }

    private void initView() {
        findViewById(R.id.ll_debug_model).setOnClickListener(this);
        findViewById(R.id.ll_mirror).setOnClickListener(this);
        findViewById(R.id.ll_min_face).setOnClickListener(this);
        findViewById(R.id.ll_face_detect_angle).setOnClickListener(this);
        findViewById(R.id.ll_camera_display_angle).setOnClickListener(this);
        findViewById(R.id.ll_quality_control).setOnClickListener(this);
        findViewById(R.id.ll_face_liveness_type).setOnClickListener(this);
        findViewById(R.id.ll_face_liveness_threshold).setOnClickListener(this);
        findViewById(R.id.ll_recognize_modle_threshold).setOnClickListener(this);
        findViewById(R.id.ll_detect_follow_starategy).setOnClickListener(this);
        findViewById(R.id.ll_setting_auth).setOnClickListener(this);
        findViewById(R.id.btn_setting_main_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting_main_back:
                finish();
                break;

            case R.id.ll_setting_auth:
                startActivity(new Intent(mContext, FaceAuthActivity.class));
                break;

            case R.id.ll_debug_model:
                startActivity(new Intent(mContext, DebugModelActivity.class));
                break;

            case R.id.tv_mirror:
                break;

            case R.id.ll_min_face:
                break;

            case R.id.ll_face_detect_angle:
                break;

            case R.id.ll_camera_display_angle:
                // 摄像头视频流回显角度
                startActivity(new Intent(this, CameraDisplayAngleActivity.class));
                break;

            case R.id.ll_quality_control:
                break;

            case R.id.ll_face_liveness_type:
                break;

            case R.id.ll_face_liveness_threshold:
                break;

            case R.id.ll_recognize_modle_threshold:
                break;

            case R.id.ll_detect_follow_starategy:
                break;

                default:
        }
    }
}
