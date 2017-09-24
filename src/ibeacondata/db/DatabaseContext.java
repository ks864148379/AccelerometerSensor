package ibeacondata.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by LK on 2016/9/29.
 */
public class DatabaseContext  extends ContextWrapper{
    /**
     * 构造函数
     * @param base
     */
    public DatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        boolean sdExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (!sdExist){
            Log.e("SD卡管理:","SD卡不存在，请加载SD卡");
            return null;
        }else {
            String dbDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            dbDir+= "/national_center";//数据库路径
            String dbPatch = dbDir+"/"+name;
            File dirFile = new File(dbDir);
            if (!dirFile.exists())
                dirFile.mkdirs();
            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            //判断文件是否存在，不存在则创建文件
            File dbFile = new File(dbPatch);
            if(!dbFile.exists()){
                try {
                    isFileCreateSuccess = dbFile.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                isFileCreateSuccess = true;
            }

            if (isFileCreateSuccess){
                return  dbFile;
            }else {
                return null;
            }
        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name),null);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name),null);
    }
}
