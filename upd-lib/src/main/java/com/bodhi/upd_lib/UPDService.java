package com.bodhi.upd_lib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author : Sun
 * @version : 1.0
 * create time : 2018/11/16 11:04
 * desc :
 */
public class UPDService extends Service {

    static final int SIZE_BUFFER = 1024 * 4;

    private DownloadTask downloadTask;

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null&&intent.hasExtra(UPDCore.INTENT_UPD_DOWNLOAD)){
            String title = intent.getStringExtra(UPDCore.INTENT_UPD_DOWNLOAD);
            UPDInfo info = UPDCore.getInstance().getInfo(title);
            if (info==null) {
                Toast.makeText(this, "任务信息错误", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    downloadTask =new DownloadTask(info);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(downloadTask==null){
                    Toast.makeText(this, "创建任务失败", Toast.LENGTH_SHORT).show();
                }else{
                    info.onStart();
                    getScheduledExecutor().submit(downloadTask);
                }
            }

        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ScheduledExecutorService getScheduledExecutor(){
        if (scheduledExecutorService==null) {
            scheduledExecutorService = Executors.newScheduledThreadPool(10);
        }

        return scheduledExecutorService;
    }

    private class DownloadTask implements Runnable{
        private UPDInfo downloadInfo;
        private UPDFileWriter fileWriter;
        private long currentOffset;
        private long totalSize;
        private boolean paused = false;

        public DownloadTask(UPDInfo info) throws IOException {
            downloadInfo = info;
            currentOffset=0;
            fileWriter = UPDFileWriter.onBuild(downloadInfo.getSavePath());
        }

        @Override
        public void run() {
            HttpURLConnection connection =null;
            URL url = null;
            InputStream is = null;
            boolean isCompleted = false;

            try {
                url = new URL(downloadInfo.getFileUrl());
                connection = (HttpURLConnection)url.openConnection();

                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();


                if(responseCode==HTTP_OK){

                    downloadInfo.onStart();
                    totalSize= connection.getContentLength();
                    is=connection.getInputStream();

                    byte[] buff=new byte[SIZE_BUFFER];
                    int length;
                    long lastT = System.currentTimeMillis();

                    while ((length=is.read(buff))!=-1) {
                        if (paused) {
                            downloadInfo.onPause();
                            return;
                        }

                        fileWriter.write(buff,0,length);
                        currentOffset+=length;

                        long nowT = System.currentTimeMillis();

                        int step = (int) (nowT-lastT);

                        if(step>100){
                            lastT = nowT;
                            downloadInfo.onProgress(currentOffset,totalSize);
                        }
                    }

                    downloadInfo.onComplete(totalSize);

                    isCompleted = true;
                }else{
                    downloadInfo.onFail();
                }

            } catch (MalformedURLException e) {
                downloadInfo.onFail();
                e.printStackTrace();
            } catch (IOException e) {
                downloadInfo.onFail();
                e.printStackTrace();
            }finally {
                if (is!=null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (connection!=null) {
                    connection.disconnect();
                }

                try {
                    fileWriter.flushAndSync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (isCompleted) {
                    downloadInfo.doAuto();
                }
            }
        }
    }
}
