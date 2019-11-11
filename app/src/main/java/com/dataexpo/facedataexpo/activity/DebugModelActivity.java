package com.dataexpo.facedataexpo.activity;

import android.os.Bundle;
import android.view.View;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;

public class DebugModelActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_model);
        initView();

    }

    private void initView() {
        findViewById(R.id.btn_debug_model_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_debug_model_back:
                finish();
                break;
                default:
        }
    }
}
