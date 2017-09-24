package ibeacondata.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import ibeacondata.bean.BeaconBean;
import ibeacondata.db.DataBaseHelper;
import ibeacondata.db.DataDao;
import ibeacondata.net.DefaultThreadPool;
import ibeacondata.net.RequestCallback;
import ibeacondata.net.RequestManager;
import ibeacondata.net.RequestParameter;
import ibeacondata.util.NetConnHelper;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LK on 2016/10/6.
 */
public class    UploadService extends Service implements RequestCallback {
    private RequestManager requestManager;
    private DataDao dataDao;
    private DataReportRunnable runnable;
    public static final int UPLOAD_SUCCESS =0;
    public static final int UPLOAD_FAILURE =1;
    public static final int UPLOAD_NODATA =2;
    public static final int UPLOAD_NONET = 3;
    Handler mHandler = new MyHandler(this);

    /**
     * 弱引用，防止使用Handler造成内存泄露
     */
    private class MyHandler extends  Handler {
        private final WeakReference<UploadService> mservice;

        private MyHandler(UploadService service) {
            mservice = new WeakReference<UploadService>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            UploadService service = mservice.get();
            if (service != null){
                switch (msg.what){
                    case UPLOAD_SUCCESS:
                        Toast.makeText(UploadService.this,getTime()+"   数据上传成功",Toast.LENGTH_LONG).show();
                        break;
                    case UPLOAD_FAILURE:
                        Toast.makeText(UploadService.this,getTime()+"   数据上传失败",Toast.LENGTH_LONG).show();
                        break;
                    case UPLOAD_NODATA:
                        Toast.makeText(UploadService.this,getTime()+"   无上传数据",Toast.LENGTH_LONG).show();
                        break;
                    case UPLOAD_NONET:
                        Toast.makeText(UploadService.this,getTime()+"   无网络，请打开网络！",Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("UploadService", "服务被创建");
        requestManager = RequestManager.getInstance(UploadService.this);
        dataDao = DataDao.getInstance(UploadService.this);
        runnable = new DataReportRunnable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DefaultThreadPool.getInstance().execute(runnable);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("UploadService", "服务被销毁");
        runnable.setStop();
        if(requestManager != null){
            requestManager.cancelRequest();
        }
    }

    @Override
    public void onSuccess(JSONObject object) {
        mHandler.sendEmptyMessage(UPLOAD_SUCCESS);
        dataDao.deleteBeaconData(DataBaseHelper.T_BEACON);
        //dataDao.deleteBeaconData(DataBaseHelper.T_BEFORE);
        //dataDao.deleteBeaconData(DataBaseHelper.T_AFTER);
    }

    @Override
    public void onFail(JSONObject object) {
        mHandler.sendEmptyMessage(UPLOAD_FAILURE);
    }

    class DataReportRunnable implements Runnable{
        private boolean stop = false;
        public void setStop(){
            stop=true;
        }
        @Override
        public void run() {
            while (!stop){
                if (NetConnHelper.isConn(UploadService.this)){
                    List<List<RequestParameter>> beaconData = generateBeaconData();
                    if (beaconData != null && beaconData.size()>0){
                        requestManager.invokeRequest(RequestManager.BeaconDataUploadJoggle,
                                generateBeaconData(),UploadService.this);
                    }else {
                        mHandler.sendEmptyMessage(UPLOAD_NODATA);
                    }
                }else {
                    mHandler.sendEmptyMessage(UPLOAD_NONET);
                }

                try {
                    Thread.sleep(15*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<List<RequestParameter>> generateBeaconData(){
        List<List<RequestParameter>> parameters = new ArrayList<>();
        List<RequestParameter> parameterList;
        List<BeaconBean> beaconBeanList = dataDao.getBeaconData(DataBaseHelper.T_BEACON);
        for (BeaconBean beaconBean : beaconBeanList){
            parameterList = new ArrayList<>();
            parameterList.add(new RequestParameter(DataBaseHelper.DEVICEID,beaconBean.getDevice_id()));
            parameterList.add(new RequestParameter(DataBaseHelper.MACID,beaconBean.getMac_id().replaceAll(":","")));
            parameterList.add(new RequestParameter(DataBaseHelper.UUID,beaconBean.getUuid()));
            parameterList.add(new RequestParameter(DataBaseHelper.MAJOR,String.valueOf(beaconBean.getMajor())));
            parameterList.add(new RequestParameter(DataBaseHelper.MINOR,String.valueOf(beaconBean.getMinor())));
            parameterList.add(new RequestParameter(DataBaseHelper.RSSI,String.valueOf(beaconBean.getRssi())));
            parameterList.add(new RequestParameter(DataBaseHelper.DISTANCE,String.valueOf(beaconBean.getDistance())));
            parameterList.add(new RequestParameter(DataBaseHelper.COLLECTIMESTRING,beaconBean.getTime()));
            parameterList.add(new RequestParameter(DataBaseHelper.FLAG,String.valueOf(beaconBean.getFlag())));
            parameters.add(parameterList);
        }
        return parameters;
    }

    /**
     *获取当前时间
     * @return
     */
    public String getTime(){
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(new Date());
    }
}
