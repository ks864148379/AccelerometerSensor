package ibeacondata.net;

import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LK on 2016/10/9.
 * 发起Http请求，实现了Runnable，
 * 从而让DefaultThreadPool可以分配新的线程来执行该请求
 */
public class Request implements Runnable  {
    private String url;
    private String sdata;
    private RequestCallback requestCallback=null;
    private HttpURLConnection conn = null;

    public Request(final String joggle,
                   final String sdata,
                   final RequestCallback callback){
        this.url = joggle;
        this.sdata = sdata;
        this.requestCallback =callback;
    }
    @Override
    public void run() {
        BufferedReader reader;
        OutputStreamWriter writer;
        Log.i("Request","start http request:"+url);
        try {
            Log.i("Request","data:"+sdata);
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("content-type", "text/html;charset=UTF-8");
            //这个writer会把data写到http正文里
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(sdata);
            writer.flush();
            writer.close();

            //getInputStream开始真正发送
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) !=null){
                result.append(line);
            }
            if (result.length() <=0){
                Log.i("Request","request error");
            }
            Log.i("Request","result:"+result.toString());
            JSONObject jsonObject = new JSONObject(result.toString());
            if (jsonObject.has("success")){
                if (jsonObject.getBoolean("success")){
                    requestCallback.onSuccess(jsonObject);
                }else {
                    requestCallback.onFail(jsonObject);
                }
            }else {
                requestCallback.onFail(jsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
            requestCallback.onFail(null);
        }finally {
            if (conn != null){
                conn.disconnect();
            }
        }
    }

    public void abort(){
        if (this.conn !=null){
            conn.disconnect();
        }
    }
}
