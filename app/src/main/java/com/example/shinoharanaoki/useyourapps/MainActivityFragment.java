package com.example.shinoharanaoki.useyourapps;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView = null;
    private RecyclerAdapter mAdapter = null;

    private AppDataDao mdao;
    private AppDataHelper dbHelper;
    private SQLiteDatabase db;

    public MainActivityFragment() {
    }

    //本当は要らないが、dbhelperはonCreate内で定義しないといけないらしい...
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        dbHelper = new AppDataHelper(getContext());
        db = dbHelper.getWritableDatabase();
        mdao = new AppDataDao(db);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        //TODO addItemDecoration...エラーになるので保留
        /*mRecyclerView.addItemDecoration(new ListItemDecoration(
                //getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                20, //
                //getResources().getInteger(R.integer.photo_list_preview_columns)));
                1) //�1)
        );*/
        //to[2]
        // RecyclerViewの参照を取得
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        // レイアウトマネージャを設定(ここで縦方向の標準リストであることを指定)
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        setupRecyclerView(mRecyclerView);

        return mView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        mAdapter = new RecyclerAdapter(getActivity(),makeList());
        recyclerView.setAdapter(mAdapter);
        //to[4]
    }

    private ArrayList<MonitoringApp> makeList() {

        //SQLデータベースからApp型リストへ変換するメソッド
        ArrayList list = mdao.findAll();
        return list;
    }

    public void addApp(MonitoringApp app){

        mdao.save(app);
        mAdapter.notifyItemInserted(0);

    }
}
