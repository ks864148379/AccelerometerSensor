package ibeacondata.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import com.example.accelerometersensortest.R;
import com.wxq.draw.DrawDBTool;
import com.wxq.draw.MapControler;

/**
 * Created by LK on 2016/10/26.
 */
public class ScanMapActivity extends Activity {
    MapControler mapLayout;
    private String[] mapDbIds = null;
    private String[] mapDbNames = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scanmap);
        mapLayout = (MapControler) findViewById(R.id.mapLayout);
        if (mapLayout == null || !mapLayout.isSuccess()) {
            finish();
        } else {
            initMap();
        }
    }

    private void initMap() {
        String dbDir = android.os.Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        dbDir += "/vMapDBFile/DBfile";// 数据库所在目录
        mapDbIds = PublicData.getInstance().getFoldFiles(dbDir);
        mapDbNames = new String[mapDbIds.length];
        for (int i = 0; i < mapDbIds.length; i++) {
            String nameDB = mapDbIds[i];
            DrawDBTool tool = new DrawDBTool(this);
            tool.setDBName(nameDB);
            String name = tool.getMallName();
            mapDbNames[i] = name;
        }

    }
    public void onChangeMap(View v) {
        new AlertDialog.Builder(this)
                .setTitle("请选择")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(mapDbNames, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                                String dbName = mapDbIds[which];
                                mapLayout.changedbmap(dbName, "null", false);
                                dialog.dismiss();
                            }

                        }).setNegativeButton("取消", null).show();
    }
}