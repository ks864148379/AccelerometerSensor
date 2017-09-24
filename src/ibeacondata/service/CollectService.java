package ibeacondata.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import ibeacondata.bean.BeaconBean;
import ibeacondata.db.DataBaseHelper;
import ibeacondata.db.DataDao;
import ibeaconconn.base.IBeacon;
import ibeaconconn.scan.IBeaconConsumer;
import ibeaconconn.scan.IBeaconManager;
import ibeaconconn.scan.Region;
import ibeaconconn.scan.monitor.MonitorNotifier;
import ibeaconconn.scan.range.RangeNotifier;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by LK on 2016/10/6.
 */
public class CollectService extends Service implements IBeaconConsumer {
    private IBeaconManager iBeaconManager;
    private ArrayList<IBeacon> beaconArrayList = new ArrayList<>();//蓝牙扫描得到的beacon数据
    private ArrayList<IBeacon> copybeaconArryList = new ArrayList<>();
    private ArrayList<IBeacon> beaconDataList = new ArrayList<>();//多次扫描均值
    private MyThread thread;
    private DataDao dataDao;
    private List<BeaconBean> beaconList = new ArrayList<>();//最终处理后的数据
    private List<BeaconBean> beanList1= new ArrayList<>() ;//处理后放入(间隔前)的数据
    private List<BeaconBean> beaconListA = new ArrayList<>();//5s间隔前采集的数据
    private List<BeaconBean> beaconListB = new ArrayList<>();//5s间隔后采集的数据
    private List<BeaconBean> totalList = new ArrayList<>();//2个大周期采到的数据
    private List<BeaconBean> beforeList = new ArrayList<>();
    private List<BeaconBean> afterList = new ArrayList<>();
    private Map<String,Integer> beaconMap = new HashMap<String,Integer>();
    private List<BeaconBean> lastList = new ArrayList();

    ArrayList<BeaconBean> tlist = new ArrayList<>();//进场的beacon,flag =0的beacon
    public static final int COLLECT_SUCCESS =0;
    private Lock lock = new ReentrantLock();
    public static int beaconDistance = -1;

    Handler mhandler = new MyHandler(this);

    /**
     * 弱引用，防止使用Handler造成内存泄露
     */
    private  class MyHandler extends Handler{
        private final WeakReference<CollectService> mService;

        private MyHandler(CollectService service) {
            mService = new WeakReference<CollectService>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            CollectService service = mService.get();
            if (service !=null){
                switch (msg.what){
                    case COLLECT_SUCCESS:
                        Toast.makeText(CollectService.this,"采集成功",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setRangeNotifier(new RRangeNotifier());
        iBeaconManager.setMonitorNotifier(new MMonitorNotifier());
        try {
            //Region myRegion = new Region("ibeaconRegion",null,null,null);
            //Region myRegion = new Region("ibeaconRegion","abc03659-f3a2-8df3-1110-f5247c015336",10000,null);
            Region myRegion = new Region("ibeaconRegion", null, null ,null);
            iBeaconManager.startRangingBeaconsInRegion(myRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("CollectService", "服务被创建");
        iBeaconManager = IBeaconManager.getInstanceForApplication(this);
        dataDao = DataDao.getInstance(this);
        thread = new MyThread();
        iBeaconManager.setForegroundScanPeriod(200);
        if (!iBeaconManager.isBound(CollectService.this)) {
            iBeaconManager.bind(CollectService.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iBeaconManager != null && iBeaconManager.isBound(this)) {
            iBeaconManager.unBind(this);
        }
        Log.i("CollectService", "服务被销毁");
        thread.setStop();
        copybeaconArryList.clear();
        beaconDataList.clear();
        beaconList.clear();
        beanList1.clear();
        beaconListA.clear();
        beaconListB.clear();
        beforeList.clear();
        afterList.clear();
        beaconMap.clear();
        lastList.clear();
        //CollectService销毁时清空临时数据表
        //dataDao.deleteBeaconData(DataBaseHelper.T_BEFORE);
        //dataDao.deleteBeaconData(DataBaseHelper.T_AFTER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }


    class RRangeNotifier implements RangeNotifier {

        @Override
        public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {

        }

        @Override
        public void onNewBeacons(Collection<IBeacon> iBeacons, Region region) {
            Iterator<IBeacon> iterator = iBeacons.iterator();
            while (iterator.hasNext()){
                IBeacon iBeacon = iterator.next();
                if(!beaconArrayList.contains(iBeacon)){
                    beaconArrayList.add(iBeacon);
                }
                //
            }
        }

        @Override
        public void onGoneBeacons(Collection<IBeacon> iBeacons, Region region) {
            Iterator<IBeacon> iterator = iBeacons.iterator();
            while (iterator.hasNext()){
                IBeacon iBeacon = iterator.next();
                if(beaconArrayList.contains(iBeacon)){
                    beaconArrayList.remove(iBeacon);
                }
                //
            }
        }

        @Override
        public void onUpdateBeacon(Collection<IBeacon> iBeacons, Region region) {
            Iterator<IBeacon> iterator = iBeacons.iterator();
            while (iterator.hasNext()){
                IBeacon iBeacon = iterator.next();
                if(beaconArrayList.contains(iBeacon)){
                    beaconArrayList.set(beaconArrayList.indexOf(iBeacon),iBeacon);
                }
                //
            }
        }
    }

    class MMonitorNotifier implements MonitorNotifier {

        @Override
        public void didEnterRegion(Region region) {

        }

        @Override
        public void didExitRegion(Region region) {

        }

        @Override
        public void didDetermineStateForRegion(int i, Region region) {

        }
    }


    public class MyThread extends Thread{
        private boolean stop = false;
        public void setStop(){
            stop=true;
        }
        /*
        private List<BeaconBean> beaconTime = new ArrayList<>();//临时数据
        private List<BeaconBean> tList0 = new ArrayList<>();//临时数据
        private List<BeaconBean> tList1 = new ArrayList<>();//临时数据
        private List<BeaconBean> tList2 = new ArrayList<>();//临时数据
        private List<BeaconBean> tList3 = new ArrayList<>();//临时数据
        */
        double period =0 ;
        @Override
        public void run() {
            lock.lock();
            try {
                while (!stop){
                    System.out.println("扫描到beacon的数量" + beaconArrayList.size());
                    //取5次的平均值
                    for (int i = 0; i < 5; i++){
                        copybeaconArryList.addAll(beaconArrayList);
                        beaconDataList = handleCollectData(beaconDataList,copybeaconArryList);
                        copybeaconArryList.clear();
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //实验之后,过滤掉小于-99db的beacon
                    beaconDataList = filterBeaconData(beaconDataList);
                    copybeaconArryList.clear();
                    beaconListB = getBeaconBeanList(beaconDataList);

                    //对于这次扫描出来的beacon，设置状态为1
                    for (int i = 0; i < beaconListB.size(); i++) {
                        String currentId = beaconListB.get(i).getMac_id();
                        beaconMap.put(currentId,1);
                    }

                    //对于之前扫描出来，这次没扫描出来的beacon，更新状态
                    Iterator<Map.Entry<String, Integer>> it = beaconMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String ,Integer> entry = it.next();
                        String s = entry.getKey();
                        int count = 0;
                        for (int i = 0; i < beaconListB.size(); i++) {
                            if (s.equals(beaconListB.get(i).getMac_id())) {
                                count++;
                            }
                        }
                        if (count == 0) {
                            entry.setValue(entry.getValue() - 1);
                            if (entry.getValue() <= -2) {
                                for (int j = 0; j < lastList.size(); j++) {
                                    BeaconBean lastBean = lastList.get(j);
                                    if (lastBean.getMac_id().equals(s)) {
                                        //离场
                                        lastBean.setRssi(-200);
                                        lastBean.setDistance(100);//离场的Beacon距离设为9米
                                        lastBean.setCollectime((new Date()).getTime()/1000);
                                        lastBean.setTime(getTime());
                                        lastBean.setFlag(1);
                                        beaconList.add(lastBean);
                                        lastList.remove(j);
                                        break;
                                    }
                                }
                                it.remove();
                            }
                        }
                    }

                    List<String> removeAll = new ArrayList<String>();


                    for (int i = 0; i < beaconListB.size(); i++) {
                        BeaconBean currentBean = beaconListB.get(i);
                        int count = 0;
                        for (int j = 0; j < lastList.size(); j++) {
                            BeaconBean lastBean = lastList.get(j);
                            if (currentBean.getMac_id().equals(lastBean.getMac_id())) {
                                count ++ ;
                                if((currentBean.getRssi() - lastBean.getRssi()) > beaconDistance
                                        || (lastBean.getRssi() - currentBean.getRssi()) > beaconDistance) {
                                    currentBean.setFlag(2); //在区域内
                                    beaconList.add(currentBean);
                                    beaconMap.put(currentBean.getMac_id(),1);
                                    removeAll.add(lastBean.getMac_id());
                                    break;
                                }
                            }
                        }
                        if (count == 0) {
                            currentBean.setFlag(0);//进场
                            beaconList.add(currentBean);
                            beaconMap.put(currentBean.getMac_id(),1);
                            lastList.add(currentBean);
                        }
                    }

                    for (int i = 0; i < removeAll.size(); i++) {
                        for (int j = 0; j < lastList.size(); j++) {
                            if (removeAll.get(i).equals(lastList.get(j).getMac_id())) {
                                lastList.remove(j);
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < removeAll.size(); i++) {
                        for (int j = 0; j < beaconListB.size(); j++) {
                            BeaconBean currentBean = beaconListB.get(j);
                            if (removeAll.get(i).equals(currentBean.getMac_id())) {
                                lastList.add(currentBean);
                                break;
                            }
                        }
                    }

                    for (BeaconBean beacon : beaconList){
                        dataDao.addBeaconData(beacon,DataBaseHelper.T_BEACON);
                        dataDao.addBeaconData(beacon,DataBaseHelper.T_BEFORE);
                    }
                    //beforeList.addAll(beanList1);
                    beanList1.clear();
                    beaconList.clear();
                    beaconDataList.clear();
                    //beaconTime.clear();
                    period = period+1;
                    try {
                        Thread.sleep(2*1000);

                        mhandler.sendEmptyMessage(COLLECT_SUCCESS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        }
    }

    /**
     * 实验之后,过滤掉Rssi<-97db的Beacon
     * @param beaconList
     * @return
     */
    public ArrayList<IBeacon> filterBeaconData(ArrayList<IBeacon> beaconList){
        if (beaconList.size()<=0){
            return beaconList;
        }else {
            List<IBeacon> list = new ArrayList<>();
            for (IBeacon iBeacon : beaconList){
                if (iBeacon.getRssi()<=(-92)){
                    list.add(iBeacon);
                }
            }
            beaconList.removeAll(list);
            return beaconList;
        }
    }

    /**
     * 前后两次结果扫描取均值
     * @param iBeaconListA
     * @param iBeaconListB
     * @return
     */
    public ArrayList<IBeacon> handleCollectData(ArrayList<IBeacon> iBeaconListA,
                                                ArrayList<IBeacon> iBeaconListB){
        if (iBeaconListA.size()<=0 || iBeaconListB.size()<=0){
            iBeaconListA.addAll(iBeaconListB);
        }else {
            ArrayList<IBeacon> list =new ArrayList<>();
            list.addAll(iBeaconListB);
            for (IBeacon iBeaconA : iBeaconListA){
                for (IBeacon iBeaconB : iBeaconListB){
                    if ((iBeaconA.getBluetoothAddress()).equals((iBeaconB.getBluetoothAddress()))){
                        iBeaconA.setRssi((iBeaconA.getRssi()+iBeaconB.getRssi())/2);
                        iBeaconA.setAccuracy((iBeaconA.getAccuracy()+iBeaconB.getAccuracy())/2);
                        list.remove(iBeaconB);
                    }
                }
            }
            iBeaconListA.addAll(list);
        }
        return iBeaconListA;
    }

    /**
     * 将IBeacon转换为BeaconBean
     * @param beconList
     * @return
     */
    public List<BeaconBean> getBeaconBeanList (ArrayList<IBeacon> beconList){
        List<BeaconBean> list = new ArrayList<>();
        for (IBeacon iBeacon : beconList){
            BeaconBean beacon = new BeaconBean();
            beacon.setDevice_id(getDeviceInfo());
            beacon.setMac_id(iBeacon.getBluetoothAddress());
            beacon.setUuid(iBeacon.getProximityUuid());
            beacon.setMajor(iBeacon.getMajor());
            beacon.setMinor(iBeacon.getMinor());
            beacon.setRssi(iBeacon.getRssi());
            beacon.setCollectime((new Date()).getTime()/1000);
            beacon.setDistance(Double.parseDouble(getDistance(iBeacon)));
            beacon.setTime(getTime());
            list.add(beacon);
        }
        return list;
    }

    /**
     * 比较间隔前后beacon进场，离场，区域内信号变化情况
     * @param beaconBeansA
     * @param beaconBeansB
     */
    public void handleBeaconData( List<BeaconBean> beaconBeansA,
                                 List<BeaconBean> beaconBeansB){
        ArrayList<BeaconBean> list1 = new ArrayList<>();
        ArrayList<BeaconBean> list2 = new ArrayList<>();
        //处理区域内信号变化的Beacon
        for (int i=0;i<beaconBeansB.size();i++){
            BeaconBean beaconBeanB=beaconBeansB.get(i);
            for(int j=0;j<beaconBeansA.size();j++){
                BeaconBean beaconBeanA = beaconBeansA.get(j);
                if((beaconBeanA.getMac_id()).equals(beaconBeanB.getMac_id())){
                    if((beaconBeanA.getRssi()-beaconBeanB.getRssi())>3
                            ||(beaconBeanB.getRssi()-beaconBeanA.getRssi())>3){
                        beaconBeanB.setFlag(2);//在区域内
                        beaconList.add(beaconBeanB);
                        beanList1.add(beaconBeanB);
                    }else {
                        beanList1.add(beaconBeanA);
                    }
                    list1.add(beaconBeanA);
                    list2.add(beaconBeanB);
                    //找了1天问题出在这！！！
                    //beaconBeansB.remove(j);
                    //beaconBeansA.remove(i);
                }
            }
        }
        if (beaconBeansA.size()>0 && list1.size()>0){
            beaconBeansA.removeAll(list1);
        }
       if (beaconBeansB.size()>0 && list2.size()>0){
           beaconBeansB.removeAll(list2);
       }

        for (int k =0;k<beaconBeansB.size();k++){
            BeaconBean beacon = beaconBeansB.get(k);
            beacon.setFlag(0);//进场
            beaconList.add(beacon);
            beanList1.add(beacon);
            tlist.add(beacon);
        }


        for (int m =0;m<beaconBeansA.size();m++){
            BeaconBean beaconA = beaconBeansA.get(m);
            if (!totalList.contains(beaconA)){
                    //tlist.remove(beaconA);
                    beaconA.setRssi(-200);
                    beaconA.setDistance(100);//离场的Beacon距离设为9米
                    beaconA.setCollectime((new Date()).getTime()/1000);
                    beaconA.setTime(getTime());
                    beaconA.setFlag(1);//离场
                    beaconList.add(beaconA);
            }
        }

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
    /**
     * 粗略获取beacon位置距离
     * @param beacon
     * @return
     */
    public String getDistance(IBeacon beacon){
        if(String.valueOf(beacon.getAccuracy()).length()>=7){
            return String.valueOf(beacon.getAccuracy()).substring(0, 7);
        }else{
            return String.valueOf(beacon.getAccuracy());
        }
    }
    /**
     * 获取当前时间
     * @return
     */
    public String getTime(){
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(new Date());
    }

}
