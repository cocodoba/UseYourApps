package com.example.shinoharanaoki.useyourapps.main_activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by shinoharanaoki on 2016/06/27.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    public static Handler handler;

    @Override
    public void onReceive(Context context, Intent intent) {

        Context ctx = context;//TEST
        Bundle bundle = intent.getExtras();
        String message = bundle.getString("message");//TEST

        Toast.makeText(ctx, "MyBroadCastReceiver.onReceive", Toast.LENGTH_SHORT).show();//TEST

        if(handler !=null){

            Message msg = new Message();//TEST

            Bundle data = new Bundle();
            data.putString("message", message);
            msg.setData(data);
            handler.sendMessage(msg);

            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();//TEST
        }
    }

    /**
     * メイン画面の表示を更新
     */
    public void registerHandler(Handler UpdateHandler) {
        handler = UpdateHandler;
        /*
        MainActivityFragment
        myReceiver.registerHandler(updateHandler);*/
    }
}
