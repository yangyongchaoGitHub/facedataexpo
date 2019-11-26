package com.dataexpo.facedataexpo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.FileUtils;
import com.dataexpo.facedataexpo.Utils.Utils;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.model.User;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_name;
    private TextView tv_ctime;
    private ImageView iv_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        initData();
    }

    private void initData() {
        User u = (User) getIntent().getSerializableExtra(Utils.EXTRA_EXPO_USRE);
        if (u != null) {
            tv_name.setText(u.getUserName());
            tv_ctime.setText(Utils.formatTime(u.getCtime(), "yyyy.MM.dd HH:mm:ss"));
            Bitmap bitmap = BitmapFactory.decodeFile(FileUtils.getBatchImportSuccessDirectory()
                    + "/" + u.getImageName());
            iv_user_image.setImageBitmap(bitmap);
        }
    }

    private void initView() {
        tv_name = findViewById(R.id.tv_user_info_name_value);
        tv_ctime = findViewById(R.id.tv_user_info_ctime_value);
        iv_user_image = findViewById(R.id.iv_user_info_image);
        findViewById(R.id.btn_user_info_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_info_back:
                finish();
                break;
            default:
        }
    }
}
