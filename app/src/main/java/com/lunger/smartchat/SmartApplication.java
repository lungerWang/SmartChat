package com.lunger.smartchat;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;


/**
 * Created by Lunger on 2016/11/29.
 */
public class SmartApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=583d2092");
    }
}
