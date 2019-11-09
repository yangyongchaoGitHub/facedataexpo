package com.dataexpo.facedataexpo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataexpo.facedataexpo.R;
import com.dataexpo.facedataexpo.Utils.FileUtils;
import com.dataexpo.facedataexpo.Utils.LogUtils;
import com.dataexpo.facedataexpo.Utils.Utils;
import com.dataexpo.facedataexpo.activity.set.BaseActivity;
import com.dataexpo.facedataexpo.api.FaceApi;
import com.dataexpo.facedataexpo.listener.OnItemClickListener;
import com.dataexpo.facedataexpo.model.ImageSrc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GallerySelectRegistActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener {
    private static final String TAG = GallerySelectRegistActivity.class.getSimpleName();
    private RecyclerView recycler;
    private Context mContext;
    private ImageAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_select_regist);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_back_gallery_select_regist).setOnClickListener(this);
        recycler = findViewById(R.id.recycler_gallery_images);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        recycler.setLayoutManager(layoutManager);

        mImageAdapter = new ImageAdapter();
        mImageAdapter.setHasStableIds(true);
        recycler.setAdapter(mImageAdapter);
        mImageAdapter.setItemClickListener(this);
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
        List<ImageSrc> images = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ImageSrc is = new ImageSrc();
            is.setUri(pics.get(i));
            images.add(is);
        }
        mImageAdapter.setDataList(images);
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_gallery_select_regist:
                finish();
                break;
            default:
        }
    }

    private static class ImageHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private ImageView img;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            img = itemView.findViewById(R.id.img_);
        }
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> implements View.OnClickListener {
        private OnItemClickListener mItemClickListener;
        private List<ImageSrc> images;

        private void setDataList(List<ImageSrc> images) {
            this.images = images;
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
        public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
            holder.itemView.setTag(position);
            // 添加数据
            //holder.img.setText(images.get(position));
            LogUtils.i(TAG, "inbinHolder " + images.get(position).getImageSrcId());
            //holder.img.setImageResource(images.get(position).getImageSrcId());
            holder.img.setImageURI(Uri.fromFile(new File(images.get(position).getUri())));
            //ImageView.setImageURI(Uri.fromFile(new File("/sdcard/test.jpg")));
        }

        @Override
        public int getItemCount() {
            return images != null ? images.size() : 0;
        }

        private void setItemClickListener(OnItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
