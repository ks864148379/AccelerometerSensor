package ibeacondata.activity;

import java.io.File;
import java.io.FilenameFilter;

import android.app.Application;

public class PublicData extends Application {
	private static PublicData self;
	@Override
	public void onCreate() {
		super.onCreate();
		self = this;
	}

	public static PublicData getInstance() {
		return self;
	}
	public String[] getFoldFiles(String absFilePath) {
		File file = new File(absFilePath);
		if (file == null || file.isDirectory() == false)
			return null;
		// 过滤，获取特定命名文件，过滤掉DB临时文件和存放公共数据的BaseData
		String[] arrayStr = file.list(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String name) {
				// TODO Auto-generated method stub
				return name.endsWith(".db") && (!name.equals("BaseData.db"));
			}
		});
		if (arrayStr == null)
			return null;
		for (int i = 0; i < arrayStr.length; i++) {

			arrayStr[i] = arrayStr[i].substring(0, arrayStr[i].indexOf("."));
		}
		return arrayStr;
	}







}
