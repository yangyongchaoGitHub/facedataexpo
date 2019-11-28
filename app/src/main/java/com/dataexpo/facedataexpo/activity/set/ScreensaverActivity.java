package com.dataexpo.facedataexpo.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.listener.OnServeiceCallback;
import com.dataexpo.facedataexpo.service.MainApplication;

import java.awt.font.TextAttribute;

import static com.dataexpo.facedataexpo.service.BgService.ACTION_HAVE_FACE;
import static com.dataexpo.facedataexpo.service.BgService.ACTION_TIMEOUT;

public class ScreensaverActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screensaver);
        initView();
        onServeiceCallback = new OnServeiceCallback() {
            @Override
            public void onCallback(int action) {
                if (action == ACTION_HAVE_FACE) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("ScreensaverActivity", "ACTION_HAVE_FACE-----");
                            if (MainApplication.getInstance().getService() != null) {
                                MainApplication.getInstance().getService().touch();
                                MainApplication.getInstance().getService().setCallback(null);
                            }
                            ScreensaverActivity.this.finish();
                        }
                    });
                }
            }
        };
    }

    @Override
    protected void onResume() {
        if (MainApplication.getInstance().getService() != null) {
            MainApplication.getInstance().getService().setCallback(onServeiceCallback);
        }
        super.onResume();
    }

    private void initView() {
        findViewById(R.id.iv_screensave).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (MainApplication.getInstance().getService() != null) {
            MainApplication.getInstance().getService().touch();
        }
        this.finish();
    }
}
