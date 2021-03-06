package com.example.shinoharanaoki.useyourapps.main_activity;

import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shinoharanaoki.useyourapps.Globals;
import com.example.shinoharanaoki.useyourapps.R;
import com.example.shinoharanaoki.useyourapps.models.MonitoringApp;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final static String TAG = "MainActivityFragment";

    private View mView;
    private RecyclerView mRecyclerView = null;
    private RecyclerAdapter mAdapter = null;

    private MyBroadcastReceiver myReceiver;
    private IntentFilter intentFilter;

    private Globals globals;

    /**他のActivity,ServiceからRecyclerViewを更新させるための時用*/
    private final MainActivityFragment self = this;


    public MainActivityFragment() {
    }

    //本当は要らないが、dbhelperはonCreate内で定義しないといけないらしい...
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        globals = (Globals) getActivity().getApplication();
        globals.setActiveFragment(self);

        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        //FIXME addItemDecoration...エラーになるので保留
        /*mRecyclerView.addItemDecoration(new ListItemDecoration(
                //getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                20, //
                //getResources().getInteger(R.integer.photo_list_preview_columns)));
                1) //�1)
        );*/
        // RecyclerViewの参照を取得
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        // レイアウトマネージャを設定(ここで縦方向の標準リストであることを指定)
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        setupRecyclerView(mRecyclerView);

        return mView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        mAdapter = new RecyclerAdapter(globals.appList);
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "setupRecyclerView: ");
    }

    public void addApp(MonitoringApp app){

        mAdapter.notifyItemInserted(0);

    }

    public void removeApp(MonitoringApp app){

        mAdapter.notifyItemRemoved(0);
    }

    public void updateListView(){
        mAdapter.notifyDataSetChanged();
    }
}
