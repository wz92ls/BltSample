package com.example.blue.ryfitdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.chronocloud.ryfibluetoothlibrary.BluetoothOpration;
import com.example.blue.R;
import com.example.blue.ryfitdemo.entity.UserData;
import com.example.blue.ryfitdemo.util.MyApplication;

import java.util.Map;

/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CareteTime:2014-3-12 上午10:05:32
 * @description Class User Create or Update
 */
@SuppressLint("NewApi")
public class UserInfoActivity extends Activity {

	private Context context;
	private RadioGroup rg_sex;
	private RadioButton rb_man, rb_woman;
	private EditText et_height, et_age;
	private Button btn_send;
	private String sex = "01";
	private Map<String, String> map;
	// public static BluetoothLeService mBluetoothLeService;
	private BluetoothOpration mBluetoothOpration;
	private String index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tizhi_user_info);
		context = this;

		initView();
		initData();
		initAction();
	}

	/**
	 * 
	 * @description Method 初始化数据
	 * @author Mars 2014-3-12 上午10:44:12
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		// TODO Auto-generated method stub
		mBluetoothOpration = MyApplication._BluetoothOpration;
		map = (Map<String, String>) getIntent().getBundleExtra("bundle")
				.getSerializable("map");
		index = map.get(UserData.INDEX);
		if (index != null) {
			if (Integer.parseInt(map.get(UserData.SEX)) == 0) {
				rb_woman.setChecked(true);
			} else {
				rb_man.setChecked(true);
			}
			et_age.setText(Integer.parseInt(map.get(UserData.AGE)) + "");
			et_height.setText(Integer.parseInt(map.get(UserData.HEIGHT)) + "");
		}
		getActionBar().setTitle(
				"用户" + Integer.parseInt(map.get(UserData.NUMERICAL_ORDER)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
	 * @description Method 初始化事件
	 * @author Mars 2014-3-12 上午10:16:28
	 */
	private void initAction() {
		// TODO Auto-generated method stub
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
				if ((!height.equals("") && !age.equals(""))
						&& (Integer.parseInt(height) >= 100 && Integer
								.parseInt(height) <= 220)
						&& (Integer.parseInt(age) >= 10 && Integer
								.parseInt(age) <= 80)) {
					if (index != null && mBluetoothOpration != null) {
						Log.i("info",
								"updateUser(0"
										+ Integer.parseInt(map
												.get(UserData.NUMERICAL_ORDER),
												16) + "," + height + "," + age
										+ "," + sex + ")");
						mBluetoothOpration.updateUser(
								"0"
										+ Integer.parseInt(map
												.get(UserData.NUMERICAL_ORDER),
												16), height, age, sex);
					} else if (index == null && mBluetoothOpration != null) {
						Log.i("info",
								"createNewUser(0"
										+ Integer.parseInt(map
												.get(UserData.NUMERICAL_ORDER),
												16) + "," + height + "," + age
										+ "," + sex + ")");
						mBluetoothOpration.createNewUser(
								"0"
										+ Integer.parseInt(map
												.get(UserData.NUMERICAL_ORDER),
												16), height, age, sex);
					}
					((Activity) context).setResult(400, new Intent());
					((Activity) context).finish();
				} else {
					Toast.makeText(context, R.string.age_height,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 
	 * @description Method 初始化 控件
	 * @author Mars 2014-3-12 上午10:13:27
	 */
	private void initView() {
		// TODO Auto-generated method stub
		rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
		rb_man = (RadioButton) findViewById(R.id.rb_man);
		rb_woman = (RadioButton) findViewById(R.id.rb_woman);
		et_height = (EditText) findViewById(R.id.et_height);
		et_age = (EditText) findViewById(R.id.et_age);
		btn_send = (Button) findViewById(R.id.btn_send);
	}
}
