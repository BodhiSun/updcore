# updcore
version update with the progress notification on top（版本更新库）

#### Usage
Step1:
```java
UPDCore.getInstance().init(context);
```
Step2:
```java
UPDCore.getInstance().startDownload(title,url,isAutoInstall,isNotificationProgress, new UPDListener() {
    @Override
    public void onStart() {
        Log.e("aaa","onStart");
    }

    @Override
    public void onPause() {
        Log.e("aaa","onPause");
    }

    @Override
    public void onProgress(long current, long total) {
        Log.e("aaa","onProgress    total:"+total+"     current:"+current);
    }

    @Override
    public void onComplete(long total, String savePath) {
        Log.e("aaa","onComplete    savePath:"+savePath);
    }

    @Override
    public void onFail() {
        Log.e("aaa","onFail");
    }
});
```
