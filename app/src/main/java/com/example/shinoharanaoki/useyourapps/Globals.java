package com.example.shinoharanaoki.useyourapps;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

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

        appList = mdao.findAll();
    }

    /*MainActivityFragmentで監視リストへの新規追加があったときに呼び出される*/
    public void addToAppList(MonitoringApp newApp){
        appList.add(newApp);
    }

    /*MainActivityFragmentで監視リストからのアプリ削除操作があったときに呼び出される*/
    public void deleteFromAppList(){

    }

    /*
    注意：グローバルクラスなのでゲッターいらない
    public ArrayList<MonitoringApp> getAppList()

        return appList;
    }*/

}
