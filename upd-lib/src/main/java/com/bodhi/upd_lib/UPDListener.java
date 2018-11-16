package com.bodhi.upd_lib;

/**
 * @author : Sun
 * @version : 1.0
 * create time : 2018/11/16 10:24
 * desc :
 */
public interface UPDListener {
    void onStart();

    void onPause();

    void onProgress(long current, long total);

    void onComplete(long total, String savePath);

    void onFail();

}
