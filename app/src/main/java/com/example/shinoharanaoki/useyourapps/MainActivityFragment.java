package com.example.shinoharanaoki.useyourapps;

import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shinoharanaoki.useyourapps.models.MonitoringApp;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView = null;
    private RecyclerAdapter mAdapter = null;

    private MyBroadcastReceiver myReceiver;
    private IntentFilter intentFilter;

    private Globals globals;


    public MainActivityFragment() {
    }

    //本当は要らないが、dbhelperはonCreate内で定義しないといけないらしい...
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        myReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACTION");
        getActivity().registerReceiver(myReceiver, intentFilter);

        myReceiver.registerHandler(updateHandler);

        globals = (Globals) getActivity().getApplication();

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

    @Override
    public void onResume() {
        super.onResume();
        myReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACTION");
        getActivity().registerReceiver(myReceiver, intentFilter);

        myReceiver.registerHandler(updateHandler);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(myReceiver);

    }



    private void setupRecyclerView(RecyclerView recyclerView) {

        mAdapter = new RecyclerAdapter(getActivity(),makeList());
        recyclerView.setAdapter(mAdapter);
        //to[4]
    }

    private ArrayList<MonitoringApp> makeList() {

        //SQLデータベースからApp型リストへ変換するメソッド
        ArrayList list = globals.appList;
        return list;
    }

    public void addApp(MonitoringApp app){

        mAdapter.notifyItemInserted(0);

    }

    public void removeApp(MonitoringApp app){

        mAdapter.notifyItemRemoved(0);
    }

    /*サービスから値を受け取ったら動かしたい内容を書く*/
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String message = bundle.getString("message");

            //FIXME リスト変数を更新してアダプタに知らせる
            mAdapter.notifyDataSetChanged();

            //TEST
            Toast.makeText(getContext(),"はんどらーだよ" + message,Toast.LENGTH_SHORT);

        }
    };
}
