package com.example.shinoharanaoki.useyourapps;

import android.graphics.drawable.Drawable;

/**
 * Created by shinoharanaoki on 2016/06/10.
 */
public class MonitoringApp {

    //TODO 単位の付け方２パターンのための定数（通常・特定の期間に何分使用）

    String applicationName;
    String packageName;
    Drawable icon;
    long useTime;
    long lastTimeUsed;
    long presetInterval;
    long timePerCredit;
    int credit;

    public MonitoringApp(String appname, String packageName){
        applicationName = appname;
        this.packageName = packageName;
        useTime = 0;
        lastTimeUsed = 0;
        presetInterval = 0;
        timePerCredit = 300000; //TODO ダイアログから設定できるようにする (300000 = 5min)
        credit =0;
    }

    public String getApplicationName(){
        return applicationName;
    }
    public String getPackageName(){
        return packageName;
    }
    public Drawable getIcon(){
        return icon;
    }
    public long getUseTime(){
        return useTime;
    }
    public long getLastTimeUsed(){
        return lastTimeUsed;
    }
    public long getInterval(){
        return presetInterval;
    }
    public long getTimePerCredit(){
        return timePerCredit;
    }
    public int getCredit(){
        return credit;
    }
    public void setIcon(Drawable icon){
        this.icon = icon;
    }
    public void setUseTime(long t){
        /*TODO 戻ってきた値が0だった時に、今までの累計が0で上書きされたら困る*/
        if(t != 0){
            useTime += t; // useTime = useTime + t
        }
    }
    public void setLastTime(long t){
        if(lastTimeUsed != t){
        lastTimeUsed = t;
        }
    }
    public void setPresetInterval(long interval){
        presetInterval= interval;
    }
    public void setTimePerCredit(long t){
        timePerCredit = t;
    }
    public void setCredit(int c){
        /*TODO 戻ってきた値が0だった時に、今までの累計が0で上書きされたら困る*/
        if(c != 0){
            credit += c; // credit = credit + c
        }
    }

}
