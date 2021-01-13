/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.blue.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.blue.MQTTService;
import com.example.blue.MyServiceConnection;
import com.example.blue.R;
import com.example.blue.https.HttpClient;

import java.util.Objects;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class BLE_DeviceControlActivity extends Activity implements MQTTService.IGetMessageCallBack {
    private final static String TAG = BLE_DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mDataField, http_result;
//    private String mDeviceName;
    private String mDeviceAddress;
    private BLE_BluetoothLeService mBleBluetoothLeService;
    private boolean mConnected = false;
    private String strcmd="None";
    static byte[] oldbyteArray;
    EditText edtSend;
	ScrollView svResult ,svResult2;
	Button btnSend,btndeviceSend,mt_shengyinkai,mt_shengyinguan,mt_deviceControl1,mt_deviceControl2,mt_deviceControl3,mt_deviceControl4;
    // Code to manage Service lifecycle.


    private MyServiceConnection serviceConnection;
//    private MQTTService mqttService;
    @Override
    public void setMessage(String message) {
        MQTTService mqttService = serviceConnection.getMqttService();
        mqttService.toCreateNotification(message);
    }
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Object model = (Object) msg.obj;
//            http_result.append(model + "\n");
//            svResult2.fullScroll(ScrollView.FOCUS_DOWN);
//        }
//    };

    final private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Object model = msg.obj;
            http_result.append(model + "\n");
            svResult2.fullScroll(ScrollView.FOCUS_DOWN);
            return false;
        }
    });

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBleBluetoothLeService = ((BLE_BluetoothLeService.LocalBinder) service).getService();
            if (!mBleBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            
            Log.e(TAG, "mBluetoothLeService is okay");
            // Automatically connects to the device upon successful start-up initialization.
            //mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BLE_BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {  //连接成功
            	Log.e(TAG, "Only gatt, just wait");
            } else if (BLE_BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { //断开连接
                mConnected = false;
                invalidateOptionsMenu();
                //btnSend.setEnabled(false);
                mt_shengyinkai.setEnabled(false);
                mt_shengyinguan.setEnabled(false);
                btndeviceSend.setEnabled(false);

                mt_deviceControl1.setEnabled(false);
                mt_deviceControl2.setEnabled(false);
                mt_deviceControl3.setEnabled(false);
                mt_deviceControl4.setEnabled(false);
                clearUI();
            }else if(BLE_BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) //可以开始干活了
            {
            	mConnected = true;
            	mDataField.setText("");
                http_result.setText("");
            	ShowDialog();
            	//btnSend.setEnabled(true);
            	btndeviceSend.setEnabled(true);
                mt_shengyinkai.setEnabled(true);
                mt_shengyinguan.setEnabled(true);

                mt_deviceControl1.setEnabled(true);
                mt_deviceControl2.setEnabled(true);
                mt_deviceControl3.setEnabled(true);
                mt_deviceControl4.setEnabled(true);

            	Log.e(TAG, "In what we need");
            	invalidateOptionsMenu();
            }else if (BLE_BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { //收到数据
            	Log.e(TAG, "RECV DATA");
//            	final String data = intent.getStringExtra(BLE_BluetoothLeService.EXTRA_DATA);
                final byte[] byteArray = intent.getByteArrayExtra(BLE_BluetoothLeService.BYTEEXTRA_DATA);
                Log.w(TAG,  hexBytes2Str(byteArray));
                Log.w(TAG,  strcmd);
                if(oldbyteArray==byteArray)
                {
                    return;
                }
                oldbyteArray=byteArray;
                if(strcmd.equals("deviceSend")&&byteArray[0]!=13)
                {
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                String result= HttpClient.Https_gongyitech.Post_deviceReturn(hexBytes2Str(byteArray));
                                Log.w(TAG,result);
                                Message message = handler.obtainMessage(22, result);
                                handler.sendMessage(message);
                                strcmd="None";
                            } catch (Exception e) {
                                e.printStackTrace();
                                strcmd="None";
                            }
                        }
                    }).start();
                }
                if(strcmd.equals("mt_shengyinkai")&&byteArray[0]!=13)
                {
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                String result= HttpClient.Https_gongyitech.Post_testingr(hexBytes2Str(byteArray));
                                strcmd="None";
                                Log.w(TAG,result);
                                strcmd="None";
                                Message message = handler.obtainMessage(22, result);
                                handler.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                strcmd="None";
                            }
                        }
                    }).start();
                }
                if(strcmd.equals("mt_shengyinguan")&&byteArray[0]!=13)
                {
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                String result= HttpClient.Https_gongyitech.Post_testingr(hexBytes2Str(byteArray));
                                strcmd="None";
                                Log.w(TAG,result);
                                Message message = handler.obtainMessage(22, result);
                                handler.sendMessage(message);

                            } catch (Exception e) {
                                e.printStackTrace();
                                strcmd="None";
                            }
                        }
                    }).start();
                }
                if(strcmd.equals("mt_deviceControl")&&byteArray[0]!=13)
                {
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
//                                HttpClient2 h2 = new HttpClient2();
//                                HttpClient2.Https_gongyitech hs=h2.new Https_gongyitech();
//                                String result=hs.Post_correspond(hexBytes2Str(byteArray));

                                String result= HttpClient.Https_gongyitech.Post_correspond(hexBytes2Str(byteArray));
                                strcmd="mt_deviceControl";
                                Log.w(TAG,result);
                                if(!result.equals("error"))
                                {
                                    String[] bufsrc=result.split("[_]");
                                    Log.w(TAG,bufsrc[0]);
                                    if(bufsrc[0].equals("0"))
                                    {
                                        Log.w(TAG,bufsrc[1]);
                                        byte[] ret= hexStr2Bytes(bufsrc[1]);
                                        mBleBluetoothLeService.WriteValue(ret);
                                    }
                                }

                                Message message = handler.obtainMessage(22, result);
                                handler.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                                strcmd="None";
                            }
                        }
                    }).start();
                }
                if (mDataField.length() > 500)
                    mDataField.setText("");
                mDataField.append(hexBytes2Str(byteArray)+"\n");
                svResult.post(new Runnable() {
                    public void run() {
                        svResult.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }
        }
    };

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {                                        //初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ble_gatt_services_characteristics);

        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(BLE_DeviceControlActivity.this);
        Intent intent2 = new Intent(this, MQTTService.class);
        bindService(intent2, serviceConnection, Context.BIND_AUTO_CREATE);

        final Intent intent = getIntent();
        String mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        mDataField = (TextView) findViewById(R.id.data_value);
        http_result=(TextView) findViewById(R.id.http_result);
        edtSend = (EditText) this.findViewById(R.id.edtSend);
        edtSend.setText("");
        svResult = (ScrollView) this.findViewById(R.id.svResult);
        svResult2=(ScrollView) this.findViewById(R.id.svResult2);
//        btnSend = (Button) this.findViewById(R.id.btnSend);
//		btnSend.setOnClickListener(new ClickEvent());
//		btnSend.setEnabled(false);

        btndeviceSend= (Button) this.findViewById(R.id.mt_deviceSend);
        btndeviceSend.setOnClickListener(new ClickEvent());
        btndeviceSend.setEnabled(false);

        mt_shengyinkai=(Button) this.findViewById(R.id.mt_shengyinkai);
        mt_shengyinkai.setOnClickListener(new ClickEvent());
        mt_shengyinkai.setEnabled(false);


        mt_shengyinguan=(Button) this.findViewById(R.id.mt_shengyinguan);
        mt_shengyinguan.setOnClickListener(new ClickEvent());
        mt_shengyinguan.setEnabled(false);


        mt_deviceControl1=(Button) this.findViewById(R.id.deviceControl1);
        mt_deviceControl2=(Button) this.findViewById(R.id.deviceControl2);
        mt_deviceControl3=(Button) this.findViewById(R.id.deviceControl3);
        mt_deviceControl4=(Button) this.findViewById(R.id.deviceControl4);

        mt_deviceControl1.setOnClickListener(new ClickEvent());
        mt_deviceControl2.setOnClickListener(new ClickEvent());
        mt_deviceControl3.setOnClickListener(new ClickEvent());
        mt_deviceControl4.setOnClickListener(new ClickEvent());

        mt_deviceControl1.setEnabled(false);
        mt_deviceControl2.setEnabled(false);
        mt_deviceControl3.setEnabled(false);
        mt_deviceControl4.setEnabled(false);


        Objects.requireNonNull(getActionBar()).setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(BLE_DeviceControlActivity.this, BLE_BluetoothLeService.class);

        Log.d(TAG, "Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //this.unregisterReceiver(mGattUpdateReceiver);
        //unbindService(mServiceConnection);

        unbindService(serviceConnection);
        super.onDestroy();

        if(mBleBluetoothLeService != null)
        {
        	mBleBluetoothLeService.close();
        	mBleBluetoothLeService = null;
        }
        Log.d(TAG, "We are in destroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                              //点击按钮
        switch(item.getItemId()) {
            case R.id.menu_connect:
                Log.d(TAG, "Trying to create a new connection."+mDeviceAddress);
                mBleBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBleBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
            	if(mConnected)
            	{
            		mBleBluetoothLeService.disconnect();
            		mConnected = false;
            	}
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void ShowDialog()
    {
    	Toast.makeText(this, "连接成功，现在可以正常通信！", Toast.LENGTH_SHORT).show();
    }

 // 按钮事件
	class ClickEvent implements View.OnClickListener {
        @Override
		public void onClick(View v) {
			if(v == btnSend) {
				if(!mConnected) return;
				
				if (edtSend.length() < 1) {
					Toast.makeText(BLE_DeviceControlActivity.this, "请输入要发送的内容", Toast.LENGTH_SHORT).show();
					return;
				}
				mBleBluetoothLeService.WriteValue(edtSend.getText().toString());
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                if(imm.isActive())
					imm.hideSoftInputFromWindow(edtSend.getWindowToken(), 0);
				//todo Send data
			}
			if(v == btndeviceSend){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            String result=HttpClient.Https_gongyitech.Post_deviceSend();
                            Log.w(TAG,result);
                            byte[] ret= hexStr2Bytes(result);
                            mBleBluetoothLeService.WriteValue(ret);
                            strcmd="deviceSend";
                        } catch (Exception e) {
                            e.printStackTrace();
                            strcmd="None";
                        }
                    }
                }).start();
            }
            if(v == mt_shengyinkai){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            String type="3";
                            String stauts="2";
                                        //除臭 水温 水压 喷嘴 风温 座温 节电 静音 夜灯 按摩 自动冲水 关闭盖圈 翻盖、翻圈 翻盖 冲大便 冲小便 智能翻盖翻圈
                            String value="0,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0";
                            String result=HttpClient.Https_gongyitech.Post_deviceControl(type,stauts,value);
                            Log.w(TAG,result);
                            byte[] ret= hexStr2Bytes2(result);
                            mBleBluetoothLeService.WriteValue(ret);
                            strcmd="mt_shengyinkai";
                        } catch (Exception e) {
                            e.printStackTrace();
                            strcmd="None";
                        }
                    }
                }).start();
            }
            if(v == mt_shengyinguan){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            String type="3";
                            String stauts="2";
                            //除臭 水温 水压 喷嘴 风温 座温 节电 静音 夜灯 按摩 自动冲水 关闭盖圈 翻盖、翻圈 翻盖 冲大便 冲小便 智能翻盖翻圈
                            String value="0,0,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0";
                            String result=HttpClient.Https_gongyitech.Post_deviceControl(type,stauts,value);
                            Log.w(TAG,result);
                            byte[] ret= hexStr2Bytes2(result);
                            mBleBluetoothLeService.WriteValue(ret);
                            strcmd="mt_shengyinguan";
                        } catch (Exception e) {
                            e.printStackTrace();
                            strcmd="None";
                        }
                    }
                }).start();
            }
            if(v == mt_deviceControl1){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            String type="1";
                            String result=HttpClient.Https_gongyitech.Post_autotestingq(type);
                            Log.w(TAG,result);
                            byte[] ret= hexStr2Bytes(result);
                            mBleBluetoothLeService.WriteValue(ret);
                            strcmd="mt_deviceControl";
                        } catch (Exception e) {
                            e.printStackTrace();
                            strcmd="None";
                        }
                    }
                }).start();
            }
            if(v == mt_deviceControl2){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            String type="2";
                            String result=HttpClient.Https_gongyitech.Post_autotestingq(type);
                            Log.w(TAG,result);
                            byte[] ret= hexStr2Bytes(result);
                            mBleBluetoothLeService.WriteValue(ret);
                            strcmd="mt_deviceControl";
                        } catch (Exception e) {
                            e.printStackTrace();
                            strcmd="None";
                        }
                    }
                }).start();
            }
            if(v == mt_deviceControl3){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            String type="3";
                            String result=HttpClient.Https_gongyitech.Post_autotestingq(type);
                            Log.w(TAG,result);
                            byte[] ret= hexStr2Bytes(result);
                            mBleBluetoothLeService.WriteValue(ret);
                            strcmd="mt_deviceControl";
                        } catch (Exception e) {
                            e.printStackTrace();
                            strcmd="None";
                        }
                    }
                }).start();
            }
            if(v == mt_deviceControl4){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            String type="4";
                            String result=HttpClient.Https_gongyitech.Post_autotestingq(type);
                            Log.w(TAG,result);
                            byte[] ret= hexStr2Bytes(result);
                            mBleBluetoothLeService.WriteValue(ret);
                            strcmd="mt_deviceControl";
                        } catch (Exception e) {
                            e.printStackTrace();
                            strcmd="None";
                        }
                    }
                }).start();
            }
		}

	}
    private static byte[] hexStr2Bytes2(String src)
    {
        String[] bufsrc=src.split("[,]");
        int l=bufsrc.length;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            String[] bufsrc2=bufsrc[i].split("[:]");
            String str=bufsrc2[1];
            if(bufsrc2[1].contains("}")) {
                 str = bufsrc2[1].substring(0, bufsrc2[1].indexOf("}"));
            }
            int tt= Integer.parseInt(str);
            byte t=(byte)tt;
            ret[i] = t;
        }
        return ret;
    }
    private static byte[] hexStr2Bytes(String src)
    {
        String[] bufsrc=src.split("[,]");
        int l=bufsrc.length;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            int tt= Integer.parseInt(bufsrc[i]);
            byte t=(byte)tt;
            ret[i] = t;
        }
        return ret;
    }
    private static String hexBytes2Str(byte[] src)
    {
        StringBuilder ret= new StringBuilder();
        for (byte b : src) {
            int tt = b & 0xFF;
            if (!ret.toString().equals(""))
                ret.append(",").append(tt);
            else
                ret = new StringBuilder(Integer.toString(tt));
        }
        return ret.toString();
    }
    private static IntentFilter makeGattUpdateIntentFilter() {                        //注册接收的事件
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLE_BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLE_BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLE_BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLE_BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }
}
