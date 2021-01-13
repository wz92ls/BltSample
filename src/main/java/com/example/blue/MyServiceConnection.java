package com.example.blue;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.example.blue.MQTTService;

public class MyServiceConnection  implements ServiceConnection {
    private MQTTService mqttService;

    private MQTTService.IGetMessageCallBack IGetMessageCallBack;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mqttService = ((MQTTService.CustomBinder)iBinder).getService();
        mqttService.setIGetMessageCallBack(IGetMessageCallBack);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public MQTTService getMqttService(){
        return mqttService;
    }

    public void setIGetMessageCallBack(MQTTService.IGetMessageCallBack IGetMessageCallBack){
        this.IGetMessageCallBack = IGetMessageCallBack;
    }
}
