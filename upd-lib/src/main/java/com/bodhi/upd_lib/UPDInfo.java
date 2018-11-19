package com.bodhi.upd_lib;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/**
 * @author : Sun
 * @version : 1.0
 * create time : 2018/11/16 10:29
 * desc :
 */
public class UPDInfo {
    private String title;
    private String fileUrl;
    private String savePath;
    private UPDListener downloadListener;
    private boolean autoOpen = false;
    private boolean notificationProgress = false;
    private int status=UPDCore.INFO_STATUS_READY;
    private NotificationManager notificationManager;
    private Notification.Builder notificationBuilder;
    private Context context;


    public static UPDInfo onCreate(String title, String fileUrl) {
        UPDInfo info = new UPDInfo();
        info.title = title;
        info.fileUrl = fileUrl;
        info.savePath = UPDCore.getInstance().generateSavePath(fileUrl);
        return info;
    }

    public UPDInfo auto(boolean auto) {
        autoOpen = auto;
        return this;
    }

    public UPDInfo notificationProgress(Context context,boolean notificationProgress){
        this.notificationProgress=notificationProgress;

        if (notificationProgress) {
            initNotificationProgress(context);
        }
        return this;
    }

    private void initNotificationProgress(Context context) {
        this.context=context;
        notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(AppUtils.getAppName(context))
                .setContentText("准备下载");

    }

    public UPDInfo listener(UPDListener listener) {
        downloadListener = listener;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSavePath() {
        return savePath;
    }

    public void onReady() {
        status=UPDCore.INFO_STATUS_READY;
    }

    public void onStart() {
        status=UPDCore.INFO_STATUS_DOWNLOADING;
        if (downloadListener != null) {
            downloadListener.onStart();
        }

        if(context==null){
            return;
        }
        if(notificationManager==null){
            return;
        }
        if(notificationBuilder==null){
            return;
        }
        if(context instanceof Activity){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notificationManager.notify(1, notificationBuilder.build());
                }
            });
        }
    }

    public void onPause() {
        status=UPDCore.INFO_STATUS_PAUSE;
        if (downloadListener != null) {
            downloadListener.onPause();
        }
    }

    public void onProgress(final long current, final long total) {
        if (downloadListener != null) {
            downloadListener.onProgress(current, total);
        }

        if(context==null){
            return;
        }
        if(notificationManager==null){
            return;
        }
        if(notificationBuilder==null){
            return;
        }
        if(context instanceof Activity){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notificationBuilder.setContentTitle(AppUtils.getAppName(context)+"-正在下载更新包");
                    notificationBuilder.setContentText((int)(current*100/total)+"%");
                    notificationManager.notify(1, notificationBuilder.build());
                }
            });
        }
    }

    public void onFail() {
        status=UPDCore.INFO_STATUS_FAILURE;
        if (downloadListener != null) {
            downloadListener.onFail();
        }

        UPDCore.getInstance().downloadComplete();

        if(context==null){
            return;
        }
        if(notificationManager==null){
            return;
        }
        if(notificationBuilder==null){
            return;
        }
        if(context instanceof Activity){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notificationBuilder.setContentTitle(AppUtils.getAppName(context));
                    notificationBuilder.setContentText("下载超时，请重新下载");
                    Notification notification = notificationBuilder.build();
                    notification.flags=Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(1, notification);
                }
            });
        }
    }

    public void onComplete(long total) {
        status=UPDCore.INFO_STATUS_COMPLETE;
        if (downloadListener != null) {
            downloadListener.onComplete(total, savePath);
        }

        UPDCore.getInstance().downloadComplete();

        if(context==null){
            return;
        }
        if(notificationManager==null){
            return;
        }
        if(notificationBuilder==null){
            return;
        }
        if(context instanceof Activity){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notificationBuilder.setContentTitle(AppUtils.getAppName(context));
                    notificationBuilder.setContentText("下载完成");
                    notificationManager.notify(1, notificationBuilder.build());
                    notificationManager.cancel(1);
                }
            });
        }
    }

    public void doAuto() {
        if (autoOpen) {
            try {
                UPDCore.getInstance().doOpen(savePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getStatus(){
        return status;
    }

}
