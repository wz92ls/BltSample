package com.example.blue.ryfitdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.example.blue.R;
import com.example.blue.ryfitdemo.entity.TestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CareteTime:2014-3-14 上午10:57:26
 * @description Class User History Data Activity
 */
@SuppressLint("NewApi")
public class UserHistoryDataActivity extends Activity {

	private Context context;
	private ListView lv_history_info;
	private BaseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tizhi_history_user_info);
		context = this;
		initView();
		initData();
	}

	/**
	 * 
	 * @description Method Initialization Data
	 * @author Mars 2014-3-14 上午11:02:41
	 */
	private void initData() {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		List<Map<String, String>> listMap = (List<Map<String, String>>) getIntent()
				.getBundleExtra("bundle").getSerializable("listMap");
		String title = getIntent().getStringExtra("title");
		if (listMap == null) {
			listMap = new ArrayList<Map<String, String>>();
		}
		adapter = new SimpleAdapter(context, listMap,
				R.layout.tizhi_history_user_info_item, new String[] { TestData.TIME,
						TestData.WEIGHT, TestData.BF, TestData.WATRER,
						TestData.MUSCLE, TestData.BONE, TestData.BMR,
						TestData.SFAT, TestData.INFAT, TestData.BODYAGE },
				new int[] { R.id.tv_measuring_time, R.id.tv_weight,
						R.id.tv_fat, R.id.tv_moisture, R.id.tv_muscle,
						R.id.tv_bone, R.id.tv_calories,
						R.id.tv_subcutaneous_fat, R.id.tv_visceral_fat,
						R.id.tv_body_age });
		lv_history_info.setAdapter(adapter);
		getActionBar().setTitle(title);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 
	 * @description Method Initialization View
	 * @author Mars 2014-3-14 上午11:00:02
	 */
	private void initView() {
		// TODO Auto-generated method stub
		lv_history_info = (ListView) findViewById(R.id.lv_history_info);
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
}
