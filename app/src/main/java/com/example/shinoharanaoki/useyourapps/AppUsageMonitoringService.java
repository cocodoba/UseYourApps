package com.example.shinoharanaoki.useyourapps;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
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

public class AppUsageMonitoringService extends Service {

    private Timer timer = null;
    private int count = 0;//テスト用！！
    
    //TODO サービスの更新間隔を設定で変えられるようにする(300sec = 5min)
    private int interval = 300;

    /*(パターン１：サービスでSQLファイルを用意する)
    /*private AppDataHelper dbHelper;
    private SQLiteDatabase db;
    private AppDataDao mdao;*/

    /*(パターン２：Globalsからデータを都度取得する)*/
    //グローバル変数
    Globals globals;

    public AppUsageMonitoringService() {
    }


    @Override
    public void onCreate(){
        /*DaoからSQLファイルを取得(パターン１：サービスで用意する)*/
        /*dbHelper = new AppDataHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        mdao = new AppDataDao(db);*/

        /*(パターン２：Globalsからデータを都度取得する)*/
        setUpAppListOnGlobals();
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


            /*                                         UsageStatsManagerで
            * Globalsのに置いてある監視リスト               OSから取得したアプリ履歴使用情報
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
                /*(パターン１：サービスでSQLファイルを用意する)
                SQLデータがまだ無い時のために例外処理が絶対必要！！*/
                /*try {
                    if (mdao.exists()) {
                        usageCheck(mdao.findAll());
                    }
                }catch(NullPointerException e){}*/

                /*(パターン２：Globalsからデータを都度取得する)*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Map<String,UsageStats> usageStatsMap;
                    getUsageStatsMap();
                    usageCheck_ForAboveMarshMallow(globals.appList, usageStatsMap);
                }

            }
        }, 3000, 1000*interval);

        return super.onStartCommand(intent, flags, startId);
    }


    private Map<String,UsageStats> getUsageStatsMap(){

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        // Process running
        //TODO UsageStatsManagerが使えないLollipop未満の端末への対処
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*OSからアプリ使用履歴情報を取得*/
            UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last interval
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*interval, time);
            // Sort the stats by the last time used
            //TODO SortedMapである必要はないかも
            if(stats != null) {
                SortedMap<String,UsageStats> mySortedMap = new TreeMap<String,UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getPackageName(), usageStats);
                }

                return mySortedMap;

                //TODO SortedMapがNullだった時にする処理を用意する必要があるかも
                if(mySortedMap != null && !mySortedMap.isEmpty()) {
                    /*手順１：Globalsの監視用リストからアイテムをを１個ずつ取り出す*/
                    for(MonitoringApp mapp : applistOnGlobals) {
                        String pname = mapp.getPackageName();
                        int earnedCredit;
                        long mUsedTime;
                        long tCredit;

                        /*手順２：アイテムごとに、フォアグラウンドでの使用があれば実行時間と終了時刻をセーブ
                        * mySortedMap.get()でUsageStats(アプリの使用情報オブジェクト)が得られる*/
                        mapp.setUseTime(mySortedMap.get(pname).getTotalTimeInForeground());
                        mapp.setLastTime(mySortedMap.get(pname).getLastTimeUsed());

                        mUsedTime = mapp.getUseTime();
                        tCredit = mapp.getTimePerCredit();

                        if(mUsedTime >= tCredit){
                            long remainder; //割った余り

                            long mEarnedCredit = mUsedTime / tCredit;
                            earnedCredit = (int)mEarnedCredit;
                            remainder = mUsedTime % tCredit;

                            mapp.setCredit(earnedCredit);
                            mapp.setUseTime(remainder);

                            Toast.makeText(this,String.format(("「%1$s」で %2$d credit獲得しました"),
                                    mapp.getApplicationName(), earnedCredit),Toast.LENGTH_LONG).show();


                        }

                        /*(パターン１：サービスで用意する)*/
                        /*mdao.save(mapp);*/

                        Toast.makeText(this,String.format(("「%1$s」現在TOTAL %2$d credit"), mapp.getApplicationName(), mapp.getCredit()), Toast.LENGTH_LONG);

                        //TODO sqlからuseTimeをapp.getInterval()と一個づつ比較して超えているものがあればダイアログを表示する等の処理


                        //TODO MainActivityFragment表示中ならそれを更新するメソッド
                    }
                }
            }
        } else {
            //TODO Lollipop以下バージョン向けの処理
            //@SuppressWarnings("deprecation") ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
            //foregroundProcess = foregroundTaskInfo.topActivity.getPackageName();
        }

    }

    public void usageCheck_ForAboveMarshMallow(ArrayList<MonitoringApp> applistOnGlobals, Map<String,UsageStats> usageStatsMap){

        /*手順１：Globalsの監視用リストからアイテムをを１個ずつ取り出す*/
        for(MonitoringApp mapp : applistOnGlobals) {
            String pname = mapp.getPackageName();
            int earnedCredit;
            long mUsedTime;
            long tCredit;

                        /*手順２：アイテムごとに、フォアグラウンドでの使用があれば実行時間と終了時刻をセーブ
                        * mySortedMap.get()でUsageStats(アプリの使用情報オブジェクト)が得られる*/
            mapp.setUseTime(usageStatsMap.get(pname).getTotalTimeInForeground());
            mapp.setLastTime(usageStatsMap.get(pname).getLastTimeUsed());

            mUsedTime = mapp.getUseTime();
            tCredit = mapp.getTimePerCredit();

            if(mUsedTime >= tCredit){
                long remainder; //割った余り

                long mEarnedCredit = mUsedTime / tCredit;
                earnedCredit = (int)mEarnedCredit;
                remainder = mUsedTime % tCredit;

                mapp.setCredit(earnedCredit);
                mapp.setUseTime(remainder);

                Toast.makeText(this,String.format(("「%1$s」で %2$d credit獲得しました"),
                        mapp.getApplicationName(), earnedCredit),Toast.LENGTH_LONG).show();


            }

                        /*(パターン１：サービスで用意する)*/
                        /*mdao.save(mapp);*/

            Toast.makeText(this,String.format(("「%1$s」現在TOTAL %2$d credit"), mapp.getApplicationName(), mapp.getCredit()), Toast.LENGTH_LONG);

            //TODO sqlからuseTimeをapp.getInterval()と一個づつ比較して超えているものがあればダイアログを表示する等の処理


            //TODO MainActivityFragment表示中ならそれを更新するメソッド

    }




}
