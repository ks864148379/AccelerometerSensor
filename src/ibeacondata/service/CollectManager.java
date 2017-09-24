package ibeacondata.service;

import android.content.Context;
import android.content.Intent;
import ibeaconconn.base.IBeacon;

import java.util.List;

/**
 * Created by LK on 2016/10/6.
 */
public class CollectManager {
    private static CollectManager instance = null;
    private Context context;
    private boolean isStarted= false;

    private CollectManager(Context context){
        this.context = context;
    }

    public static CollectManager getInstance(Context context){
        if (instance == null){
            synchronized (CollectManager.class){
                if(instance == null){
                    instance = new CollectManager(context);
                }
            }
        }
        return instance;
    }

    public void startCollect(){
        Intent intent = new Intent(context,CollectService.class);
        context.startService(intent);
        isStarted = true;
    }

    public void stopCollect(){
        Intent intent = new Intent(context,CollectService.class);
        context.stopService(intent);
        isStarted = false;
    }

    public boolean isStarted(){
        return isStarted;
    }
    /**
     * 供app实现的BeaconList改变事件的监听器，
     * 实现Service对App的通知
     */
    public interface OnBeaconListChangeListener {
        void onBeaconListChange(List<IBeacon> beacons);
    }

}
