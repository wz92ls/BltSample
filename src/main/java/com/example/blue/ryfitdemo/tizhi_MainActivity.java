package com.example.blue.ryfitdemo;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.Button;
import com.chronocloud.ryfibluetoothlibrary.BluetoothDeviceOpration;
import com.chronocloud.ryfibluetoothlibrary.listener.BlueScaleCallBack;
import com.example.blue.R;

import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

public class tizhi_MainActivity extends Activity {
	 private LeDeviceListAdapter mLeDeviceListAdapter;
//    private BluetoothAdapter mBluetoothAdapter;
     private boolean mScanning;
	 private Handler mHandler;
	 private BluetoothDeviceOpration mDeviceOpration=null;
	 private Context mContext;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
	private Button scan_button,stop_button;
	ListView lv ;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getActionBar().setTitle(R.string.title_devices);
		setContentView(R.layout.tizhi_activity_main);
		mContext=this;
		mHandler = new Handler();
		//使用这个检查，以确定BLE是否支持在设备上。然后，您可以 
        //选择地禁用BLE相关的功能。
		scan_button=(Button)findViewById(R.id.tz_scan);
		stop_button=(Button)findViewById(R.id.tz_stop);
		lv = (ListView)findViewById(R.id.tz_listview);
		stop_button.setEnabled(false);
		scan_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//start to sync data
				mLeDeviceListAdapter.clear();
				scanLeDevice(true);
				stop_button.setEnabled(true);
				scan_button.setEnabled(false);
			}
		});
		stop_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//start to sync data
				scanLeDevice(false);
				stop_button.setEnabled(false);
				scan_button.setEnabled(true);
			}
		});

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "您的设备不支持BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
      //初始化一个蓝牙适配器。对于API级别18以上，得到一个参考
        // BluetoothAdapter通过BluetoothManager。
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        // 如果检查蓝牙设备支持的。
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mDeviceOpration=new BluetoothDeviceOpration(mContext);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
		ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);     
        manager.killBackgroundProcesses(getPackageName());
	}
	 @Override
	    protected void onResume() {
	        super.onResume();

	        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
	        // fire an intent to display a dialog asking the user to grant permission to enable it.
	        final BluetoothManager bluetoothManager =
	                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
	        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
	        if (!mBluetoothAdapter.isEnabled()) {
	            if (!mBluetoothAdapter.isEnabled()) {
	                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	            }
	        }

	        // Initializes list view adapter.
	        mLeDeviceListAdapter = new LeDeviceListAdapter();
	        lv.setAdapter(mLeDeviceListAdapter);
		 	lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@SuppressWarnings("unchecked")
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
					if (device == null) return;
					final Intent intent = new Intent();
					intent.setClass(tizhi_MainActivity.this, DeviceControlActivity.class);
					intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
					intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
					DeviceControlActivity.mBluetoothDevice=device;
					if (mScanning) {
						mDeviceOpration.stopScan();
						mScanning = false;
					}
					startActivity(intent);
				}
			});
	        
	    }

	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.main, menu);
	        if (!mScanning) {
	            menu.findItem(R.id.menu_stop).setVisible(false);
	            menu.findItem(R.id.menu_scan).setVisible(true);
	            menu.findItem(R.id.menu_refresh).setActionView(null);
	        } else {
	            menu.findItem(R.id.menu_stop).setVisible(true);
	            menu.findItem(R.id.menu_scan).setVisible(false);
	            menu.findItem(R.id.menu_refresh).setActionView(
	                    R.layout.tizhi_actionbar_indeterminate_progress);
	        }
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.menu_scan:
	                mLeDeviceListAdapter.clear();
	                scanLeDevice(true);
	                break;
	            case R.id.menu_stop:
	                scanLeDevice(false);
	                break;
	        }
	        return true;
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();
	        scanLeDevice(false);
	        mLeDeviceListAdapter.clear();
	    }
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // User chose not to enable Bluetooth.
	        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
	            finish();
	            return;
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	 
//	    @Override
	    protected void onListItemClick(ListView l, View v, int position, long id) {
	        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
	        if (device == null) return;
	        final Intent intent = new Intent(this, DeviceControlActivity.class);
	        
	        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
	        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
	        DeviceControlActivity.mBluetoothDevice=device;
	        if (mScanning) {
	        	mDeviceOpration.stopScan();
	            mScanning = false;
	        }
	        startActivity(intent);
	    }

	    private void scanLeDevice(final boolean enable) {
	        if (enable) {
	            // Stops scanning after a pre-defined scan period.
	            mHandler.postDelayed(new Runnable() {
	                @Override
	                public void run() {
	                	if(mScanning)
	                	{
	                		mScanning = false;
	                		mDeviceOpration.stopScan();
//	                		invalidateOptionsMenu();
	                	}
	                }
	            }, SCAN_PERIOD);

	            mScanning = true;
	            //F000E0FF-0451-4000-B000-000000000000
	            mDeviceOpration.startScan(mBlueScaleCallBack);
	        } else {
	            mScanning = false;
	            mDeviceOpration.stopScan();
	        }
//	        invalidateOptionsMenu();
	    }

	// 设备连接返回.
	BlueScaleCallBack mBlueScaleCallBack=new BlueScaleCallBack() {
			
			@Override
			public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
		                @Override
		                public void run() {
		                	device.getUuids();
		                	mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
		                	mLeDeviceListAdapter.notifyDataSetChanged();
		                }
		            });
			}
		};
	 
	 
	  // Adapter for holding devices found through scanning.
	    private class LeDeviceListAdapter extends BaseAdapter {
	        private ArrayList<BluetoothDevice> mLeDevices;
	        private ArrayList<Integer> rssis;
	        private ArrayList<byte[]> bRecord;
	        private LayoutInflater mInflator;

	        public LeDeviceListAdapter() {
	            super();
	            mLeDevices = new ArrayList<BluetoothDevice>();
	            rssis = new ArrayList<Integer>();
	            bRecord = new ArrayList<byte[]>();
	            mInflator = tizhi_MainActivity.this.getLayoutInflater();
	        }

	        public void addDevice(BluetoothDevice device, int rs, byte[] record) {
	            if(!mLeDevices.contains(device)) {
	                mLeDevices.add(device);
	                rssis.add(rs);
	                bRecord.add(record);
	            }
	        }

	        public BluetoothDevice getDevice(int position) {
	            return mLeDevices.get(position);
	        }
	        
	        public void clear() {
	            mLeDevices.clear();
	            rssis.clear();
	            bRecord.clear();
	        }

	        @Override
	        public int getCount() {
	            return mLeDevices.size();
	        }

	        @Override
	        public Object getItem(int i) {
	            return mLeDevices.get(i);
	        }

	        @Override
	        public long getItemId(int i) {
	            return i;
	        }

	        @Override
	        public View getView(int i, View view, ViewGroup viewGroup) {
	            ViewHolder viewHolder;
	            // General ListView optimization code.
	            if (view == null) {
	                view = mInflator.inflate(R.layout.tizhi_listitem_device, null);
	                viewHolder = new ViewHolder();
	                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
	                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
	                view.setTag(viewHolder);
	            } else {
	                viewHolder = (ViewHolder) view.getTag();
	            }

	            BluetoothDevice device = mLeDevices.get(i);
	            final String deviceName = device.getName();
	            if (deviceName != null && deviceName.length() > 0)
	                viewHolder.deviceName.setText(deviceName);
	            else
	                viewHolder.deviceName.setText(R.string.unknown_device);
	            viewHolder.deviceAddress.setText(device.getAddress() + "  RSSI:" + String.valueOf(rssis.get(i)));
	            //viewHolder.deviceAddress.setText(ByteToString(bRecord.get(i)));

	            return view;
	        }
	    }
	    static class ViewHolder {
	        TextView deviceName;
	        TextView deviceAddress;
	    }
}
