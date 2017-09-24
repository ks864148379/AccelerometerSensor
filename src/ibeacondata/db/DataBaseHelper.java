package ibeacondata.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LK on 2016/9/30.
 */
 public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DataBaseHelper";
    private static final String DB_NAME = "IBeaconData.db";
    private static volatile DataBaseHelper instance = null;
    public static final String T_BEACON ="t_beacon" ;//上传数据
    public static final String T_BEFORE = "t_before";//本地处理数据
    public static final String T_AFTER = "t_after";//
    public static final String T_LOCATION = "t_location";
    public static final String ID = "id";
    public static final String DEVICEID = "device_id";
    public static final String MACID ="mac_id";
    public static final String UUID ="uuid";
    public static final String MAJOR ="major";
    public static final String MINOR = "minor";
    public static final String RSSI ="rssi";
    public static final String DISTANCE = "distance";
    public static final String COLLECTIME = "collectTime";
    public static final String COLLECTIMESTRING ="collectTimeString";
    public static final String TIME = "time";
    public static final String FLAG = "flag";//0进场，1出场，2在区域内
    public static final String BUILDING = "building";
    public static final String FLOOR = "floor";
    public static final String POSITION_X = "position_x";
    public static final String POSITION_Y = "position_y";
    public static final String SPOTNAME = "spotName";
    public static final String SPOTID = "spotId";
    /*public static final String CREATE_BEACON_TABLE = "create table "+ T_BEACON +" ( "
            + ID +" integer primary key autoincrement, " +
            DEVICEID +" text, "+
            MACID +" text, "+
            UUID +" integer, "+
            MAJOR +" integer, "+
            MINOR +" integer, "+
            RSSI +" integer, "+
            DISTANCE +" double, "+
            COLLECTIME +" long, "+
            FLAG + " integer"+ ")";
    public static final String CREATE_BEFORE_TABLE = "create table "+T_BEFORE+" ( "
            + ID +" integer primary key autoincrement, " +
            DEVICEID +" text, "+
            MACID +" text, "+
            UUID +" integer, "+
            MAJOR +" integer, "+
            MINOR +" integer, "+
            RSSI +" integer, "+
            DISTANCE +" double, "+
            COLLECTIME +" long, "+
            FLAG + " integer"+ ")";
    public static final String CREATE_AFTER_TABLE = "create table "+T_AFTER+" ( "
            + ID +" integer primary key autoincrement, " +
            DEVICEID +" text, "+
            MACID +" text, "+
            UUID +" integer, "+
            MAJOR +" integer, "+
            MINOR +" integer, "+
            RSSI +" integer, "+
            DISTANCE +" double, "+
            COLLECTIME +" long, "+
            FLAG + " integer"+ ")";*/

    public static final String CREATE_BEACON_TABLE = "create table "+ T_BEACON +" ( "
            + ID +" integer primary key autoincrement, " +
            DEVICEID +" text, "+
            MACID +" text, "+
            UUID +" integer, "+
            MAJOR +" integer, "+
            MINOR +" integer, "+
            RSSI +" integer, "+
            DISTANCE +" double, "+
            COLLECTIME +" long, "+
            FLAG + " integer,"+
            TIME+ " text"+")";
    public static final String CREATE_BEFORE_TABLE = "create table "+T_BEFORE+" ( "
            + ID +" integer primary key autoincrement, " +
            DEVICEID +" text, "+
            MACID +" text, "+
            UUID +" integer, "+
            MAJOR +" integer, "+
            MINOR +" integer, "+
            RSSI +" integer, "+
            DISTANCE +" double, "+
            COLLECTIME +" long, "+
            FLAG + " integer,"+
            TIME+ " text"+")";
    public static final String CREATE_AFTER_TABLE = "create table "+T_AFTER+" ( "
            + ID +" integer primary key autoincrement, " +
            DEVICEID +" text, "+
            MACID +" text, "+
            UUID +" integer, "+
            MAJOR +" integer, "+
            MINOR +" integer, "+
            RSSI +" integer, "+
            DISTANCE +" double, "+
            COLLECTIME +" long, "+
            FLAG + " integer,"+
            TIME+ " text"+")";

    public static final String CREATE_LOCATION_TABLE = "create table "+T_LOCATION+" ( "
            + ID +" integer primary key autoincrement, " +
            DEVICEID +" text, "+
            BUILDING +" text, "+
            FLOOR +" integer, "+
            POSITION_X + " double,"+
            POSITION_Y + " double,"+
            SPOTNAME + " text,"+
            SPOTID + " text"+")";

    public static DataBaseHelper getInstance(Context context){
        if (instance == null){
            synchronized (DataBaseHelper.class){
                if (instance ==null){
                    DatabaseContext dbContext = new DatabaseContext(context);
                    instance = new DataBaseHelper(dbContext);
                }
            }
        }
        return instance;
    }

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BEACON_TABLE);
        db.execSQL(CREATE_BEFORE_TABLE);
        db.execSQL(CREATE_AFTER_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase open(){
        return getReadableDatabase();
    }
    public void close(){
        super.close();
    }
}
