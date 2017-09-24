package ibeacondata.activity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import ibeacondata.bean.LocationBean;
import ibeacondata.db.DataBaseHelper;
import ibeacondata.db.DataDao;
import ibeacondata.util.NetConnHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LK on 2016/10/27.
 */
public class NetWorkService extends IntentService {
    private DataDao dataDao;
    //public String UPLOAD_API="http://10.103.240.141:8080/hotmap/upload";
    //public String UPLOAD_API="http://10.103.240.141:8080/upload";
    //public String UPLOAD_API="http://10.103.242.183:8080/api/beaconInfo/setDeviceLoaction";//汉达IP
    public String UPLOAD_API="http://123.57.46.160:8080/bupt3/api/beaconInfo/setDeviceLoaction";
    private HttpURLConnection conn = null;
    private String finalResult = "";

    public NetWorkService() {
        super("NetWorkService");
    }
    public NetWorkService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        dataDao = DataDao.getInstance(NetWorkService.this);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(this.getClass().getName(), "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void postToUIThread(String message){
        final String mes = message;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String result = "";
        LocationBean location = null;
        location = dataDao.getLoacationData();
        if (location != null){
            if (NetConnHelper.isConn(NetWorkService.this)){
                try {
                    result = uploadData(location,UPLOAD_API);
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (jsonObject.has("success")){
                        if (jsonObject.getBoolean("success")){
                            postToUIThread("上传成功");
                            Thread.sleep(800);
                            BSLocationActivity.instance.finish();
                        }else {
                            postToUIThread("上传出错");
                        }
                    }else {
                        postToUIThread("上传出错");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    postToUIThread("上传失败");
                }
            }else {
                postToUIThread("没有网络，请检查网络情况");
            }

        }else {
            postToUIThread("没有上传数据");
        }
    }

    public String uploadData(LocationBean location,String url) throws JSONException {
        JSONObject object = new JSONObject();
        object.put(DataBaseHelper.DEVICEID,location.getDevice_id());
        object.put(DataBaseHelper.BUILDING,location.getBuilding());
        object.put(DataBaseHelper.FLOOR,location.getFloor());
        object.put(DataBaseHelper.POSITION_X,location.getPosition_x());
        object.put(DataBaseHelper.POSITION_Y,location.getPosition_y());
        object.put(DataBaseHelper.SPOTNAME,location.getSpotName());
        object.put(DataBaseHelper.SPOTID,location.getSpotId());
        String data = object.toString();
        String result = sendData(data,url);
        return result;
    }

    public String sendData(String data,String url){
        BufferedReader reader;
        OutputStreamWriter writer;
        Log.i("Request", "start http request:" + url);
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("content-type", "text/html;charset=UTF-8");
            //这个writer会把data写到http正文里
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            writer.close();

            //getInputStream开始真正发送
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) !=null){
                result.append(line);
            }
            finalResult = result.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        finally {
            if (conn != null){
                conn.disconnect();
            }
        }
        return finalResult;
    }
}
