package com.example.blue.ryfitdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.example.blue.R;
import com.example.blue.ryfitdemo.entity.TestData;

import java.util.Map;

/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CareteTime:2014-3-13 下午4:15:06
 * @description Class description
 */
public class TestDataActivity extends Activity {

	private TextView tv_measuring_time,tv_weight,tv_fat,tv_moisture,tv_muscle,
	tv_bone,tv_calories,tv_subcutaneous_fat,tv_visceral_fat,tv_body_age;
	private Button btn_dismiss;
	private Map<String, String> testMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tizhi_test_data);
		initView();
		initData();
		initAction();
	}
	/**
	 * 
	 * @description Method Initialization Action
	 * @author Mars 2014-3-13 下午4:26:12
	 */
	private void initAction() {
		// TODO Auto-generated method stub
		btn_dismiss.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	/**
	 * 
	 * @description Method Initialization Data
	 * @author Mars 2014-3-13 下午4:16:58
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		// TODO Auto-generated method stub
		testMap=(Map<String, String>) getIntent().getBundleExtra("bundle").getSerializable("testMap");
		tv_measuring_time.setText(testMap.get(TestData.TIME));
		tv_weight.setText(testMap.get(TestData.WEIGHT)+"kg");
		tv_fat.setText(testMap.get(TestData.BF)+"%");
		tv_moisture.setText(testMap.get(TestData.WATRER)+"%");
		tv_muscle.setText(testMap.get(TestData.MUSCLE)+"%");
		tv_bone.setText(testMap.get(TestData.BONE)+"%");
		tv_calories.setText(testMap.get(TestData.BMR)+"cal");
		tv_subcutaneous_fat.setText(testMap.get(TestData.SFAT)+"%");
		tv_visceral_fat.setText(testMap.get(TestData.INFAT));
		tv_body_age.setText(testMap.get(TestData.BODYAGE)+"Years");
	}
	/**
	 * 
	 * @description Method Initialization View
	 * @author Mars 2014-3-13 下午4:15:57
	 */
	private void initView() {
		// TODO Auto-generated method stub
		tv_measuring_time=(TextView) findViewById(R.id.tv_measuring_time);
		tv_weight=(TextView) findViewById(R.id.tv_weight);
		tv_fat=(TextView) findViewById(R.id.tv_fat);
		tv_moisture=(TextView) findViewById(R.id.tv_moisture);
		tv_muscle=(TextView) findViewById(R.id.tv_muscle);
		tv_bone=(TextView) findViewById(R.id.tv_bone);
		tv_calories=(TextView) findViewById(R.id.tv_calories);
		tv_subcutaneous_fat=(TextView) findViewById(R.id.tv_subcutaneous_fat);
		tv_visceral_fat=(TextView) findViewById(R.id.tv_visceral_fat);
		tv_body_age=(TextView) findViewById(R.id.tv_body_age);
		btn_dismiss=(Button) findViewById(R.id.btn_dismiss);
	}
}
