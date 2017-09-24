package ibeacondata.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ibeacondata.bean.BeaconBean;
import ibeacondata.bean.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LK on 2016/10/6.
 */
public class DataDao {
    private Context context;
    private DataBaseHelper dbHelper;
    private static volatile  DataDao instance = null;

    private DataDao(Context context){
        this.context = context;
        dbHelper = DataBaseHelper.getInstance(context);
    }

    public static DataDao getInstance(Context context){
        if(instance == null){
            synchronized (DataDao.class){
                if(instance == null){
                    instance = new DataDao(context);
                }
            }

        }
        return instance;
    }

    public void addBeaconData(BeaconBean beaconBean,String table){
        synchronized (this){
            SQLiteDatabase db = dbHelper.open();
            ContentValues values = new ContentValues();
            values.put(DataBaseHelper.DEVICEID,beaconBean.getDevice_id());
            values.put(DataBaseHelper.MACID,beaconBean.getMac_id());
            values.put(DataBaseHelper.UUID,beaconBean.getUuid());
            values.put(DataBaseHelper.MAJOR,beaconBean.getMajor());
            values.put(DataBaseHelper.MINOR,beaconBean.getMinor());
            values.put(DataBaseHelper.RSSI,beaconBean.getRssi());
            values.put(DataBaseHelper.DISTANCE,beaconBean.getDistance());
            values.put(DataBaseHelper.COLLECTIME,beaconBean.getCollectime());
            values.put(DataBaseHelper.FLAG,beaconBean.getFlag());
            values.put(DataBaseHelper.TIME,beaconBean.getTime());
            db.insert(table, null, values);
            dbHelper.close();
        }
    }

    public List<BeaconBean> getVisitorList(long startTime,long endTime){
        List<BeaconBean> result = new ArrayList<>();
        synchronized (this){
            SQLiteDatabase db = dbHelper.open();
            Cursor cursor =db.query(DataBaseHelper.T_BEFORE, null, "collectTime >? and collectTime <?", new String[]{String.valueOf(startTime), String.valueOf(endTime)}, null, null, null);
            while (cursor.moveToNext()){
                BeaconBean beaconBean = new BeaconBean(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getString(3),cursor.getInt(4),
                        cursor.getInt(5),cursor.getInt(6),cursor.getDouble(7),
                        cursor.getLong(8),cursor.getInt(9),cursor.getString(10));
                result.add(beaconBean);
            }
            cursor.close();
            dbHelper.close();
        }
        return result;
    }

    public List<BeaconBean> getBeaconData(String table){
        List<BeaconBean> result = new ArrayList<>();
        synchronized (this){
            SQLiteDatabase db = dbHelper.open();
            Cursor cursor = db.query(table, null, null, null, null, null, null);
            while (cursor.moveToNext()){
                BeaconBean beaconBean = new BeaconBean(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getString(3),cursor.getInt(4),
                        cursor.getInt(5),cursor.getInt(6),cursor.getDouble(7),
                        cursor.getLong(8),cursor.getInt(9),cursor.getString(10));
                result.add(beaconBean);
            }
            cursor.close();
            dbHelper.close();
        }
        return result;
    }

    public void deleteBeaconData(String table){
        synchronized (this){
            SQLiteDatabase db = dbHelper.open();
            db.delete(table, null, null);
            dbHelper.close();
        }
    }

    public BeaconBean queryBeacon(String macId,String table){
        BeaconBean beaconBean =null;
        synchronized (this){
            SQLiteDatabase db = dbHelper.open();
            Cursor cursor = db.query(table, null, "mac_id = ? ", new String[]{macId}, null, null, null);
            while (cursor.moveToNext()){
                beaconBean = new BeaconBean(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getString(3),cursor.getInt(4),
                        cursor.getInt(5),cursor.getInt(6),cursor.getDouble(7),
                        cursor.getLong(8),cursor.getInt(9),cursor.getString(10));
            }
            cursor.close();
            dbHelper.close();
        }
        return beaconBean;
    }


    public void deleteBeacon(String table,String macId){
        synchronized (this){
            SQLiteDatabase db = dbHelper.open();
            db.execSQL("delete from '" + table + "'  where mac_id='" + macId + "' and collectTime = (select max(collectTime) from t_beacon where mac_id= '" + macId + "')  ");
            db.close();
        }
    }

    public void updateLocationData(LocationBean locationBean){
        LocationBean location = null;
        synchronized (this){
            SQLiteDatabase db = dbHelper.open();
            Cursor cursor = db.query(DataBaseHelper.T_LOCATION,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                location = new LocationBean(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getString(3),cursor.getDouble(4),
                        cursor.getDouble(5),cursor.getString(6),cursor.getString(7));
            }
            if (location == null){
                ContentValues values = new ContentValues();
                values.put(DataBaseHelper.DEVICEID,locationBean.getDevice_id());
                values.put(DataBaseHelper.BUILDING,locationBean.getBuilding());
                values.put(DataBaseHelper.FLOOR,locationBean.getFloor());
                values.put(DataBaseHelper.POSITION_X,locationBean.getPosition_x());
                values.put(DataBaseHelper.POSITION_Y,locationBean.getPosition_y());
                values.put(DataBaseHelper.SPOTNAME,locationBean.getSpotName());
                values.put(DataBaseHelper.SPOTID,locationBean.getSpotId());
                db.insert(DataBaseHelper.T_LOCATION,null,values);
            }else {
                ContentValues values = new ContentValues();

                db.delete(dbHelper.T_LOCATION, null, null);

                values.put(DataBaseHelper.DEVICEID,locationBean.getDevice_id());
                values.put(DataBaseHelper.BUILDING,locationBean.getBuilding());
                values.put(DataBaseHelper.FLOOR,locationBean.getFloor());
                values.put(DataBaseHelper.POSITION_X,locationBean.getPosition_x());
                values.put(DataBaseHelper.POSITION_Y, locationBean.getPosition_y());
                values.put(DataBaseHelper.SPOTNAME,locationBean.getSpotName());
                values.put(DataBaseHelper.SPOTID,locationBean.getSpotId());
                //db.update(DataBaseHelper.T_LOCATION,values," device_id = ?  ",new String[]{locationBean.getDevice_id()});
                db.insert(DataBaseHelper.T_LOCATION,null,values);
            }
            cursor.close();
            dbHelper.close();
        }

    }

    /**
     * get基站位置
     * @return
     */
    public LocationBean getLoacationData(){
        LocationBean location = null;
        synchronized (this){
            SQLiteDatabase db = dbHelper.open();
            Cursor cursor = db.query(DataBaseHelper.T_LOCATION,null,null,null,null,null,null);
            while (cursor.moveToNext()){
                location = new LocationBean(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getString(3),cursor.getDouble(4),
                        cursor.getDouble(5),cursor.getString(6),cursor.getString(7));
            }
            cursor.close();
            db.close();
        }
        return location;
    }
}
