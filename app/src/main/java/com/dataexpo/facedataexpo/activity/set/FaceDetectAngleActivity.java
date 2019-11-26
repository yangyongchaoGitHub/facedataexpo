package com.dataexpo.facedataexpo.activity.set;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.ConfigUtils;
import com.dataexpo.facedataexpo.manager.FaceSDKManager;
import com.dataexpo.facedataexpo.model.SingleBaseConfig;

public class FaceDetectAngleActivity extends BaseActivity implements View.OnClickListener {
    private RadioButton rb_0;
    private RadioButton rb_90;
    private RadioButton rb_180;
    private RadioButton rb_270;
    private int zero = 0;
    private int ninety = 90;
    private int oneHundredEighty = 180;
    private int twoHundredSeventy = 270;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facedetectangle);
        initView();
        initData();
    }

    private void initView() {
        rb_0 = findViewById(R.id.rb_facedetect_0_angle);
        rb_90 = findViewById(R.id.rb_facedetect_90_angle);
        rb_180 = findViewById(R.id.rb_facedetect_180_angle);
        rb_270 = findViewById(R.id.rb_facedetect_270_angle);
        rb_0.setOnClickListener(this);
        rb_90.setOnClickListener(this);
        rb_180.setOnClickListener(this);
        rb_270.setOnClickListener(this);
        findViewById(R.id.iv_facedetectangle_0).setOnClickListener(this);
        findViewById(R.id.iv_facedetectangle_90).setOnClickListener(this);
        findViewById(R.id.iv_facedetectangle_180).setOnClickListener(this);
        findViewById(R.id.iv_facedetectangle_270).setOnClickListener(this);
        findViewById(R.id.btn_facedetectangle_save).setOnClickListener(this);
    }

    public void initData() {
        if (SingleBaseConfig.getBaseConfig().getDetectDirection() == zero) {
            rb_0.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getDetectDirection() == ninety) {
            rb_90.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getDetectDirection() == oneHundredEighty) {
            rb_180.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getDetectDirection() == twoHundredSeventy) {
            rb_270.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_facedetectangle_save:
                if (rb_0.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDetectDirection(zero);
                }
                if (rb_90.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDetectDirection(ninety);
                }
                if (rb_180.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDetectDirection(oneHundredEighty);
                }
                if (rb_270.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDetectDirection(twoHundredSeventy);
                }
                ConfigUtils.modityJson();
                FaceSDKManager.getInstance().initConfig();
                finish();
                break;

            case R.id.iv_facedetectangle_0:
                setChecked(rb_0);
                break;

            case R.id.rb_90_angle:
            case R.id.iv_facedetectangle_90:
                setChecked(rb_90);
                break;

            case R.id.rb_180_angle:
            case R.id.iv_facedetectangle_180:
                setChecked(rb_180);
                break;

            case R.id.rb_270_angle:
            case R.id.iv_facedetectangle_270:
                setChecked(rb_270);
                break;
            default:
        }
    }

    private void setChecked(RadioButton checked) {
        rb_0.setChecked(false);
        rb_90.setChecked(false);
        rb_180.setChecked(false);
        rb_270.setChecked(false);
        checked.setChecked(true);
    }
}
