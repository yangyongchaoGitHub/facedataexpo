package com.dataexpo.facedataexpo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.dataexpo.facedataexpo.callback.FaceDetectCallBack;
import com.dataexpo.facedataexpo.listener.OnImportListener;
import com.dataexpo.facedataexpo.listener.OnItemClickListener;
import com.dataexpo.facedataexpo.listener.OnItemLongClickListener;
import com.dataexpo.facedataexpo.manager.FaceTrackManager;
import com.dataexpo.facedataexpo.manager.ImportFileManager;
import com.dataexpo.facedataexpo.model.ImageSrc;
import com.dataexpo.facedataexpo.model.LivenessModel;
import com.dataexpo.facedataexpo.model.SingleBaseConfig;
import com.dataexpo.facedataexpo.view.ImportDialog;
import com.dataexpo.facedataexpo.view.LoginDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GallerySelectRegistActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener, OnItemLongClickListener, ImportDialog.OnDialogClickListener {
    private static final String TAG = GallerySelectRegistActivity.class.getSimpleName();
    private RecyclerView recycler;
    private Context mContext;
    private ImageAdapter mImageAdapter;
    private boolean isShowCheck = true;
    private List<ImageSrc> mShowList;
    private ImportDialog mDialog;
    //网格列数
    private final static int TABLE_ROWS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_select_regist);
        mContext = this;
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_back_gallery_select_regist).setOnClickListener(this);
        findViewById(R.id.btn_add_gallery_select_regist).setOnClickListener(this);
        recycler = findViewById(R.id.recycler_gallery_images);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, TABLE_ROWS);
        recycler.setLayoutManager(layoutManager);

        mImageAdapter = new ImageAdapter();
        mImageAdapter.setShowCheckBox(isShowCheck);
        mImageAdapter.setHasStableIds(true);
        recycler.setAdapter(mImageAdapter);
        mImageAdapter.setItemClickListener(this);
        mImageAdapter.setOnItemLongClickListener(this);

        ImportFileManager.getInstance().setOnImportListener(new OnImportListener() {
            @Override
            public void startUnzip() {
            }

            @Override
            public void showProgressView() {
            }

            @Override
            public void onImporting(final int finishCount, final int successCount, final int failureCount, float progress) {
                Log.i(TAG, "onImporting!!!!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.tv_status.setText(String.format(getResources().getString(R.string.import_status), finishCount, successCount, failureCount));
                        mDialog.tv_status.invalidate();
                    }
                });
            }

            @Override
            public void endImport(int finishCount, int successCount, int failureCount) {
            }

            @Override
            public void showToastMessage(String message) {
                ToastUtils.toast(mContext, message);
            }
        });

        mDialog = new ImportDialog(mContext);
        mDialog.setDialogClickListener(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        List<String> pics;
        pics = FaceApi.getInstance().getAllPics(this);
        int size = pics.size();
        mShowList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ImageSrc is = new ImageSrc();
            is.setUri(pics.get(i));
            mShowList.add(is);
        }
        mImageAdapter.setDataList(mShowList);
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        LogUtils.i(TAG, "item click: " + position);

        if (position < mShowList.size()) {
            mShowList.get(position).setCheck(!mShowList.get(position).isCheck());
        }
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_gallery_select_regist:
                finish();
                break;
            case R.id.btn_add_gallery_select_regist:
                //将选择的图片导入到人脸库
                mDialog.show();
                faceDetect();
                break;
            default:
        }
    }


    private void faceDetect() {
        List<String> uris = new ArrayList<>();
        for (int i = 0; i < mShowList.size(); i++) {
            if (mShowList.get(i).isCheck()) {
                uris.add(mShowList.get(i).getUri());
            }
        }
        ImportFileManager.getInstance().asyncImportByGallery(uris);

    }

    @Override
    public void onLongItemClick(View view, int position) {
//        isShowCheck = !isShowCheck;
//        mImageAdapter.setShowCheckBox(isShowCheck);
//        LogUtils.i(TAG, "isShowCheck:: " + isShowCheck);
//        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConfirmClick(View view) {
        mDialog.dismiss();
    }

    @Override
    public void onModifierClick(View view) {
        mDialog.dismiss();
    }

    private static class ImageHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private ImageView img;
        private CheckBox check_btn;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            img = itemView.findViewById(R.id.img_);
            check_btn = itemView.findViewById(R.id.check_btn);
        }
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> implements View.OnClickListener {
        private OnItemClickListener mItemClickListener;
        private OnItemLongClickListener mItemLongClickListener;
        private List<ImageSrc> images;
        private boolean mShowCheckBox;

        private void setDataList(List<ImageSrc> images) {
            this.images = images;
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

        @NonNull
        @Override
        public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_gallery_show, parent, false);
            ImageHolder viewHolder = new ImageHolder(view);
            view.setOnClickListener(this);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ImageHolder holder, final int position) {
            holder.itemView.setTag(position);
            if (mShowCheckBox) {
                holder.check_btn.setVisibility(View.VISIBLE);
                if (images.get(position).isCheck()) {
                    holder.check_btn.setChecked(true);
                } else {
                    holder.check_btn.setChecked(false);
                }
            } else {
                holder.check_btn.setVisibility(View.GONE);
            }
            // 添加数据
            //holder.img.setText(images.get(position));
            //holder.img.setImageResource(images.get(position).getImageSrcId());
            holder.img.setImageURI(Uri.fromFile(new File(images.get(position).getUri())));
            //ImageView.setImageURI(Uri.fromFile(new File("/sdcard/test.jpg")));
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
            return images != null ? images.size() : 0;
        }

        private void setItemClickListener(OnItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        private void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
            mItemLongClickListener = itemLongClickListener;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
