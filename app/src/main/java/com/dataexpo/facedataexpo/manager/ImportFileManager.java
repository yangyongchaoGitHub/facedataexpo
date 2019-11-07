package com.dataexpo.facedataexpo.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.utils.ZipUtils;
import com.dataexpo.facedataexpo.Utils.FileUtils;
import com.dataexpo.facedataexpo.api.FaceApi;
import com.dataexpo.facedataexpo.listener.OnImportListener;
import com.dataexpo.facedataexpo.model.User;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 导入相关管理类
 */

public class ImportFileManager {
    private static final String TAG = "ImportFileManager";

    private Future mFuture;
    private ExecutorService mExecutorService;
    private OnImportListener mImportListener;
    // 是否需要导入
    private volatile boolean mIsNeedImport;

    private int mTotalCount;
    private int mFinishCount;
    private int mSuccessCount;
    private int mFailCount;

    private static class HolderClass {
        private static final ImportFileManager instance = new ImportFileManager();
    }

    public static ImportFileManager getInstance() {
        return ImportFileManager.HolderClass.instance;
    }

    // 私有构造，实例化ExecutorService
    private ImportFileManager() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
    }

    public void setOnImportListener(OnImportListener importListener) {
        mImportListener = importListener;
    }

    /**
     * 开始批量导入
     */
    public void batchImport() {
        // 获取导入目录 /sdcard/Face-Import
        File batchFaceDir = FileUtils.getBatchImportDirectory();
        // 遍历该目录下的所有文件
        String[] files = batchFaceDir.list();
        if (files == null || files.length == 0) {
            Log.i(TAG, "导入数据的文件夹没有数据");
            if (mImportListener != null) {
                mImportListener.showToastMessage("导入数据的文件夹没有数据");
            }
            return;
        }

        // 判断Face.zip是否存在
        File zipFile = FileUtils.isFileExist(batchFaceDir.getPath(), "Face.zip");
        if (zipFile == null) {
            Log.i(TAG, "导入数据的文件夹没有Face.zip");
            if (mImportListener != null) {
                mImportListener.showToastMessage("搜索失败，请检查操作步骤并重试");
            }
            return;
        }

        // 开启线程解压、导入
        asyncImport(batchFaceDir, zipFile);
    }

    /**
     * 开启线程解压、导入
     *
     * @param batchFaceDir 导入的目录
     * @param zipFile      压缩文件
     */
    private void asyncImport(final File batchFaceDir, final File zipFile) {
        mIsNeedImport = true;
        mFinishCount = 0;
        mSuccessCount = 0;
        mFailCount = 0;

        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }

        mFuture = mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mImportListener != null) {
                        mImportListener.startUnzip();
                    }

                    // 解压
                    boolean zipSuccess = ZipUtils.unZipFolder(zipFile.getAbsolutePath(), batchFaceDir.toString());
                    if (!zipSuccess) {
                        if (mImportListener != null) {
                            mImportListener.showToastMessage("解压失败");
                        }
                        return;
                    }

                    // 删除zip文件
                    FileUtils.deleteFile(zipFile.getPath());
                    Log.i(TAG, "解压成功");

                    // 解压成功之后再次遍历该目录是不是存在文件
                    File batchPicDir = FileUtils.getBatchImportDirectory();
                    File[] files = batchPicDir.listFiles();

                    // 如果该目录下没有文件，则提示获取图片失败
                    if (files == null) {
                        if (mImportListener != null) {
                            mImportListener.showToastMessage("获取图片失败");
                        }
                        return;
                    }

                    // 如果该目录下有文件，则判断该文件是目录还是文件
                    File[] picFiles;   // 定义图片文件数组

                    if (files[0].isDirectory()) {
                        picFiles = files[0].listFiles();
                    } else {
                        picFiles = files;
                    }

                    // 解压并读取图片成功，开始显示进度条
                    if (mImportListener != null) {
                        mImportListener.showProgressView();
                    }

                    mTotalCount = picFiles.length;

                    for (int i = 0; i < picFiles.length; i++) {
                        if (!mIsNeedImport) {
                            break;
                        }

                        // 获取图片名
                        String picName = picFiles[i].getName();
                        // 判断图片后缀
                        if (!picName.endsWith(".jpg") && !picName.endsWith(".png")) {
                            Log.i(TAG, "图片后缀不满足要求");
                            mFinishCount++;
                            mFailCount++;
                            // 更新进度
                            updateProgress(mFinishCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                            continue;
                        }

                        // 获取不带后缀的图片名
                        String picNameNoEx = FileUtils.getFileNameNoEx(picName);
                        // 通过既定的图片名格式，按照“-”分割，获取组名和用户名
                        String[] picNames = picNameNoEx.split("-");
                        // 如果分割失败，则该图片命名不满足要求
                        if (picNames.length != 2) {
                            //Log.e(TAG, "图片命名格式不符合要求");
                            if (mImportListener != null) {
                                mImportListener.showToastMessage("图片命名格式不符合要求");
                            }
                            mFinishCount++;
                            mFailCount++;
                            continue;
                        }
                        String groupName = picNames[1];   // 组名
                        String userName = picNames[0];    // 用户名

                        boolean success = false;

                        // 根据姓名查询数据库与文件中对应的姓名是否相等，如果相等，则直接过滤
                        List<User> listUsers = FaceApi.getInstance().getUserListByUserName(groupName, userName);
                        if (listUsers != null && listUsers.size() > 0) {
                            //Log.i(TAG, "与之前图片名称相同");
                            if (mImportListener != null) {
                                mImportListener.showToastMessage("与之前图片名称相同");
                            }

                            mFinishCount++;
                            mFailCount++;
                            // 更新进度
                            updateProgress(mFinishCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                            continue;
                        }

                        // 判断姓名是否有效
                        String nameResult = FaceApi.getInstance().isValidName(userName);
                        if (!"0".equals(nameResult)) {
                            Log.i(TAG, nameResult);
                            mFinishCount++;
                            mFailCount++;
                            // 更新进度
                            updateProgress(mFinishCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                            continue;
                        }

                        // 根据图片的路径将图片转成Bitmap
                        Bitmap bitmap = BitmapFactory.decodeFile(picFiles[i].getAbsolutePath());

                        if (bitmap != null) {
                            byte[] bytes = new byte[512];
                            float ret = -1;
                            // 走人脸SDK接口，通过人脸检测、特征提取拿到人脸特征值
                            ret = FaceApi.getInstance().getFeature(bitmap, bytes,
                                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO);

                            Log.i(TAG, "live_photo = " + ret);

                            if (ret == -1) {
                                //Log.e(TAG, picName + "未检测到人脸，可能原因：人脸太小");
                                if (mImportListener != null) {
                                    mImportListener.showToastMessage("未检测到人脸，可能原因：人脸太小 ");
                                }
                            } else if (ret == 128) {
                                // 将用户信息和用户组信息保存到数据库
                                boolean importDBSuccess = FaceApi.getInstance().registerUserIntoDBmanager(groupName,
                                        userName, picName, null, bytes);

                                // 保存数据库成功
                                if (importDBSuccess) {
                                    // 保存图片到新目录中
                                    File facePicDir = FileUtils.getBatchImportSuccessDirectory();
                                    if (facePicDir != null) {
                                        File savePicPath = new File(facePicDir, picName);
                                        if (FileUtils.saveBitmap(savePicPath, bitmap)) {
                                            Log.i(TAG, "图片保存成功");
                                            success = true;
                                        } else {
                                            if (mImportListener != null) {
                                                mImportListener.showToastMessage("图片保存失败 ");
                                            }
                                            //Log.i(TAG, "图片保存失败");
                                        }
                                    }
                                } else {
                                    //Log.e(TAG, picName + "：保存到数据库失败");
                                    if (mImportListener != null) {
                                        mImportListener.showToastMessage("保存到数据库失败 ");
                                    }
                                }
                            } else {
                                if (mImportListener != null) {
                                    mImportListener.showToastMessage("未检测到人脸 ");
                                }
                                //Log.e(TAG, picName + "：未检测到人脸");
                            }

                            // 图片回收
                            if (!bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                        } else {
                            if (mImportListener != null) {
                                mImportListener.showToastMessage("该图片转成Bitmap失败 ");
                            }
                            //Log.e(TAG, picName + "：该图片转成Bitmap失败");
                        }

                        // 判断成功与否
                        if (success) {
                            mSuccessCount++;
                        } else {
                            mFailCount++;
                            Log.e(TAG, "失败图片:" + picName);
                        }
                        mFinishCount++;
                        // 导入中（用来显示进度）
                        Log.i(TAG, "mFinishCount = " + mFinishCount
                                + " progress = " + ((float) mFinishCount / (float) mTotalCount));
                        if (mImportListener != null) {
                            mImportListener.onImporting(mFinishCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                        }
                    }

                    // 导入完成
                    if (mImportListener != null) {
                        mImportListener.showToastMessage("inject ok!!!! ");

                        mImportListener.endImport(mFinishCount, mSuccessCount, mFailCount);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "exception = " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateProgress(int finishCount, int successCount, int failureCount, float progress) {
        if (mImportListener != null) {
            mImportListener.onImporting(finishCount, successCount, failureCount, progress);
        }
    }

    /**
     * 释放功能，用于关闭线程操作
     */
    public void release() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }

        if (mExecutorService != null) {
            mExecutorService = null;
        }
    }
}
