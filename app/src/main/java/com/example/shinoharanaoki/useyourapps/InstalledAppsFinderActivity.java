package com.example.shinoharanaoki.useyourapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InstalledAppsFinderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.installed_apps_row);

        // 端末にインストール済のアプリケーション一覧情報を取得
        final PackageManager pm = getPackageManager();
        final int flags = PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_DISABLED_COMPONENTS;
        final List<ApplicationInfo> installedAppList = pm.getInstalledApplications(flags);

        AppDataHelper dbHelper = new AppDataHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final AppDataDao mdao = new AppDataDao(db);

        // リストに一覧データを格納する
        final List<AppData> dataList = new ArrayList<>();
        for (ApplicationInfo app : installedAppList) {
            AppData data = new AppData();
            data.label = app.loadLabel(pm).toString();
            data.icon = app.loadIcon(pm);
            //TODO 既に監視対象に登録済みのアプリはリストに表示しないようにする
            //data.pname = app.packageName;
            dataList.add(data);
        }

        // リストビューにアプリケーションの一覧を表示する
        final ListView listView = new ListView(this);
        listView.setAdapter(new AppListAdapter(this, dataList));


        //TODO チェックボックスに対応する

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ApplicationInfo item = installedAppList.get(position);

                MonitoringApp newapp = new MonitoringApp(item.loadLabel(pm).toString(),item.packageName);
                newapp.setIcon(item.loadIcon(pm));

                PackageManager pManager = getPackageManager();
                Intent intent = pManager.getLaunchIntentForPackage(item.packageName);
                startActivity(intent);

                Toast.makeText(InstalledAppsFinderActivity.this, item.loadLabel(pm).toString(), Toast.LENGTH_SHORT).show();

                mdao.save(newapp);

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

    //あとは略
}
