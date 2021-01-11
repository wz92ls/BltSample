package com.example.blue.ryfitdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.chronocloud.ryfibluetoothlibrary.BluetoothOpration;
import com.chronocloud.ryfibluetoothlibrary.entity.TestDataInfo;
import com.chronocloud.ryfibluetoothlibrary.entity.User;
import com.chronocloud.ryfibluetoothlibrary.listener.BluetoothOprationCallback;
import com.example.blue.MainActivity;
import com.example.blue.R;
import com.example.blue.ryfitdemo.entity.OperationData;
import com.example.blue.ryfitdemo.entity.TestData;
import com.example.blue.ryfitdemo.entity.UserData;
import com.example.blue.ryfitdemo.util.MyApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CareteTime:2014-3-10 下午7:25:55
 * @description Class User Data Operation
 */
@SuppressLint("NewApi")
public class UserDataOperationActivity extends Activity implements
		OnClickListener {

	private Map<String, String> map;
	private TextView user_id, tv_age, tv_height, tv_sex, scale_weight;
	private Switch s_switch;
	private ListView lv_opration_data;
	private LinearLayout ll_dud;
	private Button btn_delet_user, btn_update_user, btn_delete_scale,
			btn_create_user;
	private String numerical_order, index,age,height;
	private BaseAdapter adapter;
	private Context context;
	private BluetoothOpration mBluetoothOpration;
	private boolean isTest = false;
	private Map<String, String> testMap;
	private List<Map<String, String>> listMap;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tizhi_user_data_operation);
		context = this;
		initView();
		initData();
		initAction();
	}

	/**
	 * 
	 * @description Method 初始化事件
	 * @author Mars 2014-3-10 下午7:58:18
	 */
	private void initAction() {
		// TODO Auto-generated method stub
		mBluetoothOpration = MainActivity._BluetoothOpration;
		mBluetoothOpration.addBluetoothOprationCallback(BOcallback);
		s_switch.setChecked(isTest);
		s_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				isTest = isChecked;
				if (isTest && index != null && mBluetoothOpration != null) {
					mBluetoothOpration.selectUserScale(
							"0" + map.get(UserData.NUMERICAL_ORDER),
							map.get(UserData.HEIGHT), map.get(UserData.AGE),
							"0" + map.get(UserData.SEX),"999");
				}
			}
		});
		lv_opration_data.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Map<String, String> map = (Map<String, String>) arg0
						.getItemAtPosition(arg2);
				if (map.get("order").equals(OperationData.IS_EXIST)
						&& mBluetoothOpration != null) {
					mBluetoothOpration.userIsExist(numerical_order);
				} else if (map.get("order").equals(OperationData.GET_SIM_USER)
						&& mBluetoothOpration != null) {
					mBluetoothOpration.getUserInfoByNumber(numerical_order);
				} else if (map.get("order").equals(OperationData.CLEAR)) {
					user_id.setText("");
					tv_age.setText("");
					tv_height.setText("");
					tv_sex.setText("");
				} else if (map.get("order").equals(
						OperationData.READ_USER_HISTORY)
						&& mBluetoothOpration != null) {
					if(age==null||height==null){
						return;
					}
					listMap.clear();
					mBluetoothOpration.selectUserScale(numerical_order,age,height);
					if (dialog == null || !dialog.isShowing()) {
						dialog = new ProgressDialog(context);
					}
					dialog.setMessage(getString(R.string.loading));
					dialog.setCancelable(true);
					dialog.show();
				}
			}
		});

		btn_delet_user.setOnClickListener(this);
		btn_update_user.setOnClickListener(this);
		btn_delete_scale.setOnClickListener(this);
		btn_create_user.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, UserInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("map", (Serializable) map);
		intent.putExtra("bundle", bundle);
		switch (v.getId()) {
		// Delete User
		case R.id.btn_delet_user:
			if (mBluetoothOpration != null) {
				mBluetoothOpration.deleteUser(numerical_order);
			}
			break;
		// Update User
		case R.id.btn_update_user:
			startActivityForResult(intent, 100);
			break;
		// Delete User Scale
		case R.id.btn_delete_scale:
			if (mBluetoothOpration != null) {
				mBluetoothOpration.deleteUserScale(numerical_order);
			}
			break;
		// Create User
		case R.id.btn_create_user:
			startActivityForResult(intent, 100);
			break;
		}
	}

	/**
	 * 
	 * @description Method 初始化数据
	 * @author Mars 2014-3-10 下午7:50:58
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		// TODO Auto-generated method stub
		listMap = new ArrayList<Map<String, String>>();
		map = (Map<String, String>) getIntent().getBundleExtra("bundle")
				.getSerializable("map");
		numerical_order = "0"
				+ Integer.parseInt(map.get(UserData.NUMERICAL_ORDER), 16);
		age = map.get(UserData.AGE);
		height = map.get(UserData.HEIGHT);
		index = map.get(UserData.INDEX);
		if (index == null) {
			ll_dud.setVisibility(View.GONE);
			btn_create_user.setVisibility(View.VISIBLE);
		} else {
			ll_dud.setVisibility(View.VISIBLE);
			btn_create_user.setVisibility(View.GONE);
		}
		adapter = new SimpleAdapter(context, getData(),
				android.R.layout.simple_list_item_1, new String[] { "name" },
				new int[] { android.R.id.text1 });
		lv_opration_data.setAdapter(adapter);
		getActionBar().setTitle("用户" + Integer.parseInt(numerical_order, 16));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 
	 * @description Method description
	 * @author Mars Jul 7, 2014 3:11:14 PM
	 * @return
	 */
	private List<Map<String, String>> getData() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> mapData = new HashMap<String, String>();
		mapData.put("name", getString(R.string.is_exist));
		mapData.put("order", OperationData.IS_EXIST);
		list.add(mapData);

		mapData = new HashMap<String, String>();
		mapData.put("name", getString(R.string.get_sim_user));
		mapData.put("order", OperationData.GET_SIM_USER);
		list.add(mapData);

		mapData = new HashMap<String, String>();
		mapData.put("name", getString(R.string.clear));
		mapData.put("order", OperationData.CLEAR);
		list.add(mapData);

		mapData = new HashMap<String, String>();
		mapData.put("name", getString(R.string.read_user_history));
		mapData.put("order", OperationData.READ_USER_HISTORY);
		list.add(mapData);
		return list;
	}

	/**
	 * 
	 * @description Method 初始化控件
	 * @author Mars 2014-3-10 下午7:27:43
	 */
	private void initView() {
		// TODO Auto-generated method stub
		user_id = (TextView) findViewById(R.id.user_id);
		tv_age = (TextView) findViewById(R.id.age);
		tv_height = (TextView) findViewById(R.id.height);
		tv_sex = (TextView) findViewById(R.id.sex);
		scale_weight = (TextView) findViewById(R.id.scale_weight);
		s_switch = (Switch) findViewById(R.id.s_switch);
		lv_opration_data = (ListView) findViewById(R.id.lv_opration_data);
		ll_dud = (LinearLayout) findViewById(R.id.ll_dud);
		btn_delet_user = (Button) findViewById(R.id.btn_delet_user);
		btn_update_user = (Button) findViewById(R.id.btn_update_user);
		btn_delete_scale = (Button) findViewById(R.id.btn_delete_scale);
		btn_create_user = (Button) findViewById(R.id.btn_create_user);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBluetoothOpration.removeBluetoothOprationCallback(BOcallback);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 * @description Method start History Activity
	 * @author Mars 2014-3-14 上午10:38:09
	 */
	private void startHistoryActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, UserHistoryDataActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("listMap", (Serializable) listMap);
		intent.putExtra("bundle", bundle);
		intent.putExtra("title", "用户" + Integer.parseInt(numerical_order, 16));
		startActivity(intent);
	}

	/**
	 * 
	 * @description Method Start Test Data Activity
	 * @author Mars 2014-3-13 下午4:32:24
	 */
	private void startTestActivity() {
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
			if (staut == 1) {
				Toast.makeText(context, R.string.no_such_user,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.user_exists,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onUpdateUser(int staut) {
			// TODO Auto-generated method stub
			if (staut == 0) {
				mBluetoothOpration.getUserInfoByNumber(numerical_order);
				// mBluetoothLeService.WriteValue("A4" + numerical_order);
				Toast.makeText(context, R.string.updated_successfully,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.user_not_exist,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onTestDataInfo(TestDataInfo t) {
			// TODO Auto-generated method stub
			listMap.clear();
			testMap = new HashMap<String, String>();
			testMap.put(TestData.TIME, t.getTime());
			testMap.put(TestData.WEIGHT, t.getWeight());
			testMap.put(TestData.BF, t.getBf());
			testMap.put(TestData.WATRER, t.getWatrer());
			testMap.put(TestData.MUSCLE, t.getMuscle());
			testMap.put(TestData.BONE, t.getBone());
			testMap.put(TestData.BMR, t.getBmr());
			testMap.put(TestData.SFAT, t.getSfat());
			testMap.put(TestData.INFAT, t.getInfat());
			testMap.put(TestData.BODYAGE, t.getBodyage());
			listMap.add(testMap);
			startTestActivity();
		}

		@Override
		public void onSelectUserScale(List<TestDataInfo> listDataInfo) {
			// TODO Auto-generated method stub
			listMap.clear();
			if (listDataInfo != null) {
				for (TestDataInfo t : listDataInfo) {
					testMap = new HashMap<String, String>();
					testMap.put(TestData.TIME, t.getTime());
					testMap.put(TestData.WEIGHT, t.getWeight() + "kg");
					testMap.put(TestData.BF, t.getBf() + "%");
					testMap.put(TestData.WATRER, t.getWatrer() + "%");
					testMap.put(TestData.MUSCLE, t.getMuscle() + "%");
					testMap.put(TestData.BONE, t.getBone() + "%");
					testMap.put(TestData.BMR, t.getBmr() + "cal");
					testMap.put(TestData.SFAT, t.getSfat() + "%");
					testMap.put(TestData.INFAT, t.getInfat() + "");
					testMap.put(TestData.BODYAGE, t.getBodyage() + "Years");
					listMap.add(testMap);
				}
				startHistoryActivity();
			} else {
				Toast.makeText(context, R.string.no_history_data,
						Toast.LENGTH_SHORT).show();
			}
			if (dialog != null && dialog.isShowing()) {
				dialog.cancel();
				dialog = null;
			}
		}

		@Override
		public void onSelectUserScale(int staut) {
			// TODO Auto-generated method stub
			if (staut == 0) {
				Toast.makeText(context, R.string.select_success,
						Toast.LENGTH_SHORT).show();
			} else {
				s_switch.setChecked(false);
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
			if (user == null) {
				Toast.makeText(context, R.string.no_such_user,
						Toast.LENGTH_SHORT).show();
			} else {
				user_id.setText(Integer.parseInt(numerical_order, 16) + "");
				tv_age.setText(user.getAge());
				tv_height.setText(user.getHeight() + "cm");
				tv_sex.setText(Integer.parseInt(user.getSex()) == 0 ? "Woman"
						: "Man");
				if (map == null) {
					map = new HashMap<String, String>();
				}
				map.put(UserData.INDEX, Integer.parseInt(numerical_order, 16)
						+ "");
				map.put(UserData.NUMERICAL_ORDER,
						Integer.parseInt(numerical_order, 16) + "");
				map.put(UserData.HEIGHT, user.getHeight());
				map.put(UserData.AGE, user.getAge());
				map.put(UserData.SEX, user.getSex());
				age = user.getAge();
				height = user.getHeight();
			}
		}

		@Override
		public void onDisconnected(Context context, Intent intent) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDeleteUserScale(int staut) {
			// TODO Auto-generated method stub
			if (staut == 1) {
				Toast.makeText(context, R.string.delete_failed,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.deleted_successfully,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onDeleteUser(int staut) {
			// TODO Auto-generated method stub
			if (staut == 1) {
				Toast.makeText(context, R.string.delete_failed,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.deleted_successfully,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}

		@Override
		public void onCreateNewUser(int staut) {
			// TODO Auto-generated method stub
			if (staut == 0) {
				mBluetoothOpration.getUserInfoByNumber(numerical_order);
				// mBluetoothLeService.WriteValue("A4" + numerical_order);
				Toast.makeText(context, R.string.new_user_successfully,
						Toast.LENGTH_SHORT).show();
			} else if (staut == 1) {
				Toast.makeText(context, R.string.users_is_full,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.user_is_exists,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onConnectSuccess(Context context, Intent intent) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPureGuestMode(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onQuitPureGuestMode(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReadMacAddress(String macAddress) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResetScaleParam(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onZero(int staut) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReadNumber(String number) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onWriteNumber(int staut) {
			// TODO Auto-generated method stub

		}
	};
}
