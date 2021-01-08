package com.example.blue;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import com.example.blue.https.HttpClient;
import com.example.blue.ryfitdemo.tizhi_MainActivity;
import com.example.blue.ble.BLE_DeviceScanActivity;
import com.example.blue.xueya.DeviceScanActivity;

public class MainActivity extends TabActivity implements View.OnClickListener{

    private static final String TAG = "TabActivity";
    private Bundle mBundle = new Bundle();
    private TabHost tabHost;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private String titel="";;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titel=getString(R.string.app_name)+"-"+getString(R.string.xueya);
        getActionBar().setTitle(titel);
        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv3 = (TextView)findViewById(R.id.tv3);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tabHost = getTabHost();
        tabHost.addTab(getNewTab("tv1",R.string.unknown_device,R.drawable.ic_launcher, DeviceScanActivity.class));
        tabHost.addTab(getNewTab("tv2",R.string.unknown_device,R.drawable.ic_launcher,tizhi_MainActivity.class));
        tabHost.addTab(getNewTab("tv3",R.string.unknown_device,R.drawable.ic_launcher, BLE_DeviceScanActivity.class));
//        tabHost.addTab(getNewTab("tv3",R.string.unknown_device,R.drawable.ic_scan,DeviceScanActivity.class));
//        tabHost.addTab(getNewTab("tv4",R.string.unknown_device,R.drawable.ic_home,DeviceScanActivity.class));
        tabHost.setCurrentTabByTag("tv1");




    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv1:
                tabHost.setCurrentTabByTag("tv1");
                titel = getString(R.string.app_name)+"-"+getString(R.string.xueya);
                getActionBar().setTitle(titel);
//                tizhi_MainActivity.messageCallback.onMessage("stop");
                break;
            case R.id.tv2:
                tabHost.setCurrentTabByTag("tv2");
                titel = getString(R.string.app_name)+"-"+getString(R.string.tizhi);
                getActionBar().setTitle(titel);
//                DeviceScanActivity.messageCallback.onMessage("stop");
                break;
            case R.id.tv3:
                tabHost.setCurrentTabByTag("tv3");
                titel = getString(R.string.app_name)+"-"+getString(R.string.matong);
                getActionBar().setTitle(titel);
//                DeviceScanActivity.messageCallback.onMessage("stop");
//                tizhi_MainActivity.messageCallback.onMessage("stop");
                break;
        }
    }

    private TabHost.TabSpec getNewTab(String spec,int label,int icon,Class<?> cls){
        Intent intent = new Intent(this,cls).putExtras(mBundle);
        return tabHost.newTabSpec(spec).setContent(intent).setIndicator(getString(label),getResources()
                .getDrawable(icon));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        if (!false) {
//            menu.findItem(R.id.menu_stop).setVisible(true);
//            menu.findItem(R.id.menu_scan).setVisible(true);
//            menu.findItem(R.id.menu_refresh).setActionView(null);
//        } else {
//            menu.findItem(R.id.menu_stop).setVisible(true);
//            menu.findItem(R.id.menu_scan).setVisible(false);
//            menu.findItem(R.id.menu_refresh).setActionView(
//                    R.layout.tizhi_actionbar_indeterminate_progress);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d("12345","curtab="+tabHost.getCurrentTab());
//        int curtab=tabHost.getCurrentTab();
//        switch (item.getItemId()) {
//            case R.id.menu_scan:
//                DeviceScanActivity.messageCallback.onMessage("scan");
//                break;
//            case R.id.menu_stop:
//                DeviceScanActivity.messageCallback.onMessage("stop");
//                break;
//        }
//        return true;
//    }
}