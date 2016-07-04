package com.example.shinoharanaoki.useyourapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shinoharanaoki.useyourapps.models.MonitoringApp;

import java.util.ArrayList;

/**
 * Created by shinoharanaoki on 2016/06/10.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private LayoutInflater mInflater;
    private ArrayList<MonitoringApp> mData;
    private Context mContext;
    private MonitoringApp mApp;
    //private OnRecyclerListener mListener;

    public RecyclerAdapter(Context context, ArrayList<MonitoringApp> data) {

        mData = data;
        mContext = context;
        //mListener = listener;
    }

    @Override //ex. RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//parent = ex.RecyclerView
        // 表示するレイアウトを設定
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {

        // データ表示(SQLに監視対象アプリデータが有れば)
        if (mData != null && mData.size() > i && mData.get(i) != null) {
            //TODO
            //viewHolder.imageView_icon.setImageDrawable(mData.get(i).getIcon());
            viewHolder.textView_credit.setText(mData.get(i).getCredit());
            viewHolder.textView_appName.setText(mData.get(i).getApplicationName());

            // クリック処理
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO リストクリックでアプリを開けるようにする
                    PackageManager pManager = mContext.getPackageManager();
                    Intent intent = pManager.getLaunchIntentForPackage(mData.get(i).getPackageName());
                    mContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }


    // ViewHolder(固有ならインナークラスでOK)

    class ViewHolder extends RecyclerView.ViewHolder {

        //ImageView imageView_icon;
        TextView textView_credit;
        TextView textView_appName;

        //TODO 下部のメモ欄メモがあれば表示無ければ隠す
        //TextView textView_memo;

        public ViewHolder(View itemView) {
            super(itemView);
            //imageView_icon = (ImageView) itemView.findViewById(R.id.icon);
            textView_credit = (TextView) itemView.findViewById(R.id.credit);
            textView_appName = (TextView) itemView.findViewById(R.id.app_name);
        }
    }
}
