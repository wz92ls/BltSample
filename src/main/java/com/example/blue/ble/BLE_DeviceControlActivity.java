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
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.blue.R;
import com.example.blue.https.HttpClient;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class BLE_DeviceControlActivity extends Activity {
    private final static String TAG = BLE_DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private BLE_BluetoothLeService mBleBluetoothLeService;
    private boolean mConnected = false;
    private String strcmd="None";
    
    EditText edtSend;
	ScrollView svResult;
	Button btnSend,btndeviceSend;
    // Code to manage Service lifecycle.
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
                btnSend.setEnabled(false);
                clearUI();
            }else if(BLE_BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) //可以开始干活了
            {
            	mConnected = true;
            	mDataField.setText("");
            	ShowDialog();
            	btnSend.setEnabled(true);
            	btndeviceSend.setEnabled(true);
            	Log.e(TAG, "In what we need");
            	invalidateOptionsMenu();
            }else if (BLE_BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { //收到数据
            	Log.e(TAG, "RECV DATA");
//            	final String data = intent.getStringExtra(BLE_BluetoothLeService.EXTRA_DATA);
                final byte[] byteArray = intent.getByteArrayExtra(BLE_BluetoothLeService.BYTEEXTRA_DATA);
                Log.w(TAG,  hexBytes2Str(byteArray));
                if(strcmd=="deviceSend")
                {
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                String result= HttpClient.Https_gongyitech.Post_deviceReturn(hexBytes2Str(byteArray));
                                Log.w(TAG,result);
                                strcmd="None";
                            } catch (Exception e) {
                                e.printStackTrace();
                                strcmd="None";
                            }
                        }
                    }).start();
                }

            	if (byteArray != null) {
                	if (mDataField.length() > 500)
                		mDataField.setText("");
                    mDataField.append(new String(byteArray));
                    svResult.post(new Runnable() {
            			public void run() {
            				svResult.fullScroll(ScrollView.FOCUS_DOWN);
            			}
            		});
                }
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

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        mDataField = (TextView) findViewById(R.id.data_value);
        edtSend = (EditText) this.findViewById(R.id.edtSend);
        edtSend.setText("www.jnhuamao.cn");
        svResult = (ScrollView) this.findViewById(R.id.svResult);
        
        btnSend = (Button) this.findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new ClickEvent());
		btnSend.setEnabled(false);

        btndeviceSend= (Button) this.findViewById(R.id.mt_deviceSend);
        btndeviceSend.setOnClickListener(new ClickEvent());
        btndeviceSend.setEnabled(true);


        getActionBar().setTitle(mDeviceName);
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
			if (v == btnSend) {
				if(!mConnected) return;
				
				if (edtSend.length() < 1) {
					Toast.makeText(BLE_DeviceControlActivity.this, "请输入要发送的内容", Toast.LENGTH_SHORT).show();
					return;
				}
				mBleBluetoothLeService.WriteValue(edtSend.getText().toString());
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

		}

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
        String ret="";
        int l=src.length;
        for (int i = 0; i < l; i++)
        {
            int tt= src[i]&0xFF;
            if(ret!="")
                ret=ret+","+Integer.toString(tt);
            else
                ret=Integer.toString(tt);
        }
        return ret;
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
