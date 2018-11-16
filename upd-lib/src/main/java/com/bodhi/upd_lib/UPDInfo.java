package com.bodhi.upd_lib;

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
    private int status=UPDCore.INFO_STATUS_READY;


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
    }

    public void onPause() {
        status=UPDCore.INFO_STATUS_PAUSE;
        if (downloadListener != null) {
            downloadListener.onPause();
        }
    }

    public void onProgress(long current, long total) {
        if (downloadListener != null) {
            downloadListener.onProgress(current, total);
        }
    }

    public void onFail() {
        status=UPDCore.INFO_STATUS_FAILURE;
        if (downloadListener != null) {
            downloadListener.onFail();
        }

        UPDCore.getInstance().downloadComplete();
    }

    public void onComplete(long total) {
        status=UPDCore.INFO_STATUS_COMPLETE;
        if (downloadListener != null) {
            downloadListener.onComplete(total, savePath);
        }

        UPDCore.getInstance().downloadComplete();
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
