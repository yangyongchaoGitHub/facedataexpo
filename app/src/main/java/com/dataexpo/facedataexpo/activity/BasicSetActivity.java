package com.dataexpo.facedataexpo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;

public class BasicSetActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BasicSetActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basicset);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_set_confirm).setOnClickListener(this);
        findViewById(R.id.btn_face_depository).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_confirm:
                startActivity(new Intent(this, MainWindow.class));
                finish();
                break;
            case R.id.btn_face_depository:
                startActivity(new Intent(this, FaceDepositoryActivity.class));
                break;
                default:
        }
    }
}
