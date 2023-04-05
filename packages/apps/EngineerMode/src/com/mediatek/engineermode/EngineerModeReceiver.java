
package com.mediatek.engineermode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


public final class EngineerModeReceiver extends BroadcastReceiver {

    private static final String TAG = "EM/SECRET_CODE";
    private static final String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";
    // process *#*#3646633#*#*
    private final Uri mEmUri = Uri.parse("android_secret_code://3646633");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            Log.i("@M_" + TAG, "Null action");
            return;
        }
        if (intent.getAction().equals(SECRET_CODE_ACTION)) {
            Uri uri = intent.getData();
            Log.i("@M_" + TAG, "getIntent success in if");
            if (uri.equals(mEmUri)) {
                Intent intentEm = new Intent(context, EngineerMode.class);
                intentEm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.i("@M_" + TAG, "Before start EM activity");
                context.startActivity(intentEm);
            } else {
                Log.i("@M_" + TAG, "No matched URI!");
            }
        } else {
            Log.i("@M_" + TAG, "Not SECRET_CODE_ACTION!");
        }
    }
}
