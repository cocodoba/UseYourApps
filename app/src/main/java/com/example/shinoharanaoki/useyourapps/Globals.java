package com.example.shinoharanaoki.useyourapps;

import android.app.Application;
import android.app.usage.UsageStats;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by shinoharanaoki on 2016/06/27.
 */
public class Globals extends Application {

    private AppDataHelper dbHelper;
    private SQLiteDatabase db;
    private AppDataDao mdao;


    /*ServiceやMainActivityFragmentで使用する参照用リスト
    * アプリ、もしくはサービス起動中には、監視アプリリストがここに常に保持される* */
    ArrayList<MonitoringApp> appList;



    /*グローバル変数初期化*/
    public void GlobalsAllInit(){


        dbHelper = new AppDataHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        mdao = new AppDataDao(db);

        //TODO SQLデータがまだ無い時のために例外処理が絶対必要！！*/
        try {
            if (mdao.exists()) {
                appList = mdao.findAll();
            }
        }catch(NullPointerException e){

            //TEST
            int index = 1;
            String app = "FakeApp";
            String pname = "anonymous.fake.app";
            appList.add(1, new MonitoringApp(app, pname));

            //TODO throwを書いてみる

        }
    }

    public void GlobalsAllSave(){

        try {
            if (mdao.exists()) {
                if(appList != null) {
                    for (MonitoringApp app : appList) {
                        mdao.save(app);
                    }
                }
            }
        }catch(NullPointerException e){

        }

    }

    /*MainActivityFragmentで監視リストへの新規追加があったときに呼び出される*/
    public void addToAppList(MonitoringApp newApp){
        appList.add(newApp);
    }

    /*MainActivityFragmentで監視リストからのアプリ削除操作があったときに呼び出される*/
    public void deleteFromAppList(){

    }

    public boolean isListExist(){
            return !appList.isEmpty();
    }

    /*
    注意：グローバルクラスなのでゲッターいらない
    public ArrayList<MonitoringApp> getAppList()

        return appList;
    }*/

}
