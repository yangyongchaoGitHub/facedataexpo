package com.dataexpo.facedataexpo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;

public class FaceRegistActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = FaceRegistActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_regist);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_regist_by_photo).setOnClickListener(this);
        findViewById(R.id.btn_regist_by_gallery).setOnClickListener(this);
        findViewById(R.id.btn_regist_by_depository).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist_by_photo:
                startActivity(new Intent(this, PhotoRegistActivity.class));
                break;
            case R.id.btn_regist_by_gallery:
                break;
            case R.id.btn_regist_by_depository:
                break;
            default:
        }
    }
}
