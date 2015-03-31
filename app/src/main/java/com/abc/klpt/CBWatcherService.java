package com.abc.klpt;

/**
 * Created by abhishekrai on 3/13/15.
 */

import android.app.Notification;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CBWatcherService extends Service {

    String mPreviousText = "";
    ClipboardManager myClipBoard ;
    static boolean bHasClipChangedListener = false;

    private OnPrimaryClipChangedListener listener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };

    private void RegPrimaryClipChanged(){
        if(!bHasClipChangedListener){
            myClipBoard.addPrimaryClipChangedListener(listener);
            bHasClipChangedListener = true;
        }
    }
    private void UnRegPrimaryClipChanged(){
        if(bHasClipChangedListener){
            myClipBoard.removePrimaryClipChangedListener(listener);
            bHasClipChangedListener = false;
        }
    }

    @Override
    public void onCreate() {
        myClipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UnRegPrimaryClipChanged();
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RegPrimaryClipChanged();
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Clip On!")
                .setPriority(Notification.PRIORITY_MIN).build();
        startForeground(7301,notification);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void performClipboardCheck() {
        try{
            DbHandler db = new DbHandler(getApplicationContext());
            ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cb.hasPrimaryClip()) {
                ClipData cd = cb.getPrimaryClip();
                try {
                    if (mPreviousText.equals(cd.getItemAt(0).getText().toString())) return;
                    else {
                        String clipboardText = cd.getItemAt(0).getText().toString();
                        db.addClipboardText(clipboardText);
                        mPreviousText = clipboardText;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception ex){
            Log.e("Error while adding :",ex.getMessage());
        }
    }
}
