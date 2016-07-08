package credencys.kiosk.Activity.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import credencys.kiosk.Activity.MainActivity;
import credencys.kiosk.Activity.Util.Constant;

/**
 * Created by Credencys on 14/07/2015.
 */
public class BootComplete extends BroadcastReceiver {
    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = context.getSharedPreferences("com.credencys.kiosk",
                Context.MODE_PRIVATE);
        if (prefs.getBoolean(Constant.IS_KIOSK_MODE_ACTIVE, false)) {
            Intent myIntent = new Intent(context, MainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }

}
