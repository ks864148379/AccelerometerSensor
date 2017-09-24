package ibeacondata.net;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LK on 2016/10/9.
 * 用来取消请求
 * 因为每次发起请求，都会把为此创建的request添加到RequestManager中
 * 所以RequestManager中保存了全部的request
 */
public class RequestManager {
    private Context context;
    private List<Request> requestList = null;
    private static volatile RequestManager manager = null;
    public static final String IP ="http://123.57.46.160:8080/bupt3";//服务器IP
    //public static final String IP ="http://10.103.242.183:8081";//汉达IP
    //public static final String IP ="http://10.103.240.141:8080";
    public static final String BeaconDataUploadJoggle = IP +"/api/beaconInfo/upload/";
    //public static final String BeaconDataUploadJoggle = IP +"/upload";
    private RequestManager(Context context){
        this.context = context;
        requestList = new ArrayList<>();
    }

    public static RequestManager getInstance(Context context){
        if (manager ==null){
            synchronized (RequestManager.class){
                if (manager == null){
                    manager = new RequestManager(context);
                }
            }
        }
        return manager;
    }

    private void addRequest(final Request request){
        requestList.add(request);
    }

    public void cancelRequest(){
        /*if(requestList !=null && requestList.size()>0){
            for (Request request : requestList){
                request.abort();
                requestList.remove(request);
            }
        }*/
        if (requestList != null && requestList.size()>0){
            for (int i = 0;i<requestList.size();i++){
                Request request = requestList.get(i);
                requestList.remove(request);
            }
        }
        DefaultThreadPool.getInstance().removeAllTask();
    }

    public void invokeRequest(final String joggle,
                              final RequestCallback callback){
        this.invokeRequest(joggle, null, callback);
    }

    public void invokeRequest(final String joggle,
                              final List<List<RequestParameter>> parameters,
                              final RequestCallback callback){
        Request request = new Request(joggle,generateReportData(parameters),callback);
        addRequest(request);
        DefaultThreadPool.getInstance().execute(request);
    }

    private String generateReportData(List<List<RequestParameter>> parameters){
        JSONObject finalObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            if (parameters !=null && parameters.size()>0){
                for (List<RequestParameter> list : parameters){
                    JSONObject object = new JSONObject();
                    for (RequestParameter p : list){
                        object.put(p.getName(),p.getValue());
                    }
                    jsonArray.put(object);
                }
                finalObj.put("data",jsonArray);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        String str = "{\"data\":\"liukai\"}";
        //return "param="+finalObj.toString();
        //return "jsonstr="+str;
        return finalObj.toString();
    }
}
