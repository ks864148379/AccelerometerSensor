package ibeacondata.service;

import android.content.Context;
import android.content.Intent;

/**
 * Created by LK on 2016/10/6.
 */
public class UploadManager {
    private static volatile UploadManager instance = null;
    private Context context = null;
    private boolean isStarted = false;

    private UploadManager(Context context){
        this.context = context;
    }

    public static UploadManager getInstance(Context context){
        if (instance ==null){
            synchronized (UploadManager.class){
                if (instance == null){
                    instance = new UploadManager(context);
                }
            }
        }
        return instance;
    }

    public void startUpload(){
        Intent intent = new Intent(context, UploadService.class);
        context.startService(intent);
        isStarted = true;
    }

    public void stopUpload(){
        Intent intent = new Intent(context,UploadService.class);
        context.stopService(intent);
        isStarted = false;
    }

    public boolean isStarted(){
        return isStarted;
    }
}
