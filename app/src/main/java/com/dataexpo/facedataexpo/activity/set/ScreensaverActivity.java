package com.dataexpo.facedataexpo.activity.set;

import android.os.Bundle;
import android.view.View;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.service.MainApplication;

public class ScreensaverActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screensaver);
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_screensave).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MainApplication.getInstance().getService().setbInLongTimeNoTouch(false);
        this.finish();
    }
}
