package com.example.blue.ryfitdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.chronocloud.ryfibluetoothlibrary.BluetoothOpration;
import com.chronocloud.ryfibluetoothlibrary.entity.TestDataInfo;
import com.chronocloud.ryfibluetoothlibrary.entity.User;
import com.chronocloud.ryfibluetoothlibrary.listener.BluetoothOprationCallback;
import com.example.blue.R;
import com.example.blue.ryfitdemo.entity.OperationData;
import com.example.blue.ryfitdemo.entity.TestData;
import com.example.blue.ryfitdemo.util.MyApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CareteTime:2014-2-25 下午4:03:46
 * @description Class 设备控制
 */
@SuppressLint("NewApi")
public class DeviceControlActivity extends Activity {
	private final static String TAG = DeviceControlActivity.class
			.getSimpleName();
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	public static final String NO = "no";
	public static final String ORDER = "order";
	public static final String NAME = "name";
	private String mDeviceName;
	// private String mDeviceAddress;
	private boolean mConnected = false;
	private ListView lvData;
	private Context context;
	private List<Map<String, String>> data;
	private BaseAdapter adapter;
//	private List<Map<String, String>> userData;
//	private int index = 1;
//	private EditText et_value;
//	private Button btn_send1;
	private Button btn_read, btn_write;

	private RadioGroup rg_sex;
	private TextView scale_weight, tv_mac_address;
	private EditText et_height, et_age, et_number;
	private Button btn_send;
	private LinearLayout ll_test;
	private String sex = "01";
	private Map<String, String> testMap;
	private boolean isPause = false;

	private BluetoothOpration mBluetoothOpration = null;
	public static BluetoothDevice mBluetoothDevice = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tizhi_gatt_services_characteristics);
		context = this;
		// Sets up UI references.
		initView();
		initAction();
	}

	/**
	 * 
	 * @description Method 初始化view
	 * @author Mars Jul 4, 2014 5:13:15 PM
	 */
	@SuppressLint("NewApi")
	private void initView() {
		// TODO Auto-generated method stub
		lvData = (ListView) findViewById(R.id.lv_data);
		rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
		et_height = (EditText) findViewById(R.id.et_height);
		et_age = (EditText) findViewById(R.id.et_age);
		btn_send = (Button) findViewById(R.id.btn_send);
		ll_test = (LinearLayout) findViewById(R.id.ll_test);
		scale_weight = (TextView) findViewById(R.id.scale_weight);
		tv_mac_address = (TextView) findViewById(R.id.tv_mac_address);
//		et_value = (EditText) findViewById(R.id.et_value);
//		btn_send1 = (Button) findViewById(R.id.btn_send1);
		btn_read = (Button) findViewById(R.id.btn_read);
		btn_write = (Button) findViewById(R.id.btn_write);
		et_number = (EditText) findViewById(R.id.et_number);
		data = new ArrayList<Map<String, String>>();
		adapter = new SimpleAdapter(context, data,
				android.R.layout.simple_list_item_1, new String[] { "name" },
				new int[] { android.R.id.text1 });
		lvData.setAdapter(adapter);
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		// mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
		getActionBar().setTitle(mDeviceName);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mBluetoothOpration = MyApplication._BluetoothOpration;
		mBluetoothOpration.addBluetoothOprationCallback(BOcallback);
	}

	/**
	 * 
	 * @description Method 初始化事件
	 * @author Mars Jul 4, 2014 8:16:22 PM
	 */
	private void initAction() {
		// TODO Auto-generated method stub
		// btn_send1.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// mBluetoothLeService.WriteValue(et_value.getText().toString());
		// }
		// });
		btn_read.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mBluetoothOpration.ReadNumber();
			}
		});
		btn_write.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mBluetoothOpration.writeNumber(et_number.getText().toString()
						.trim());
			}
		});

		lvData.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				userData = null;
				HashMap<String, String> map = (HashMap<String, String>) arg0
						.getAdapter().getItem(arg2);
				String order = map.get(ORDER);
				if (order.equals(OperationData.VIEW_ALL_USERS)) {
					Intent intent1 = new Intent(context, UserListActivity.class);
					startActivity(intent1);
				} else if (order.equals(OperationData.PURE_GUEST_MODE)) {
					mBluetoothOpration.pureGestMode();
				} else if (order.equals(OperationData.QUIT_PURE_GUEST_MODE)) {
					mBluetoothOpration.quitPureGuestMode();
				} else if (order.equals(OperationData.READ_MAC_ADDRESS)) {
					mBluetoothOpration.readMacAddress();
				} else if (order.equals(OperationData.RESET_SCALE_PARAM)) {
					mBluetoothOpration.resetScaleParam();
				} else if (order.equals(OperationData.ZERO)) {
					mBluetoothOpration.zero();
				}
			}
		});
		rg_sex.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int arg1) {
				// TODO Auto-generated method stub
				switch (radioGroup.getCheckedRadioButtonId()) {
				case R.id.rb_man:
					sex = "01";
					break;
				case R.id.rb_woman:
					sex = "00";
					break;
				}
			}
		});

		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String height = et_height.getText().toString();
				String age = et_age.getText().toString();
				if (mBluetoothOpration != null
						&& (!height.equals("") && !age.equals(""))
						&& (Integer.parseInt(height) >= 100 && Integer
								.parseInt(height) <= 220)
						&& (Integer.parseInt(age) >= 10 && Integer
								.parseInt(age) <= 80)) {
					mBluetoothOpration.selectUserScale("09", height, age, sex,"999");
				} else {
					Toast.makeText(context, R.string.age_height,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private List<Map<String, String>> setData(String order, String name) {
		if (data == null) {
			data = new ArrayList<Map<String, String>>();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(ORDER, order);
		map.put(NAME, name);
		data.add(map);

		return data;
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
	public boolean onOptionsItemSelected(MenuItem item) { // 点击按钮
		switch (item.getItemId()) {
		case R.id.menu_connect:
			if (mBluetoothDevice != null) {
				mBluetoothOpration.connect(mBluetoothDevice);
			}
			return true;
		case R.id.menu_disconnect:
			mBluetoothOpration.disconnect();
			return true;
		case android.R.id.home:
			if (mConnected) {
				mBluetoothOpration.disconnect();
				mConnected = false;
			}
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBluetoothOpration.removeBluetoothOprationCallback(BOcallback);
		mBluetoothOpration.disconnect();
		Log.d(TAG, "We are in destroy");
	}

	private void ShowDialog() {
		Toast.makeText(this, R.string.connection_successful, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isPause = true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isPause = false;
	}

	/**
	 * 
	 * @description Method Start Test Data Activity
	 * @author Mars 2014-3-13 下午4:32:24
	 */
	private void startTestActivity() {
		btn_send.setClickable(true);
		btn_send.setTextColor(getResources().getColor(android.R.color.white));
		Intent intent = new Intent(context, TestDataActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("testMap", (Serializable) testMap);
		intent.putExtra("bundle", bundle);
		startActivity(intent);
	}

	BluetoothOprationCallback BOcallback = new BluetoothOprationCallback() {

		@Override
		public void onWeight(int staut, double weight) {
			// TODO Auto-generated method stub
			scale_weight.setText(weight + "kg");
			if (staut == 0) {
				scale_weight.setTextColor(Color.RED);
			} else {
				scale_weight.setTextColor(Color.BLACK);
			}
		}

		@Override
		public void onUserIsExist(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpdateUser(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTestDataInfo(TestDataInfo dataInfo) {
			// TODO Auto-generated method stub
			if (isPause) {
				return;
			}
			testMap = new HashMap<String, String>();
			testMap.put(TestData.TIME, dataInfo.getTime());
			testMap.put(TestData.WEIGHT, dataInfo.getWeight());
			testMap.put(TestData.BF, dataInfo.getBf());
			testMap.put(TestData.WATRER, dataInfo.getWatrer());
			testMap.put(TestData.MUSCLE, dataInfo.getMuscle());
			testMap.put(TestData.BONE, dataInfo.getBone());
			testMap.put(TestData.BMR, dataInfo.getBmr());
			testMap.put(TestData.SFAT, dataInfo.getSfat());
			testMap.put(TestData.INFAT, dataInfo.getInfat());
			testMap.put(TestData.BODYAGE, dataInfo.getBodyage());
			startTestActivity();
		}

		@Override
		public void onSelectUserScale(List<TestDataInfo> listDataInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSelectUserScale(int staut) {
			// TODO Auto-generated method stub
			if (staut == 0) {
				btn_send.setClickable(false);
				btn_send.setTextColor(getResources().getColor(
						android.R.color.black));
				Toast.makeText(context, R.string.select_success,
						Toast.LENGTH_SHORT).show();
			} else {
				btn_send.setClickable(true);
				btn_send.setTextColor(getResources().getColor(
						android.R.color.white));
				Toast.makeText(context, R.string.choose_failure,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onSelectAllUser(Context context, Intent intent,
				List<User> listUser) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetUserInfo(User user) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDisconnected(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mConnected = false;
			invalidateOptionsMenu();
			data.clear();
			setData(NO, "No Connection");
			ll_test.setVisibility(View.GONE);
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onDeleteUserScale(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDeleteUser(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCreateNewUser(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectSuccess(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mConnected = true;
			ShowDialog();
			invalidateOptionsMenu();

			data.clear();
			setData(OperationData.VIEW_ALL_USERS, "View all users");
			setData(OperationData.PURE_GUEST_MODE, "Pure guest mode");
			setData(OperationData.QUIT_PURE_GUEST_MODE, "Quit Pure guest mode");
			setData(OperationData.READ_MAC_ADDRESS, "Read MAC address");
			setData(OperationData.RESET_SCALE_PARAM, "Reset scale param");
			setData(OperationData.ZERO, "Zero");
			// setData("00","ReaderUpdateValue");
			adapter.notifyDataSetChanged();
			ll_test.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPureGuestMode(int staut) {
			// TODO Auto-generated method stub
			if (staut == 0) {
				Toast.makeText(context, "Setting Pure guest mode success!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "Setting Pure guest mode error!",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onQuitPureGuestMode(int staut) {
			// TODO Auto-generated method stub
			if (staut == 0) {
				Toast.makeText(context, "Quit Pure guest mode success!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "Quit Pure guest mode error!",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onReadMacAddress(String macAddress) {
			// TODO Auto-generated method stub
			tv_mac_address.setText("Mac:" + macAddress);
		}

		@Override
		public void onResetScaleParam(int staut) {
			// TODO Auto-generated method stub
			if (staut == 0) {
				Toast.makeText(context, "Reset Success!", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "Reset Error!", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onZero(int staut) {
			// TODO Auto-generated method stub
			if (staut == 0) {
				Toast.makeText(context, "Zero success!", Toast.LENGTH_SHORT)
						.show();
			} else if (staut == 1) {
				Toast.makeText(context, "Zero error!", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onReadNumber(String number) {
			// TODO Auto-generated method stub
			et_number.setText(number);
		}

		@Override
		public void onWriteNumber(int staut) {
			// TODO Auto-generated method stub
			if (staut == 4) {
				Toast.makeText(context, "write success!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
}
