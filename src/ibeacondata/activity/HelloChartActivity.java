package ibeacondata.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.accelerometersensortest.R;
import ibeacondata.bean.BeaconBean;
import ibeacondata.bean.VisitorBean;
import ibeacondata.db.DataDao;
import ibeacondata.util.Time;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.*;
import lecho.lib.hellocharts.view.LineChartView;
import org.json.JSONArray;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lenovo on 2016/10/27.
 * 使用开源库HelloChart实现折线图
 */
public class HelloChartActivity extends Activity {
    private LineChartView lineChart;
    String[] date = {"8时","9时","10时","11时","12时"};
    int[] score = {11,8,5,16,3};
    private List<PointValue> mPointValuesList = new ArrayList<>();
    private List<AxisValue> mAxisXValuesList = new ArrayList<>();
    private TextView tv_totalVisitors;
    private TextView tv_currentVisitors;
    private TextView tv_timeVisotors;
    private ListView visitorlist;
    private List<BeaconBean> currentList = new ArrayList<>();//当前Visitor
    private List<BeaconBean> totalList = new ArrayList<>();//累计Visitor
    public DataDao dataDao;
    private VisitorAdapter visitorAdapter;
    private LinearLayout ll_tilte;
    private MyThread thread;
    List<VisitorBean> listXY = new ArrayList<>();
    public long timeHour;
    LinkedHashMap<Integer,List<BeaconBean>> visitorMap = new LinkedHashMap<Integer,List<BeaconBean>>();
    Handler handler = new MyHandler(this);
    int hour1 =0;
    int m =0;
    /**
     * 弱引用，防止Handler造成内存泄露
     */
    private class MyHandler extends Handler{
        private final WeakReference<HelloChartActivity> mActivity;

        private MyHandler(HelloChartActivity activity) {
            mActivity = new WeakReference<HelloChartActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            HelloChartActivity activity = mActivity.get();
            if (activity != null){
                switch (msg.what){
                    case 0:
                        //visitorAdapter.updateData(currentList);
                        //visitorAdapter.notifyDataSetChanged();
                        tv_totalVisitors.setText("累计参展人数：  "+totalList.size()+"人");
                        tv_currentVisitors.setText("当前实时参展人数：  " + currentList.size() + "人");
                        break;
                /*case 1:
                    ll_tilte.setVisibility(View.INVISIBLE);
                    break;*/
                    case 2:
                        tv_timeVisotors.setVisibility(View.VISIBLE);
                        tv_timeVisotors.setText(hour1+"时参展人数：  " + m + "人");
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hellochart);
        dataDao = DataDao.getInstance(this);
        tv_currentVisitors = (TextView) findViewById(R.id.currentVisitors);
        tv_totalVisitors = (TextView) findViewById(R.id.totalVisitors);
        tv_timeVisotors = (TextView) findViewById(R.id.timeVisitors);
        visitorlist = (ListView) findViewById(R.id.list);
        lineChart = (LineChartView) findViewById(R.id.line_chart);
        ll_tilte = (LinearLayout) findViewById(R.id.title);
        ll_tilte.setVisibility(View.INVISIBLE);
        tv_timeVisotors.setVisibility(View.INVISIBLE);
        final int[] hour = {Integer.valueOf(Time.getHour())};
        List<BeaconBean> beaconBeanList = new ArrayList<>();
        for (int i=7;i<= hour[0];i++){
            List<BeaconBean> timeList = new ArrayList<>();//按时间获取Visitor
            if (i== hour[0]){
                String time = Time.getDay()+" "+i+":00:00";
                try {
                    timeHour= Long.valueOf(Time.dateToStamp(time))/1000;
                    beaconBeanList= dataDao.getVisitorList(timeHour-30*60,(new Date()).getTime()/1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                String time =Time.getDay()+" "+i+":00:00";
                try {
                    timeHour= Long.valueOf(Time.dateToStamp(time))/1000;
                    beaconBeanList= dataDao.getVisitorList(timeHour-30*60,timeHour+30*60);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            Iterator<BeaconBean> iterator = beaconBeanList.iterator();
            while (iterator.hasNext()){
                BeaconBean beacon = iterator.next();
                if (!timeList.contains(beacon)){
                    timeList.add(beacon);
                }
            }
            VisitorBean visitorBean = new VisitorBean();
            visitorBean.setAxisX(i);
            visitorBean.setAxisY(timeList.size());
            visitorMap.put(i-7,timeList);
            listXY.add(visitorBean);
            //timeList.clear();
        }
        System.out.println(visitorMap);
        getAxisXY();
        /*getAxisPoints();
        getAxisXLables();*/
        initLineChart();
        thread = new MyThread();
        /*visitorAdapter = new VisitorAdapter(getApplicationContext(),currentList);
       // visitorAdapter.setData(currentList);
        visitorlist.setAdapter(visitorAdapter);*/

        lineChart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, PointValue pointValue) {
                List<BeaconBean> list = new ArrayList<BeaconBean>();
                list = visitorMap.get(i1);
                System.out.println(list);
                if (list.size()>0){
                    ll_tilte.setVisibility(View.VISIBLE);
                }else {
                    ll_tilte.setVisibility(View.INVISIBLE);
                }
                visitorAdapter = new VisitorAdapter(getApplicationContext(),list);
                visitorlist.setAdapter(visitorAdapter);
                hour1 = i1+7;
                m=list.size();
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.setStop();
    }

    private void initLineChart(){
        Line line = new Line(mPointValuesList).setColor(Color.parseColor("#336699"));
        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabels(true);
        line.setHasLines(true);
        line.setHasPoints(true);
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //x坐标轴
        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(false);//x轴下面坐标轴字体是斜的还是直的，true是斜的显示
        axisX.setTextColor(Color.WHITE);
        axisX.setTextColor(Color.parseColor("#336699"));
        axisX.setName(getTime() + " 参展人数统计");
        axisX.setTextSize(13);
        axisX.setMaxLabelChars(5);
        axisX.setValues(mAxisXValuesList);
        data.setAxisXBottom(axisX);
        axisX.setHasLines(true);//x轴分割线

        //y坐标轴
        Axis axisY = new Axis();
        axisY.setName("");
        axisY.setTextSize(13);
        data.setAxisYLeft(axisY);//y轴设置在左边

        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 3);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 5;
        lineChart.setCurrentViewport(v);
    }

    /**
     * X轴的显示
     */
    private void getAxisXLables(){
        for (int i=0;i<date.length;i++){
            mAxisXValuesList.add(new AxisValue(i).setLabel(date[i]));
        }
    }

    /**
     * 图表中每个点的显示
     */
    private void getAxisPoints(){
        for (int i=0;i<score.length;i++){
            mPointValuesList.add(new PointValue(i,score[i]));
        }
    }

    private void getAxisXY(){
        for (int i=0;i<listXY.size();i++){
            VisitorBean visitorBean = listXY.get(i);
            mAxisXValuesList.add(new AxisValue(i).setLabel(visitorBean.getAxisX()+"时"));
            mPointValuesList.add(new PointValue(i,visitorBean.getAxisY()));
        }
    }
    public String getTime(){
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(new Date());
    }

    public class MyThread extends Thread {
        private boolean stop = false;
        public void setStop(){
            stop=true;
        }

        @Override
        public void run() {
            while (!stop){
                totalList.clear();
                currentList.clear();
                List<BeaconBean> beaconBeanList = dataDao.getVisitorList(0,(new Date()).getTime()/1000);
                System.out.println(beaconBeanList.size());
                Iterator<BeaconBean> iterator = beaconBeanList.iterator();
                while (iterator.hasNext()){
                    BeaconBean beacon = iterator.next();
                    if (!totalList.contains(beacon)){
                        totalList.add(beacon);
                    }
                }

                List<BeaconBean> beaconBeanList1 = dataDao.getVisitorList((new Date()).getTime()/1000-2*60,(new Date()).getTime()/1000);
                Iterator<BeaconBean> iterator1 = beaconBeanList1.iterator();
                while (iterator1.hasNext()){
                    BeaconBean beacon = iterator1.next();
                    if (!currentList.contains(beacon) && beacon.getFlag()==0){
                        currentList.add(beacon);
                    }else if (currentList.size()>0 && currentList.contains(beacon) && beacon.getFlag()==(1)){
                        currentList.remove(beacon);
                    }
                }

                if (currentList.size()<=0){
                    handler.sendEmptyMessage(1);
                }

                handler.sendEmptyMessage(0);

                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}