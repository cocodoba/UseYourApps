package com.example.shinoharanaoki.useyourapps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shinoharanaoki on 2016/06/14.
 */
public class AppDataHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "task_data";
    private static final int DATABASE_VERSION = 1;

    public AppDataHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //create table
        db.execSQL(AppDataDao.CREATE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
