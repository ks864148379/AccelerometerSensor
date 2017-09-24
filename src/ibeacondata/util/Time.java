package ibeacondata.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LK on 2016/10/31.
 */
public class Time {

    /**
     * 将时间转换为时间戳
     * @param s
     * @return
     * @throws ParseException
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * 将时间戳转化为时间
     * @param s
     * @return
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String getTime(){
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(new Date());
    }

    public  static String getDay(){
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(new Date());
    }
    public static String getHour(){
        SimpleDateFormat sdf =  new SimpleDateFormat("HH");
        return  sdf.format(new Date());
    }
    public static void main(String[] args) throws ParseException {
        System.out.println("当前时间为："+getTime());
        System.out.println("当前时间戳："+dateToStamp(getTime()));
        System.out.println(dateToStamp("2016-10-31 14:41:12"));
        System.out.println("当前时间:"+stampToDate(dateToStamp(getTime())));
        System.out.println("当前Hour:"+getHour());
    }
}
