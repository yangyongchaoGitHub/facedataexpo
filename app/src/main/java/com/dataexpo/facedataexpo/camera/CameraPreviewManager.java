package com.dataexpo.facedataexpo.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import com.dataexpo.facedataexpo.callback.CameraDataCallback;
import com.dataexpo.facedataexpo.model.SingleBaseConfig;

import java.io.IOException;
import java.util.List;

public class CameraPreviewManager extends BaseCameraManager implements TextureView.SurfaceTextureListener {
    private static final String TAG = CameraPreviewManager.class.getSimpleName();

    boolean mPreviewed = false;

    /**
     * 垂直方向
     */
    public static final int ORIENTATION_PORTRAIT = 0;
    /**
     * 水平方向
     */
    public static final int ORIENTATION_HORIZONTAL = 1;

    private int tempWidth;
    private int tempHeight;



    private int mCameraNum;

    private int mirror = 1; // 镜像处理

    private static volatile CameraPreviewManager instance = null;

    public static CameraPreviewManager getInstance() {
        synchronized (CameraPreviewManager.class) {
            if (instance == null) {
                instance = new CameraPreviewManager();
            }
        }
        return instance;
    }

    public int getCameraFacing() {
        return cameraFacing;
    }

    public int getDisplayOrientation() {
        return displayOrientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        this.displayOrientation = displayOrientation;
    }

    @Override
    public void startPreview(Context context, AutoTexturePreviewView textureView, int width, int height, CameraDataCallback cameraDataCallback) {
        super.startPreview(context, textureView, width, height, cameraDataCallback);
        mTextureView.getTextureView().setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int i, int i1) {
        Log.e(TAG, "--surfaceTexture--SurfaceTextureAvailable");
        mSurfaceTexture = texture;
        mSurfaceCreated = true;
        textureWidth = i;
        textureHeight = i1;
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int i, int i1) {
        Log.e(TAG, "--surfaceTexture--TextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
        Log.e(TAG, "--surfaceTexture--destroyed");
        mSurfaceCreated = false;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        // Log.e(TAG, "--surfaceTexture--Updated");
    }
}
