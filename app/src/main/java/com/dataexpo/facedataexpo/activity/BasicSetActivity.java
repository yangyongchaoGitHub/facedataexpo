package com.dataexpo.facedataexpo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.activity.set.MinFaceActivity;
import com.dataexpo.facedataexpo.api.FaceApi;
import com.dataexpo.facedataexpo.model.SingleBaseConfig;

public class BasicSetActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BasicSetActivity.class.getSimpleName();
    private Context mContext;
    private TextView tv_min_face;
    private TextView tv_silent_live_type;
    private TextView tv_feature_threshold;
    private TextView tv_rgb_live_threshold;
    private TextView tv_nir_live_threshold;
    private TextView tv_depth_lice_threshold;
    private TextView tv_detect_type;
    private TextView tv_device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_basicset);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        tv_min_face.setText(String.valueOf(SingleBaseConfig.getBaseConfig().getMinimumFace()));
        tv_silent_live_type.setText(SingleBaseConfig.getBaseConfig().getType() == 1 ? "不使用活体"
                : SingleBaseConfig.getBaseConfig().getType() == 2 ? "RGB+LIVE活体"
                : SingleBaseConfig.getBaseConfig().getType() == 3 ? "RGB+NIR活体"
                : SingleBaseConfig.getBaseConfig().getType() == 4 ? "RGB+Depth活体"
                : "不使用活体");
        tv_feature_threshold.setText(String.valueOf(SingleBaseConfig.getBaseConfig().getThreshold()));
        tv_rgb_live_threshold.setText(String.valueOf(SingleBaseConfig.getBaseConfig().getRgbLiveScore()));
        tv_nir_live_threshold.setText(String.valueOf(SingleBaseConfig.getBaseConfig().getNirLiveScore()));
        tv_depth_lice_threshold.setText(String.valueOf(SingleBaseConfig.getBaseConfig().getDepthLiveScore()));
        tv_detect_type.setText("wireframe".equals(SingleBaseConfig.getBaseConfig().getDetectFrame()) ? "全屏线框"
                : "fixed_area".equals(SingleBaseConfig.getBaseConfig().getDetectFrame())
                ? "固定检测区域" : "全屏线框");

        FaceAuth fa = new FaceAuth();
        tv_device_id.setText(fa.getDeviceId(this));
    }

    private void initView() {
        findViewById(R.id.btn_set_confirm).setOnClickListener(this);
        findViewById(R.id.btn_face_depository).setOnClickListener(this);
        findViewById(R.id.btn_basecset_all_config).setOnClickListener(this);
        tv_min_face = findViewById(R.id.tv_min_face_value);
        tv_min_face.setOnClickListener(this);
        findViewById(R.id.tv_basicset_min_face_dspc).setOnClickListener(this);
        tv_silent_live_type = findViewById(R.id.tv_silent_live_type_value);
        tv_feature_threshold = findViewById(R.id.tv_feature_threshold_value);
        tv_rgb_live_threshold = findViewById(R.id.tv_rgb_live_threshold_value);
        tv_nir_live_threshold = findViewById(R.id.tv_nir_live_threshold_value);
        tv_depth_lice_threshold = findViewById(R.id.tv_depth_live_threshold_value);
        tv_detect_type = findViewById(R.id.tv_detect_type_value);
        tv_device_id = findViewById(R.id.tv_device_id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_basicset_min_face_dspc:
            case R.id.tv_min_face_value:
                startActivity(new Intent(mContext, MinFaceActivity.class));
                break;

            case R.id.btn_set_confirm:
                startActivity(new Intent(this, MainWindow.class));
                finish();
                break;
            case R.id.btn_face_depository:
                startActivity(new Intent(this, FaceDepositoryActivity.class));
                break;
            case R.id.btn_basecset_all_config:
                startActivity(new Intent(this, SettingMainActivity.class));
                break;
                default:
        }
    }
}
