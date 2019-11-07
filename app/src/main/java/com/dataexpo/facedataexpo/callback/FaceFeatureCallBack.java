package com.dataexpo.facedataexpo.callback;

/**
 * 人脸特征抽取回调接口。
 *
 */
public interface FaceFeatureCallBack {

    public void onFaceFeatureCallBack(float featureSize, byte[] feature);

}