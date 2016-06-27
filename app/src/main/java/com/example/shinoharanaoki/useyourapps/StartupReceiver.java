package com.example.shinoharanaoki.useyourapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shinoharanaoki on 2016/06/14.
 */

//TODO 監視リストがないときはサービスを起動しないようにする
public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AppUsageMonitoringService.class);
        context.startService(serviceIntent);
    }
}
