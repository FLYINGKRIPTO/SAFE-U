package com.womensafety.shajt3ch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class PanicAlarm extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmQuest","Here");
        context.startService(new Intent(context, FloatingViewService.class));
    }

}
