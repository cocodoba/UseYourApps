package com.example.shinoharanaoki.useyourapps;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;


/*
* 監視アプリのデータはこのサービス起動時にDao経由で取得し、その後はGlobalsクラスに保持させる。
* これによってService、その他のActivityをそれぞれインスタンス化して参照し合う、などということをしなくてよくなる。
* */


/*TODO UsageStatsManagerが使えないLollipop未満の端末用のServiceと、
       Marshmallow以上の端末用のServiceそれぞれ用意した方がいいかも知れない
*/

public class AppUsageMonitoringService extends Service {


    private Handler handler;//FIXME 要らない？


    private Timer timer = null;
    private int count = 0;//テスト用！！
    
    //TODO サービスの更新間隔を設定で変えられるようにする(300sec = 5min)
    private int interval = 300;

    //グローバル変数
    Globals globals;

    public AppUsageMonitoringService() {
    }

    @Override
    public void onCreate(){

        setUpAppListOnGlobals(); //メソッド分ける必要あるか？
    }

    /*(パターン２：Globalsからデータを都度取得する)*/
    public void setUpAppListOnGlobals(){
        //グローバル変数を取得
        globals = (Globals) this.getApplication();
        //初期化
        globals.GlobalsAllInit();

    }

    //TODO 要らないかもしれないので後で消してみる↓
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /*サービス起動時に毎回呼び出される*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "onStartCommand");
        timer = new Timer();
        /*サービス駆動確認用*/
        timer.schedule( new TimerTask(){
            @Override
            public void run(){
                Log.d( "TestService" , "count = "+ count );
                count++;
            }
        }, 0, 1000);

        /*
        指定した時間(interval)が来るごとにusageCheck()メソッドを呼び出して実行
        */
        timer.schedule( new TimerTask(){
            @Override
            public void run(){

                //TEST
                String message = "さーびすからのメッセージ";
                sendUpdateBroadCast(message);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    SortedMap<String,UsageStats> usageStatsMap;
                    usageStatsMap = getUsageStatsMap();

                    usageCheck_aboveMarshmallow(globals.appList, usageStatsMap);

                }else {
                    //FIXME UsageStatsManagerが使えないLollipop未満の端末への対処
                    //ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                    // Process running
                    //@SuppressWarnings("deprecation") ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
                    //foregroundProcess = foregroundTaskInfo.topActivity.getPackageName();
                }

            }
        }, 3000, 1000*interval);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            SortedMap<String,UsageStats> usageStatsMap;
            usageStatsMap = getUsageStatsMap();

            usageCheck_aboveMarshmallow(globals.appList, usageStatsMap);

            globals.GlobalsAllSave();

        }else {
            //FIXME UsageStatsManagerが使えないLollipop未満の端末への対処
            //ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
            // Process running
            //@SuppressWarnings("deprecation") ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
            //foregroundProcess = foregroundTaskInfo.topActivity.getPackageName();
        }

    }

    /*                                         UsageStatsManagerで
        * Globalsに置いてある監視リスト               OSから取得したアプリ履歴使用情報
        * applistOnGlobals<>                                 ↓
        *        ↓                                  UsageStats型オブジェクト
        * 一個ずつ取り出す                                      ↓
        *        ↓                                    全てSortedMapに一時格納
        *        ↓           パッケージ名を元に順に照合           ↓
        *   mapp mapp mapp....  → → → → → → → →  SortedMap.get(pname).getTotalTimeInForeground()
        *                                        SortedMap.get(pname).getLastTimeUsed()
        *                                         ...
        *    mapp.set〇〇等で上書き ← ← ← ← ← ← ← ←   などのメソッドで使用履歴の詳細情報を取得
        *         ↓
        *
        *
        * */


    private SortedMap<String,UsageStats> getUsageStatsMap(){

        SortedMap<String,UsageStats> mySortedMap = new TreeMap<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            /*OSからアプリ使用履歴情報を取得*/
            UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last interval
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*interval, time);
            // Sort the stats by the last time used
            //TODO SortedMapである必要はないかも
            if(stats != null) {
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getPackageName(), usageStats);
                }
            }
        }
        return mySortedMap;
    }




    public void usageCheck_aboveMarshmallow(ArrayList<MonitoringApp> applistOnGlobals, Map<String,UsageStats> usageStatsMap){

        //TODO SortedMapがNullだった時にする処理を用意する必要があるかも
        //if(mySortedMap != null && !mySortedMap.isEmpty()) {

        /*手順１：Globalsの監視用リストからアイテムを１個ずつ取り出す*/
        for(MonitoringApp mapp : applistOnGlobals) {
            String pname = mapp.getPackageName();

        /*手順２：アイテムごとに、フォアグラウンドでの使用があれば実行時間と終了時刻をセーブ
        * mySortedMap.get()でUsageStats(アプリの使用情報オブジェクト)が得られる*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                mapp.setUseTime(usageStatsMap.get(pname).getTotalTimeInForeground());
                mapp.setLastTime(usageStatsMap.get(pname).getLastTimeUsed());
            }

            mapp.addCredit();


            /*TEST Globals変数への再保存確認用*/
            Toast.makeText(this,String.format(("「%1$s」を %2$d 使用しました"),
                    mapp.getApplicationName(), mapp.getUseTime()),Toast.LENGTH_LONG).show();

            Toast.makeText(this,String.format(("「%1$s」で %2$d credit獲得しました"),
                    mapp.getApplicationName(), mapp.getLastEarnedCredit()),Toast.LENGTH_LONG).show();
        }
        //TODO Globals.appListからuseTimeをmapp.getInterval()と一個づつ比較して超えているものがあればダイアログを表示する等の処理
    }

    public void usageCheck_BelowLollipop(){
        //TODO UsageStatsManagerが使えないLollipop未満の端末への対処
    }

    //FIXME 要らない？
    public void registerHandler(Handler updateHandler) {
        handler = updateHandler;
    }


    //TODO MainActivityFragment表示中ならそれを更新するメソッド
    public void sendUpdateBroadCast(String message){

        Intent broadcastIntent = new Intent();
        //TEST new Dateはテスト用
        broadcastIntent.putExtra("message", message + new Date().toString());
        broadcastIntent.setAction("UPDATE_ACTION");
        // ブロードキャストへ配信させる
        getBaseContext().sendBroadcast(broadcastIntent);

    }




}
