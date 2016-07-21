package com.example.shinoharanaoki.useyourapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shinoharanaoki.useyourapps.models.AppDataDao;
import com.example.shinoharanaoki.useyourapps.models.AppDataHelper;
import com.example.shinoharanaoki.useyourapps.models.MonitoringApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstalledAppsFinderActivity extends AppCompatActivity {

    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.installed_apps_row);

        // 端末にインストール済のアプリケーション一覧情報を取得
        final PackageManager pm = getPackageManager();
        final int flags = PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES;
        final List<ApplicationInfo> installedAppList = pm.getInstalledApplications(flags);

        AppDataHelper dbHelper = new AppDataHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final AppDataDao mdao = new AppDataDao(db);

        globals = (Globals) this.getApplication();

        // リストに一覧データを格納する
        final List<AppData> dataList = new ArrayList<>();
        for (ApplicationInfo app : installedAppList) {
            AppData data = new AppData();
            data.label = app.loadLabel(pm).toString();
            //TODO 既に監視対象に登録済みのアプリはリストに表示しないようにする
            //data.pname = app.packageName;
            dataList.add(data);
        }

        // リストビューにアプリケーションの一覧を表示する
        final ListView listView = new ListView(this);
        listView.setAdapter(new AppListAdapter(this, dataList));


        //TODO チェックボックスに対応する

        /**
         *
         *
         *
         * リストをタッチでGlobalsの監視用アプリリストオブジェクトに新たに監視するアプリを加える。
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ApplicationInfo item = installedAppList.get(position);

                MonitoringApp newapp = new MonitoringApp(item.loadLabel(pm).toString(),item.packageName);

                //TODO アイコンはデータに保存せずに画面表示の都度ApplicationManagerからもらうようにする
                //newapp.setIcon(item.loadIcon(pm));

                globals.addToAppList(newapp);
                globals.adapterNotify();
                /*
                For TEST Globalsのリストに登録されたか確認用
                */
                Toast.makeText(InstalledAppsFinderActivity.this, globals.appList.get(0).getApplicationName(), Toast.LENGTH_SHORT).show();


                //TODO インテントとハンドラ配信
                //sendUpdateBroadCast("監視対象のアプリが追加されました");

            }
        });
        setContentView(listView);
    }

    // アプリケーションデータ格納クラス
    private static class AppData {
        String label;
        Drawable icon;
        String pname;
    }

    // アプリケーションのラベルとアイコンを表示するためのアダプタークラス
    private static class AppListAdapter extends ArrayAdapter<AppData> {

        private final LayoutInflater mInflater;

        public AppListAdapter(Context context, List<AppData> dataList) {
            super(context, R.layout.installed_apps_row);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            addAll(dataList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = new ViewHolder();

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.installed_apps_row, parent, false);
                holder.textLabel = (TextView) convertView.findViewById(R.id.label);
                //holder.imageIcon = (ImageView) convertView.findViewById(R.id.icon);
                //holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 表示データを取得
            final AppData data = getItem(position);
            // ラベルとアイコンをリストビューに設定
            holder.textLabel.setText(data.label);
            //holder.imageIcon.setImageDrawable(data.icon);

            return convertView;
        }
    }

    // ビューホルダー
    private static class ViewHolder {
        TextView textLabel;
        //ImageView imageIcon;
        //CheckBox checkBox;
    }


    /*
    リストへの追加があったことをMainActivityFragmentのAdapterに向けてインテントで伝える
    */
    /*public void sendUpdateBroadCast(String message){

        Intent broadcastIntent = new Intent();
        //TEST new Dateはテスト用
        broadcastIntent.putExtra("message", message + new Date().toString());
        //Intent putExtra (String name, String value)
        broadcastIntent.setAction("MY_ACTION");
        // ブロードキャストへ配信させる
        getBaseContext().sendBroadcast(broadcastIntent);

        Toast.makeText(InstalledAppsFinderActivity.this, "sendUpdateBroadCast", Toast.LENGTH_SHORT).show();

    }*/

}

