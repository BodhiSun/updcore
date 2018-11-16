package com.bodhi.updateservicecore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bodhi.upd_lib.UPDCore;
import com.bodhi.upd_lib.UPDListener;

public class MainActivity extends AppCompatActivity {
    public static String url="http://down.jser123.com/app-debug-v3.0.1_301_2_yyb_sign.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UPDCore.getInstance().init(this);
    }

    public void clickTx(View view) {
        UPDCore.getInstance().startDownload("testApp", url, true, new UPDListener() {
            @Override
            public void onStart() {
                Log.e("test","onStart");
            }

            @Override
            public void onPause() {
                Log.e("test","onPause");
            }

            @Override
            public void onProgress(long current, long total) {
                Log.e("test","onProgress    total:"+total+"     current:"+current);
            }

            @Override
            public void onComplete(long total, String savePath) {
                Log.e("test","onComplete    savePath:"+savePath);
            }

            @Override
            public void onFail() {
                Log.e("test","onFail");
            }
        });
    }
}
