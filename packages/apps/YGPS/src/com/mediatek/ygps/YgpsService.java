
package com.mediatek.ygps;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class YgpsService extends Service {

    private static final String TAG = "EM/YGPS_Service";
    protected static final String SERVICE_START_ACTION = "com.mediatek.ygps.YgpsService";
    private static final int NTF_ID_YGPS_FORGROUND = 10;
    private boolean mForeground = false;
    private Binder mBinder = new LocalYgpsBinder();

    /**
     * request service as foreground service.
     * @param requester activity class
     */
    public void requestForeground(Class<? extends Activity> requester) {
        startForeground(NTF_ID_YGPS_FORGROUND, buildNotification(requester));
        mForeground = true;
    }

    private Notification buildNotification(Class<? extends Activity> clazz) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert).
        setContentTitle("YGPS run in background").
        setContentText("Tap here enter YGPS");
        Intent intent = new Intent(this, clazz);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pi);
        return builder.build();
    }

    /**
     * dismiss foreground notification.
     */
    public void dismissForeground() {
        stopForeground(true);
        mForeground = false;
    }

    @Override
    public void onCreate() {
        Log.v("@M_" + TAG, "YGPSService onCreate");
        // sSelf = this;
        // mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    /**
     * tell whether service run as foreground service.
     * @return true if service was run as foreground
     */
    public boolean isForeground() {
        return mForeground;
    }

    @Override
    public void onDestroy() {
        Log.v("@M_" + TAG, "YGPSService onDestroy");
        if (mForeground) {
            dismissForeground();
        }
        // mNM.cancel(R.string.mobilelog_service_start);
        // mNM.cancelAll();
        // sSelf = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("@M_" + TAG, "onStartCommand " + intent + " flags " + flags);
        return START_STICKY;
    }

    /**
     * ygps local binder.
     * @author: mtk
     */
    class LocalYgpsBinder extends Binder {
        public YgpsService getServiceInstance() {
            return YgpsService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
