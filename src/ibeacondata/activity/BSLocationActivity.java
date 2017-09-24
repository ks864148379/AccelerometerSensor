package ibeacondata.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.accelerometersensortest.R;
import com.lxr.overflot.OverlayIcon;
import com.lxr.overflot.OverlayLayout;
import com.wxq.draw.DrawDBTool;
import com.wxq.draw.MapControler;
import ibeacondata.bean.LocationBean;
import ibeacondata.db.DataDao;

/**
 * Created by LK on 2016/10/26.
 */
public class BSLocationActivity extends Activity {
    LocationBean location = new LocationBean();
    MapControler mapLayout;
    private String[] mapDbIds = null;
    private String[] mapDbNames = null;
    OverlayIcon overlaypointer;
    private OverlayLayout overlaylayout;
    private Button btn_save;
    private Button btn_upload;
    private EditText deviceId;
    private Boolean locationSelected = false;//位置是否选择
    private Boolean locationSaved =false;//位置是否保存
    private DataDao dataDao;
    public static BSLocationActivity instance = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bslocation);
        instance = this;
        dataDao = DataDao.getInstance(this);
        mapLayout = (MapControler) findViewById(R.id.mapLayout);
        deviceId = (EditText) findViewById(R.id.mac_edit);
        btn_save = (Button) findViewById(R.id.save);
        btn_upload = (Button) findViewById(R.id.upload);
        if (mapLayout == null || !mapLayout.isSuccess()) {
            finish();
        } else {
            initMap();
            initNewLocationView();
        }
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "选择的位置为:\nbuilding:%s\nfloor:%s\nx:%s\ny:%s\nspotName:%s";
                new AlertDialog.Builder(BSLocationActivity.this)
                        .setTitle("提示")
                        .setMessage(String.format(msg,location.getBuilding(),location.getFloor(),location.getPosition_x(),location.getPosition_y(),location.getSpotName()))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (locationSelected) {
                                    dataDao.updateLocationData(location);
                                    locationSaved = true;
                                    Toast.makeText(BSLocationActivity.this,"位置选定成功！",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(BSLocationActivity.this,"尚未选择位置！",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).create().show();
            }
        });

       btn_upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (locationSaved){
                   Intent intent = new Intent(BSLocationActivity.this,NetWorkService.class);
                   startService(intent);
               }else {
                   Toast.makeText(BSLocationActivity.this,"尚未保存位置！",Toast.LENGTH_SHORT).show();
               }
           }
       });

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

    private void initNewLocationView(){
        overlaypointer = new OverlayIcon(mapLayout,R.drawable.loc_pointer,0.8f,false,-1);
        overlaylayout = new OverlayLayout(mapLayout,R.layout.navigation_overlay_modify,2);
        Button btn = (Button) overlaylayout.layoutView.findViewById(R.id.bt_locates);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapLayout.getFloatView().clear();
                locationSelected = true;
                location.setDevice_id(deviceId.getText().toString());
                System.out.println("idid:" + deviceId.getText().toString());
                location.setBuilding(mapLayout.getDbfile());
                location.setFloor(mapLayout.getFloor());
                location.setPosition_x(overlaylayout.MapCoord.x);
                location.setPosition_y(overlaylayout.MapCoord.y);
                String spotName = mapLayout
                        .getSpotInfoAtMapCoord(overlaylayout.MapCoord);
                String spotId = mapLayout.getCurrentSpotID();
                location.setSpotName(spotName);
                location.setSpotId(spotId);
                drawLocation(overlaylayout.MapCoord.x,overlaylayout.MapCoord.y);
            }
        });

        mapLayout.setMapclicklistener(new MapControler.IMapClickListener() {
            @Override
            public void mapClicked(float arg0, float arg1) {
                overlaypointer.pinAtMapWithScreenCoord(new PointF(arg0, arg1));
                overlaylayout.pinAtMapWithScreenCoord(new PointF(arg0, arg1));
            }

            @Override
            public void mapChanged() {

            }

            @Override
            public void floorChanged() {

            }
        });
    }


    private void drawLocation(float x, float y) {
        Paint paintEmptyB = new Paint();
        paintEmptyB.setStyle(Paint.Style.FILL);
        paintEmptyB.setColor(Color.BLUE);
        paintEmptyB.setAlpha(20);

        Paint paintEmptyc = new Paint();
        paintEmptyc.setStyle(Paint.Style.FILL);
        paintEmptyc.setColor(Color.BLACK);


        Paint paintCricleB = new Paint();
        paintCricleB.setStyle(Paint.Style.STROKE);
        paintCricleB.setColor(Color.BLUE);

        mapLayout.getFloatView().addCircle("ha",
                x,
                y,
                5,
                paintCricleB);

        mapLayout.getFloatView()
                .addCircle("hah",
                        x,
                        y,
                        5,
                        paintEmptyB);
        mapLayout.getFloatView()
                .addCircle("h",
                        x,
                        y,
                        1,
                        paintEmptyc);
    }

    /**
     * 获取手机信息
     * @return
     */
    private String getDeviceInfo() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        String imei = mTelephonyMgr.getDeviceId();
        String model1 = Build.MODEL;
        return imei;
    }
}