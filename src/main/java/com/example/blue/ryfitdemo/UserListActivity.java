package com.example.blue.ryfitdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.chronocloud.ryfibluetoothlibrary.BluetoothOpration;
import com.chronocloud.ryfibluetoothlibrary.entity.TestDataInfo;
import com.chronocloud.ryfibluetoothlibrary.entity.User;
import com.chronocloud.ryfibluetoothlibrary.listener.BluetoothOprationCallback;
import com.example.blue.R;
import com.example.blue.ryfitdemo.adapter.UserListAdapter;
import com.example.blue.ryfitdemo.entity.UserData;
import com.example.blue.ryfitdemo.util.MyApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CareteTime:2014-3-10 下午2:42:01
 * @description Class User List
 */
@SuppressLint("NewApi")
public class UserListActivity extends Activity {

	private ListView lvData;
	private UserListAdapter adapter;
	private Context context;
	private List<Map<String, String>>  userData;
//	private BluetoothBroadcast bluetoothBroadcast;
//	public static BluetoothLeService mBluetoothLeService;
	private BluetoothOpration mBluetoothOpration;
	private int index=1;
	private boolean isOver=false;
	private ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tizhi_gatt_services_characteristics);
		context=this;
		mBluetoothOpration=MyApplication._BluetoothOpration;
		mBluetoothOpration.addBluetoothOprationCallback(BOcallback);
		initView ();
		getActionBar().setTitle("View all users");
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	BluetoothOprationCallback BOcallback=new BluetoothOprationCallback() {
		
		@Override
		public void onWeight(int staut, double weight) {
			// TODO Auto-generated method stub
			
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
			
		}
		
		@Override
		public void onSelectUserScale(List<TestDataInfo> listDataInfo) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSelectUserScale(int staut) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSelectAllUser(Context context, Intent intent,
				List<User> listUser) {
			// TODO Auto-generated method stub
			
			if(listUser.size()>0){
				for (User user : listUser) {
					setUserData(user);
				}
			}else{
				setUserData(null);
			}
			
			adapter.notifyDataSetChanged();
			isOver=true;
			if(dialog!=null&&dialog.isShowing()){
				dialog.cancel();
				dialog=null;
			}
		}
		
		@Override
		public void onGetUserInfo(User user) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDisconnected(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
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
	/**
	 * 
	 * @description Method 初始化控件
	 * @author Mars 2014-3-10 下午2:42:58
	 */
	private void initView() {
		// TODO Auto-generated method stub
		lvData=(ListView) findViewById(R.id.lv_data);
//		Intent intent=getIntent();
//		userData=(List<Map<String, String>>) intent.getBundleExtra("data").getSerializable("data");
		userData=new ArrayList<Map<String,String>>();
		adapter=new UserListAdapter(context, userData);
		lvData.setAdapter(adapter);
		
		lvData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(isOver){
				Intent intent=new Intent(context,UserDataOperationActivity.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("map", (Serializable) arg0.getItemAtPosition(arg2));
				intent.putExtra("bundle", bundle);
				startActivityForResult(intent, 100);
				}
			}
		});
	}
	   private void setUserData(User user){
	    	if(userData==null||userData.size()==0){
	    		index=1;
	    	}
	    	
	    	
	    	Map<String,String> map;
	    	
	    	
	    	if(user==null){
//    			if(Integer.parseInt(das[2],16)==1){
    				while(index<9){
    		    		map=new HashMap<String, String>();
    		        	map.put(UserData.NUMERICAL_ORDER, "0"+index);
    		        	userData.add(map);
    		    		index++;
    		    	}
//    			}
    		return;	
	    	}
	    	if(Integer.parseInt(user.getNumericalOder())!=0&&Integer.parseInt(user.getNumericalOder())<9){
	    	while(Integer.parseInt(user.getNumericalOder())!=index){
	    		map=new HashMap<String, String>();
	        	map.put(UserData.NUMERICAL_ORDER, "0"+index);
	        	userData.add(map);
	    		index++;
	    	}
	    	}
	    	map=new HashMap<String, String>();
	    	map.put(UserData.CMD_ID, user.getCmdId());
	    	map.put(UserData.ACCOUNT, user.getAccount());
	    	map.put(UserData.INDEX, user.getIndex());
	    	map.put(UserData.NUMERICAL_ORDER, user.getNumericalOder());
	    	map.put(UserData.HEIGHT, user.getHeight());
	    	map.put(UserData.AGE, user.getAge());
	    	map.put(UserData.SEX, user.getSex());
	    	userData.add(map);
	    	index++;
	    	if(user.getAccount().equals(user.getIndex())){
	    		while(index<9){
		    		map=new HashMap<String, String>();
		        	map.put(UserData.NUMERICAL_ORDER, "0"+index);
		        	userData.add(map);
		    		index++;
		    	}
	    	}
	    }
	   @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if(mBluetoothOpration!=null){
		isOver=false;
		userData.clear();
		adapter.notifyDataSetChanged();
		mBluetoothOpration.selectAllUser();
		
		if(dialog==null||!dialog.isShowing()){
			dialog=new ProgressDialog(context);
		}
		dialog.setMessage(getString(R.string.loading));
		dialog.setCancelable(true);
		dialog.show();
		}
	}
	   @Override
		protected void onDestroy() {
			super.onDestroy();
			mBluetoothOpration.removeBluetoothOprationCallback(BOcallback);
		}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                              //点击按钮
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
}
