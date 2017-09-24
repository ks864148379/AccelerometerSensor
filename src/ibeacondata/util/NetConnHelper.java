package ibeacondata.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by LK on 2016/10/11.
 */
public class NetConnHelper {
    public static boolean isConn(Context context){
        boolean bisConnFlag = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null){
            bisConnFlag =conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }
}
