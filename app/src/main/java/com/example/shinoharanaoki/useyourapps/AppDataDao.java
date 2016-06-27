package com.example.shinoharanaoki.useyourapps;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by shinoharanaoki on 2016/06/14.
 */
public class AppDataDao {

    private static final String TABLE_NAME = "application";

    private static final String COLUMN_APP_NAME = "app_name";
    private static final String COLUMN_PACKAGE_NAME = "package_name";
    private static final String COLUMN_END_TIME = "last_use_time";
    private static final String COLUMN_TOTAL_USE = "total_use_time";
    private static final String COLUMN_CREDIT = "total_credits";

    //private static final String COLUMN_POSITION_ON_LIST = "position";


    private static final String[] COLUMNS = {
            COLUMN_APP_NAME,
            COLUMN_PACKAGE_NAME,
            COLUMN_END_TIME,
            COLUMN_TOTAL_USE,
            COLUMN_CREDIT};

    private SQLiteDatabase db;

    //TODO
    public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_APP_NAME + " TEXT, "
            + COLUMN_PACKAGE_NAME + " TEXT PRIMARY KEY, "
            + COLUMN_END_TIME + " LONG, "
            + COLUMN_TOTAL_USE + " LONG, "
            + COLUMN_CREDIT + " INTEGER)";




    public AppDataDao(SQLiteDatabase db){
        this.db = db;
    }

    public ArrayList<MonitoringApp> findAll() {

        ArrayList<MonitoringApp> list = new ArrayList<>();
        //query生成
        Cursor c = db.query(
                TABLE_NAME, //The table name to compile the query against.
                COLUMNS, //A list of which columns to return.
                null, //selection
                null, //selection args
                null, //group by
                null, //having
                null); //順番

        while (c.moveToNext()) {
            MonitoringApp app =
                    new MonitoringApp(c.getString(c.getColumnIndex(COLUMN_APP_NAME)),
                                      c.getString(c.getColumnIndex(COLUMN_PACKAGE_NAME)));

            app.setLastTime(c.getLong(c.getColumnIndex(COLUMN_END_TIME)));
            app.setUseTime(c.getLong(c.getColumnIndex(COLUMN_TOTAL_USE)));
            app.setCredit(c.getInt(c.getColumnIndex(COLUMN_CREDIT)));

            list.add(app);
        }
        c.close();

        return list;

    }



    public MonitoringApp find(String pname) {
        // query生成
        Cursor c = db.query(TABLE_NAME,
                COLUMNS,
                COLUMN_PACKAGE_NAME + " = ?",
                new String[] { String.valueOf(pname) },
                null,
                null,
                null);

        MonitoringApp app = null;

        // 1行だけfetch
        if (c.moveToFirst()) {
            app = new MonitoringApp(c.getString(c.getColumnIndex(COLUMN_APP_NAME)),
                            c.getString(c.getColumnIndex(COLUMN_PACKAGE_NAME)));

            app.setLastTime(c.getInt(c.getColumnIndex(COLUMN_END_TIME)));
            app.setUseTime(c.getInt(c.getColumnIndex(COLUMN_TOTAL_USE)));
            app.setCredit(c.getInt(c.getColumnIndex(COLUMN_CREDIT)));
        }

        // cursorのclose
        c.close();

        return app;
    }

    /**
     * 保存
     *
     * @param app
     * @return
     */

    //TODO saveAll()を用意する

    public long save(MonitoringApp app) {
        /*if (!app.validate()) {
            // validationチェックにひっかかったら保存しない
            return -1;
        }*/
        // 値設定
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_NAME, app.getApplicationName());
        values.put(COLUMN_TOTAL_USE,app.getUseTime());
        values.put(COLUMN_END_TIME, app.getLastTimeUsed());
        values.put(COLUMN_CREDIT, app.getCredit());

        if (exists(app.getPackageName())) {
            // データすでに存在するなら更新
            String where = COLUMN_PACKAGE_NAME + " = ?";
            String[] arg = { String.valueOf(app.getPackageName()) };
            return db.update(TABLE_NAME, values, where, arg);
        } else {
            // データがまだないなら挿入
            values.put(COLUMN_PACKAGE_NAME, app.getPackageName());
            return db.insert(TABLE_NAME, null, values);
        }
    }


    public int delete(String pname) {
        String whereClause = COLUMN_PACKAGE_NAME + "=" + pname;
        return db.delete(TABLE_NAME, whereClause, null);
    }

    /**
     * 日付で存在チェック
     *
     * @param packageName
     * @return
     */
    public boolean exists(String packageName) {
        return find(packageName) != null;
    }

    /**
     * データベースが空かどうかチェック
     *
     * @return
     */
    public boolean exists() {
        return findAll().size() > 0;
    }


}



