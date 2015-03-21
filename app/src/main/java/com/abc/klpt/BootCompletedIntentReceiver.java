package com.abc.klpt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by abhishekrai on 3/20/15.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedpreferences;
        sharedpreferences = context.getSharedPreferences("kltp", Context.MODE_PRIVATE);
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) && sharedpreferences.getBoolean("enable",false)) {
            Intent pushIntent = new Intent(context, CBWatcherService.class);
            context.startService(pushIntent);
        }
    }
}