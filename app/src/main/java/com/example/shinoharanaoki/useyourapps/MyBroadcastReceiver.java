package com.example.shinoharanaoki.useyourapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by shinoharanaoki on 2016/06/27.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    public static Handler handler;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String message = bundle.getString("message");//TEST

        if(handler !=null){

            Message msg = new Message();//TEST

            Bundle data = new Bundle();
            data.putString("message", message);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }

    /**
     * メイン画面の表示を更新
     */
    public void registerHandler(Handler locationUpdateHandler) {
        handler = locationUpdateHandler;

        /*
        MainActivityFragment
        myReceiver.registerHandler(updateHandler);*/
    }
}
