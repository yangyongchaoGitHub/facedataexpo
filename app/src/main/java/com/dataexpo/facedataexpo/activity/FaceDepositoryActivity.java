package com.dataexpo.facedataexpo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.FileUtils;
import com.dataexpo.facedataexpo.Utils.LogUtils;
import com.dataexpo.facedataexpo.Utils.ToastUtils;
import com.dataexpo.facedataexpo.Utils.Utils;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.api.FaceApi;
import com.dataexpo.facedataexpo.listener.OnDialogClickListener;
import com.dataexpo.facedataexpo.listener.OnImportListener;
import com.dataexpo.facedataexpo.listener.OnItemClickListener;
import com.dataexpo.facedataexpo.listener.OnItemLongClickListener;
import com.dataexpo.facedataexpo.manager.ImportFileManager;
import com.dataexpo.facedataexpo.manager.UserInfoManager;
import com.dataexpo.facedataexpo.model.User;
import com.dataexpo.facedataexpo.view.CircleImageView;
import com.dataexpo.facedataexpo.Utils.Utils;
import com.dataexpo.facedataexpo.view.ImportDialog;
import com.dataexpo.facedataexpo.view.LoginDialog;
import com.dataexpo.facedataexpo.view.RegistSelectDialog;

import java.util.List;

import static com.dataexpo.facedataexpo.manager.ImportFileManager.GROUP_DEFAULT;
import static com.dataexpo.facedataexpo.view.RegistSelectDialog.REGIST_BY_DEPOSITORY;
import static com.dataexpo.facedataexpo.view.RegistSelectDialog.REGIST_BY_GALLERY;
import static com.dataexpo.facedataexpo.view.RegistSelectDialog.REGIST_BY_PHOTO;

public class FaceDepositoryActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener, OnItemLongClickListener, OnDialogClickListener, ImportDialog.OnDialogClickListener, OnImportListener {
    private static final String TAG = FaceDepositoryActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private Context mContext;
    private FaceUserAdapter mFaceUserAdapter;
    private UserListListener mUserListListener;
    private List<User> mListUserInfo;
    private TextView mTextGroupName;
    private boolean isShowCheck = false;
    private TextView btn_cancel;
    private TextView btn_delete;
    private RegistSelectDialog mDialog;
    private ImportDialog mDialog_import;
    private ButtonState mButtonState = ButtonState.BATCH_OPERATION;             // 当前按钮状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_depository);
        mContext = this;

        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                //进行注册方式选择
                //startActivity(new Intent(this, FaceRegistActivity.class));
                mDialog.show();
                break;

            case R.id.btn_face_depository_cancel:
                for (int i = 0; i < mListUserInfo.size(); i++) {
                    mListUserInfo.get(i).setChecked(false);
                }
                isShowCheck = false;
                mFaceUserAdapter.setShowCheckBox(isShowCheck);
                btn_cancel.setVisibility(View.INVISIBLE);
                btn_delete.setVisibility(View.INVISIBLE);
                mFaceUserAdapter.notifyDataSetChanged();
                break;

            case R.id.btn_face_depository_delete:
                UserInfoManager.getInstance().deleteUserListInfo(mListUserInfo, mUserListListener, 0);
                //UserInfoManager.getInstance().deleteUserInfo(mUserId, GROUP_DEFAULT, mListener);
                break;

            case R.id.btn_face_depository_back:
                finish();
                break;
                default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserInfoManager.getInstance().release();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (isShowCheck) {
            LogUtils.i(TAG, "item click: " + position);

            if (position < mListUserInfo.size()) {
                mListUserInfo.get(position).setChecked(!mListUserInfo.get(position).isChecked());
            }
            mFaceUserAdapter.notifyDataSetChanged();
        } else {
            Intent intent  = new Intent(this, UserInfoActivity.class);
            User u = mListUserInfo.get(position);
            intent.putExtra(Utils.EXTRA_EXPO_USRE, u);
            startActivity(intent);
        }
    }

    @Override
    public void onLongItemClick(View view, int position) {
        isShowCheck = !isShowCheck;
        mFaceUserAdapter.setShowCheckBox(isShowCheck);
        LogUtils.i(TAG, "isShowCheck:: " + isShowCheck);
        mFaceUserAdapter.notifyDataSetChanged();
        btn_cancel.setVisibility(View.VISIBLE);
        btn_delete.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDialogItemClick(View view) {
    }

    @Override
    public void onDialogItemClick(int i) {
        Log.i(TAG, "onDialogItemClick: " + i);
        if (REGIST_BY_PHOTO == i) {
            startActivity(new Intent(this, PhotoRegistActivity.class));
        } else if (REGIST_BY_GALLERY == i) {
            startActivity(new Intent(this, GallerySelectRegistActivity.class));
        } else if (REGIST_BY_DEPOSITORY == i) {
            mDialog_import.show();
            mDialog_import.tv_status.setText("搜索中，请稍后----  ------  ---");
            ImportFileManager.getInstance().setOnImportListener(this);
            ImportFileManager.getInstance().batchImport();
        }
        mDialog.dismiss();
    }

    @Override
    public void onConfirmClick(View view) {
        mDialog_import.dismiss();
    }

    @Override
    public void onModifierClick(View view) {
        mDialog_import.dismiss();
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
                mDialog_import.tv_status.setText(String.format(
                                getResources().getString(R.string.import_status),
                                total - successCount - failureCount,
                                successCount,
                                failureCount));
                mDialog_import.tv_status.invalidate();
            }
        });
    }

    @Override
    public void endImport(int finishCount, int successCount, int failureCount) {

    }

    @Override
    public void showToastMessage(String message) {
        mDialog_import.tv_status.setText(message);
        mDialog_import.tv_status.invalidate();
    }

    private enum ButtonState {
        BATCH_OPERATION,
        ALL_SELECT
    }

    private void initData() {
        mUserListListener = new UserListListener();
        // 读取数据库，获取用户信息
        UserInfoManager.getInstance().getUserListInfoByGroupId(null, GROUP_DEFAULT,
                mUserListListener);
    }

    private void initView() {
        recyclerView = findViewById(R.id.user_info_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(layoutManager);

        mFaceUserAdapter = new FaceUserAdapter();
        recyclerView.setAdapter(mFaceUserAdapter);
        mFaceUserAdapter.setItemClickListener(this);
        mFaceUserAdapter.setOnItemLongClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_face_depository_back).setOnClickListener(this);
        btn_cancel = findViewById(R.id.btn_face_depository_cancel);
        btn_delete = findViewById(R.id.btn_face_depository_delete);
        btn_delete.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        mDialog = new RegistSelectDialog(this);
        mDialog.setDialogClickListener(this);
        mDialog.setCanceledOnTouchOutside(true);
        //mDialog.setCancelable(false);

        mDialog_import = new ImportDialog(mContext);
        mDialog_import.setDialogClickListener(this);
        mDialog_import.setCanceledOnTouchOutside(false);
        mDialog_import.setCancelable(false);
    }

    private void updateDeleteUI(boolean needDelete) {
        if (needDelete) {
            mButtonState = ButtonState.ALL_SELECT;
            // 右上角按钮改为“全选”
            //mBtnBatchOperation.setText("全选");
            // 列表显示复选框
            mFaceUserAdapter.setShowCheckBox(true);
            mFaceUserAdapter.notifyDataSetChanged();
            // 显示删除布局
            //mLinearOperation.setVisibility(View.VISIBLE);
        } else {
            mButtonState = ButtonState.BATCH_OPERATION;
            // 右上角按钮改为“批量操作”
            //mBtnBatchOperation.setText("批量操作");
            // 列表隐藏复选框
            mFaceUserAdapter.setShowCheckBox(false);
            mFaceUserAdapter.notifyDataSetChanged();
            // 隐藏删除布局
            //mLinearOperation.setVisibility(View.GONE);
        }
    }

    // 用于返回读取用户的结果
    private class UserListListener extends UserInfoManager.UserInfoListener {
        // 读取用户列表成功
        @Override
        public void userListQuerySuccess(final List<User> listUserInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (listUserInfo == null || listUserInfo.size() == 0) {
                        ToastUtils.toast(mContext, "暂未搜索到用户");
                        return;
                    } else {
                        mListUserInfo = listUserInfo;
                    }

                    ToastUtils.toast(mContext, "用户数：" + listUserInfo.size());
                    mFaceUserAdapter.setDataList(listUserInfo);

                    if (mButtonState == ButtonState.ALL_SELECT) {
                        updateDeleteUI(false);
                        ToastUtils.toast(mContext, "删除成功");
                    } else {

                        mFaceUserAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        // 读取用户列表失败
        @Override
        public void userListQueryFailure(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mContext == null) {
                        return;
                    }
                    ToastUtils.toast(mContext, message);
                }
            });
        }

        // 删除用户列表成功
        @Override
        public void userListDeleteSuccess() {
            // 数据变化，更新内存
            FaceApi.getInstance().initDatabases(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UserInfoManager.getInstance().getUserListInfoByGroupId(null, GROUP_DEFAULT, mUserListListener);
                }
            });
        }

        // 删除用户列表失败
        @Override
        public void userListDeleteFailure(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mContext == null) {
                        return;
                    }
                    ToastUtils.toast(mContext, message);
                }
            });
        }
    }

    private static class FaceUserHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView text_name;
        private TextView text_info;
        private TextView text_ctime;
        private CircleImageView image;
        private CheckBox check_btn;

        public FaceUserHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            image = itemView.findViewById(R.id.user_info_image);
            text_name = itemView.findViewById(R.id.text_user_name);
            text_ctime = itemView.findViewById(R.id.text_user_ctime);
            text_info = itemView.findViewById(R.id.text_user_info);
            check_btn = itemView.findViewById(R.id.check_btn);
        }
    }

    public class FaceUserAdapter extends RecyclerView.Adapter<FaceUserHolder> implements View.OnClickListener {
        private List<User> mList;
        private boolean mShowCheckBox;
        private OnItemClickListener mItemClickListener;
        private OnItemLongClickListener mItemLongClickListener;

        private void setDataList(List<User> list) {
            mList = list;
        }

        private void setShowCheckBox(boolean showCheckBox) {
            mShowCheckBox = showCheckBox;
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, (Integer) v.getTag());
            }
        }

        private void setItemClickListener(OnItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        private void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
            mItemLongClickListener = itemLongClickListener;
        }

        @NonNull
        @Override
        public FaceUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_info_list, parent, false);
            FaceUserHolder viewHolder = new FaceUserHolder(view);
            view.setOnClickListener(this);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull FaceUserHolder holder, final int position) {
            holder.itemView.setTag(position);
            if (mShowCheckBox) {
                holder.check_btn.setVisibility(View.VISIBLE);
                if (mList.get(position).isChecked()) {
                    holder.check_btn.setChecked(true);
                } else {
                    holder.check_btn.setChecked(false);
                }
            } else {
                holder.check_btn.setVisibility(View.GONE);
            }
            // 添加数据
            holder.text_name.setText(mList.get(position).getUserName());
            String ctime = Utils.formatTime(mList.get(position).getCtime(), "yyyy.MM.dd HH:mm:ss");
            String userInfo = mList.get(position).getUserInfo();
            if (!TextUtils.isEmpty(userInfo)) {
                holder.text_info.setText(userInfo);
            } else {
                holder.text_info.setText("");
            }
            holder.text_ctime.setText("创建时间：" + ctime);
            Bitmap bitmap = BitmapFactory.decodeFile(FileUtils.getBatchImportSuccessDirectory()
                    + "/" + mList.get(position).getImageName());
            holder.image.setImageBitmap(bitmap);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if  (mItemLongClickListener != null) {
                        mItemLongClickListener.onLongItemClick(v, position);
                        return true;
                    }

                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }
    }
}
