package com.example.shinoharanaoki.useyourapps;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.example.shinoharanaoki.useyourapps.main_activity.MainActivityFragment;
import com.example.shinoharanaoki.useyourapps.main_activity.RecyclerAdapter;
import com.example.shinoharanaoki.useyourapps.models.AppDataDao;
import com.example.shinoharanaoki.useyourapps.models.AppDataHelper;
import com.example.shinoharanaoki.useyourapps.models.MonitoringApp;

import java.util.ArrayList;

/**
 * Created by shinoharanaoki on 2016/06/27.
 */
public class Globals extends Application {

    private final static String TAG = "Globals";

    private AppDataHelper dbHelper;
    private SQLiteDatabase db;
    private AppDataDao mdao;

    private MainActivityFragment mFragment;

    //private boolean dataExist = false;

    /**
     *
     * ServiceやMainActivityFragmentで使用する参照用リスト
    * アプリ、もしくはサービス起動中には、監視アプリリストがここに常に保持される*
     * */
    public ArrayList<MonitoringApp> appList;


    /*グローバル変数初期化*/
    public void GlobalsAllInit(){

        dbHelper = new AppDataHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        mdao = new AppDataDao(db);

        //TODO SQLデータがまだ無い時のために例外処理が絶対必要！！*/

        if (mdao.exists()) {
            appList = mdao.findAll();
            //dataExist = true;
        }else{
            appList = new ArrayList<>();
            //dataExist = false;
            //TEST
            /*String app = "FakeApp";
            String pname = "anonymous.fake.app";
            mdao.save(new MonitoringApp(app, pname));
            appList = mdao.findAll();*/
        }

        //TODO throwを書いてみる
        try {
            if (mdao.exists()) {
                appList = mdao.findAll();
            }
        }catch(NullPointerException e){
            throw new NullPointerException("SQLdb is Null");
        }

        Log.d(TAG, "GlobalsAllInit: ");

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

    private ArrayList<MonitoringApp> makeList() {

        //SQLデータベースからApp型リストへ変換するメソッド
        ArrayList list = appList;
        return list;
    }

    /*MainActivityFragmentで監視リストへの新規追加があったときに呼び出される*/
    public void addToAppList(MonitoringApp newApp){
        appList.add(newApp);
        adapterNotify();
        //dataExist = true;

        /**
         * For Test*/
        Toast.makeText(Globals.this, "add an item and adapter notify", Toast.LENGTH_SHORT).show();

    }

    /*MainActivityFragmentで監視リストからのアプリ削除操作があったときに呼び出される*/
    public void deleteFromAppList(){

        if(!isListEmpty()){
            //dataExist = false;
        }
    }

    public boolean isListEmpty(){
            return appList.isEmpty();
    }

    /*public boolean isDataExist() {
        return dataExist;
    }*/
    /*
    注意：グローバルクラスなのでゲッターいらない
    public ArrayList<MonitoringApp> getAppList()

        return appList;
    }*/

    public void setActiveFragment(MainActivityFragment fragment){
        mFragment = fragment;
    }

    public void adapterNotify(){
        mFragment.updateListView();
    }

}
