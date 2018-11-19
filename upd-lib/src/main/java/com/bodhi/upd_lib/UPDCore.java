package com.bodhi.upd_lib;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

/**
 * @author : Sun
 * @version : 1.0
 * create time : 2018/11/16 10:08
 * desc :
 */
public class UPDCore {

    public static final String INTENT_UPD_DOWNLOAD = "upd_download";
    public static final int INFO_STATUS_READY = 0;
    public static final int INFO_STATUS_DOWNLOADING = 1;
    public static final int INFO_STATUS_PAUSE = 2;
    public static final int INFO_STATUS_COMPLETE = 3;
    public static final int INFO_STATUS_FAILURE = 4;
    private Context appContext;
    private static UPDCore UPDCore;

    private UPDCore() {
    }

    public static UPDCore getInstance() {
        if (UPDCore == null) {
            UPDCore = new UPDCore();
        }
        return UPDCore;
    }

    private UPDInfo currentDownloadInfo;

    public void init(Context context) {
        appContext = context;
    }

    public void startDownload(String title, String url, boolean auto,boolean notificationProgress, UPDListener listener) {
        if (currentDownloadInfo != null) {
            int status = currentDownloadInfo.getStatus();
            if (status == INFO_STATUS_DOWNLOADING) {
                Toast.makeText(appContext, "正在下载中", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        currentDownloadInfo = UPDInfo.onCreate(title, url).auto(auto).listener(listener).notificationProgress(appContext,notificationProgress);
        appContext.startService(new Intent(appContext, UPDService.class).putExtra(INTENT_UPD_DOWNLOAD, currentDownloadInfo.getTitle()));
    }

    public String generateSavePath(String downloadUrl) {
        final String cacheName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);

        final File cacheFile = appContext.getExternalFilesDir(null);
        String cachePath = "";
        if (null != cacheFile) {
            cachePath = cacheFile.getAbsolutePath();
        }

        if (cachePath.endsWith(File.separator)) {
            return cachePath + cacheName;
        } else {
            return cachePath + File.separator + cacheName;
        }
    }

    public void doOpen(String savedPath) {
        File targetFile = new File(savedPath);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");

        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(appContext, appContext.getPackageName() + ".fileprovider", targetFile);
            intent.setDataAndType(uri, appContext.getContentResolver().getType(uri));
        } else {
            intent.setDataAndType(Uri.fromFile(targetFile), getIntentType(targetFile));
        }

        try {
            appContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getIntentType(File targetFile) {
        String suffix = targetFile.getName();
        String name = suffix.substring(suffix.lastIndexOf(".") + 1, suffix.length()).toLowerCase();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(name);
    }

    public void downloadComplete() {
        currentDownloadInfo = null;
    }

    public UPDInfo getInfo(String title) {
        if (currentDownloadInfo == null) {
            Log.e("UPD", "download info is null");
        }

        if (currentDownloadInfo.getTitle().equals(title)) {
            return currentDownloadInfo;
        }

        return null;
    }

}
