package com.dataexpo.facedataexpo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.ToastUtils;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.listener.OnImportListener;
import com.dataexpo.facedataexpo.manager.ImportFileManager;
import com.dataexpo.facedataexpo.view.ImportDialog;

public class FaceRegistActivity extends BaseActivity implements View.OnClickListener, OnImportListener, ImportDialog.OnDialogClickListener {
    private static final String TAG = FaceRegistActivity.class.getSimpleName();
    private Context mContext;
    private ImportDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_regist);
        mContext = this;
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_regist_by_photo).setOnClickListener(this);
        findViewById(R.id.btn_regist_by_gallery).setOnClickListener(this);
        findViewById(R.id.btn_regist_by_depository).setOnClickListener(this);

        mDialog = new ImportDialog(mContext);
        mDialog.setDialogClickListener(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist_by_photo:
                startActivity(new Intent(this, PhotoRegistActivity.class));
                break;

            case R.id.btn_regist_by_gallery:
                startActivity(new Intent(this, GallerySelectRegistActivity.class));
                break;

            case R.id.btn_regist_by_depository:
                Log.i(TAG, "btn_regist_by_depository");
                mDialog.show();
                mDialog.tv_status.setText("搜索中，请稍后----  ------  ---");
                ImportFileManager.getInstance().setOnImportListener(this);
                ImportFileManager.getInstance().batchImport();
                break;
            default:
        }
    }

    @Override
    public void startUnzip() {

    }

    @Override
    public void showProgressView() {

    }

    @Override
    public void onImporting(int finishCount, int successCount, int failureCount, float progress) {

    }

    @Override
    public void onImporting(final int finishCount, final int successCount, final int failureCount, final int total) {
        Log.i(TAG, "onImporting!!!!");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDialog.tv_status.setText(String.format(getResources().getString(R.string.import_status), total - successCount - failureCount, successCount, failureCount));
                mDialog.tv_status.invalidate();
            }
        });
    }

    @Override
    public void endImport(int finishCount, int successCount, int failureCount) {

    }

    @Override
    public void showToastMessage(String message) {
        mDialog.tv_status.setText(message);
        mDialog.tv_status.invalidate();
    }

    @Override
    public void onConfirmClick(View view) {
        mDialog.dismiss();
    }

    @Override
    public void onModifierClick(View view) {
        mDialog.dismiss();
    }
}
