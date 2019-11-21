package com.dataexpo.facedataexpo.activity.set;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.ConfigUtils;
import com.dataexpo.facedataexpo.manager.FaceSDKManager;
import com.dataexpo.facedataexpo.model.SingleBaseConfig;

import static com.dataexpo.facedataexpo.model.BaseConfig.THRESHOLD_MAX;
import static com.dataexpo.facedataexpo.model.BaseConfig.THRESHOLD_MIN;

public class RecognizeModleThresholdActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {
    private final String TAG = RecognizeModleThresholdActivity.class.getSimpleName();
    private int threshold;
    private EditText et_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_model);
        initView();
        initData();
    }

    private void initData() {
        threshold = SingleBaseConfig.getBaseConfig().getThreshold();
        et_value.setText(String.valueOf(threshold));
    }

    private void initView() {
        findViewById(R.id.btn_recognizemodel_decrease).setOnClickListener(this);
        findViewById(R.id.btn_recognizemodel_increase).setOnClickListener(this);
        findViewById(R.id.btn_recognizemodel_save).setOnClickListener(this);
        //findViewById(R.id.btn_recognizemodel_increase).setOnLongClickListener(this);
        //findViewById(R.id.btn_recognizemodel_decrease).setOnLongClickListener(this);
        et_value = findViewById(R.id.et_recognizemodel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recognizemodel_save:
                SingleBaseConfig.getBaseConfig().setThreshold(Integer.valueOf(et_value.getText().toString()));
                ConfigUtils.modityJson();
                FaceSDKManager.getInstance().initConfig();
                finish();
                break;

            case R.id.btn_recognizemodel_increase:
                if (threshold >= THRESHOLD_MIN && threshold < THRESHOLD_MAX) {
                    threshold += 1;
                    et_value.setText(String.valueOf(threshold));
                }
                break;

            case R.id.btn_recognizemodel_decrease:
                if (threshold > THRESHOLD_MIN && threshold <= THRESHOLD_MAX) {
                    threshold -= 1;
                    et_value.setText(String.valueOf(threshold));
                }
                break;
            default:
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_recognizemodel_decrease:
                if (threshold > THRESHOLD_MIN && threshold <= THRESHOLD_MAX) {
                    threshold -= 1;
                    et_value.setText(String.valueOf(threshold));
                }
                break;
            case R.id.btn_recognizemodel_increase:
                if (threshold >= THRESHOLD_MIN && threshold < THRESHOLD_MAX) {
                    threshold += 1;
                    et_value.setText(String.valueOf(threshold));
                }
                break;
                default:
        }
        return false;
    }
}
