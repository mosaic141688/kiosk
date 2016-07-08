package credencys.kiosk.Activity.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import credencys.kiosk.Activity.Util.Constant;
import credencys.kiosk.Activity.Util.MyApplicationClass;

public class OnScreenOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            MyApplicationClass myApplicationClass = (MyApplicationClass) context.getApplicationContext();
            // is Kiosk Mode active?
            if (myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE)) {
                System.out.println("OnScreenOffReceiver");
                wakeUpDevice(myApplicationClass);
            }
        }
    }

    private void wakeUpDevice(MyApplicationClass context) {
        PowerManager.WakeLock wakeLock = context.getWakeLock(); // get WakeLock reference via AppContext
        if (wakeLock.isHeld()) {
            wakeLock.release(); // release old wake lock
        }
        // create a new wake lock...
        wakeLock.acquire();
        // ... and release again
        wakeLock.release();
    }


}