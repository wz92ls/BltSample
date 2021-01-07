package com.example.blue.ryfitdemo.util;

import android.app.Application;
import android.util.Log;
import com.chronocloud.ryfibluetoothlibrary.BluetoothOpration;

/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CreateTime:Jul 4, 2014 10:26:27 AM
 * @description Class description
 */
public class MyApplication extends Application {

	public static BluetoothOpration _BluetoothOpration;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		_BluetoothOpration=new BluetoothOpration(this);
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.i("info", "demo onLowMemory..");
		_BluetoothOpration.disconnect();
		_BluetoothOpration.onDestroy();
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i("info", "demo onTerminate..");
	}
}
