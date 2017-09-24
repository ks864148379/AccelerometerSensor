package ibeacondata.net;

import org.json.JSONObject;

/**
 * Created by LK on 2016/10/9.
 */
public interface RequestCallback {
    void onSuccess(JSONObject object);

    void onFail(JSONObject object);
}
