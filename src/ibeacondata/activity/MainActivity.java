package ibeacondata.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.example.accelerometersensortest.R;
import ibeacondata.bean.BeaconBean;
import ibeacondata.db.DataBaseHelper;
import ibeacondata.db.DataDao;
import ibeacondata.service.CollectManager;
import ibeacondata.service.UploadManager;
import ibeaconconn.scan.IBeaconManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {
    private IBeaconManager iBeaconManager;
    private CollectManager collectManager;
    private UploadManager uploadManager;
    private DataBaseHelper dbHelper;
    private DataDao dataDao;
    private Button bt_location;
    private Button bt_start;
    private Button bt_stop;
    private Button bt_startUpload;
    private Button bt_stopUpload;
    private Button bt_currentVisitor;
    private Button bt_totalVisitor;
    private Button bt_scanMap;
    private Button bt_setting;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        bt_location = (Button) findViewById(R.id.location);
        bt_start = (Button) findViewById(R.id.start);
        bt_stop = (Button) findViewById(R.id.stop);
        bt_startUpload = (Button) findViewById(R.id.start_upload);
        bt_stopUpload = (Button) findViewById(R.id.stop_upload);
        bt_currentVisitor = (Button) findViewById(R.id.currentVisitor);
       // bt_totalVisitor = (Button) findViewById(R.id.totalVisitor);
        bt_setting = (Button) findViewById(R.id.setting);
        bt_scanMap = (Button) findViewById(R.id.scanMap);
        iBeaconManager = IBeaconManager.getInstanceForApplication(this);
        collectManager = CollectManager.getInstance(this);
        uploadManager = UploadManager.getInstance(this);
        dbHelper = DataBaseHelper.getInstance(getBaseContext());
        dbHelper.open();
        dataDao = DataDao.getInstance(this);
        BeaconBean beaconBean = new BeaconBean();
        beaconBean.setDevice_id("kupai8670");
        beaconBean.setMac_id("4D69HG9078HG");
        beaconBean.setUuid("hadshsjf");
        beaconBean.setMajor(11111);
        beaconBean.setMajor(22222);
        beaconBean.setRssi(80);
        beaconBean.setDistance(3.28);
        beaconBean.setCollectime(new Date().getTime());
        beaconBean.setFlag(0);
        if (collectManager !=null && collectManager.isStarted()){
            bt_start.setVisibility(View.GONE);
        }
        if (collectManager !=null && !collectManager.isStarted()){
            bt_stop.setVisibility(View.GONE);
        }
        if (uploadManager != null && uploadManager.isStarted()){
            bt_startUpload.setVisibility(View.GONE);
        }
        if (uploadManager != null && !uploadManager.isStarted()){
            bt_stopUpload.setVisibility(View.GONE);
        }
        //dataDao.addBeaconData(beaconBean,DataBaseHelper.T_BEACON);
        //List<BeaconBean> beaconBeans = dataDao.getBeaconData(DataBaseHelper.T_BEACON);
        //dataDao.deleteBeaconData(DataBaseHelper.T_BEACON);
        bt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* LocationBean location = new LocationBean();
                location.setDevice_id("23884848098");
                location.setBuilding("学三公寓");
                location.setFloor("三层");
                location.setPosition_x(23.65);
                location.setPosition_y(56.32);
                dataDao.addLocationData(location);*/
                /*LocationBean location =dataDao.getLoacationData();
                System.out.println(location.getBuilding());*/
                Intent intent = new Intent(MainActivity.this,BSLocationActivity.class);
                startActivity(intent);
            }
        });

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(collectManager !=null && !collectManager.isStarted()){
                    collectManager.startCollect();
                    bt_start.setVisibility(View.GONE);
                    bt_stop.setVisibility(View.VISIBLE);
                }
            }
        });

        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collectManager !=null && collectManager.isStarted()){
                    collectManager.stopCollect();
                    bt_stop.setVisibility(View.GONE);
                    bt_start.setVisibility(View.VISIBLE);
                }
            }
        });

        bt_startUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadManager != null && !uploadManager.isStarted()){
                    uploadManager.startUpload();
                    bt_startUpload.setVisibility(View.GONE);
                    bt_stopUpload.setVisibility(View.VISIBLE);
                }
            }
        });

        bt_stopUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadManager != null&& uploadManager.isStarted()){
                    uploadManager.stopUpload();
                    bt_stopUpload.setVisibility(View.GONE);
                    bt_startUpload.setVisibility(View.VISIBLE);
                }
            }
        });

        bt_currentVisitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HelloChartActivity.class);
                startActivity(intent);

            }
        });

       /* bt_totalVisitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BeaconBean> beaconBeanList = dataDao.getVisitorList(0,(new Date()).getTime()/1000);
                List<BeaconBean> list = new ArrayList<BeaconBean>();
                System.out.println(beaconBeanList.size());
                Iterator<BeaconBean> iterator = beaconBeanList.iterator();
                while (iterator.hasNext()){
                    BeaconBean beacon = iterator.next();
                    if (!list.contains(beacon)){
                        list.add(beacon);
                    }
                }
                Toast.makeText(MainActivity.this,"累计人数："+list.size(),Toast.LENGTH_LONG).show();
            }
        });*/

        bt_scanMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ScanMapActivity.class);
                startActivity(intent);
            }
        });

        /*bt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeaconBean bean = new BeaconBean();
                bean.setMac_id("11:22:33:66");
                bean.setCollectime(1477873740);
                dataDao.addBeaconData(bean,DataBaseHelper.T_BEFORE);
            }
        });*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        initBluetooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initBluetooth(){
        final BluetoothAdapter blueToothEable = BluetoothAdapter.getDefaultAdapter();
        if (!blueToothEable.isEnabled()){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("蓝牙开启")
                    .setMessage("需要开启蓝牙")
                    .setCancelable(false)
                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           /* if(collectManager !=null && !collectManager.isStarted()){
                                collectManager.startCollect();
                            }*/
                            blueToothEable.enable();
                        }
                    }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   MainActivity.this.finish();
                }
            }).create().show();
        }else {
            /*if(collectManager !=null && !collectManager.isStarted()){
                collectManager.startCollect();
            }*/
        }
    }

    private String getDeviceInfo() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        String imei = mTelephonyMgr.getDeviceId();
        String model1 = android.os.Build.MODEL;
        return "MODEL:"+model1+"  "+"IMSI:"+imsi+"  "+"IMEI:"+imei;
    }

}
