package credencys.kiosk.Activity.Util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.PowerManager;

import java.util.Set;

import credencys.kiosk.Activity.BroadcastReceiver.OnScreenOffReceiver;
import credencys.kiosk.Activity.Service.PersistService;

/**
 * Created by Credencys on 18/07/2015.
 */
public class MyApplicationClass extends Application {

    private SharedPreferences sharedPreferences;
    private MyApplicationClass instance;
    private PowerManager.WakeLock wakeLock;
    private OnScreenOffReceiver onScreenOffReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(Constant.PACKAGE_NAME, MODE_PRIVATE);
        instance = this;
        registerKioskModeScreenOffReceiver();
        startKioskService();
    }

    private void registerKioskModeScreenOffReceiver() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        registerReceiver(onScreenOffReceiver, filter);
    }

    private void startKioskService() { // ... and this method
        setBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE, true);
        startService(new Intent(this, PersistService.class));
    }

    public PowerManager.WakeLock getWakeLock() {
        if (wakeLock == null) {
            // lazy loading: first call, create wakeLock via PowerManager.
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
        }
        return wakeLock;
    }

    public void storeIntPrefrence(String key, Integer value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public Integer getIntPrefrence(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void storeStringSetPrefrence(String key, Set<String> value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public Set<String> getStringSetPrefrence(String key) {
        return sharedPreferences.getStringSet(key, null);
    }

    public boolean getBooleanPrefrence(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void setBooleanPrefrence(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getStringPrefrence(String key) {
        if (key.equalsIgnoreCase(Constant.USERNAME)) {
            return sharedPreferences.getString(key, "Test");
        } else if (key.equalsIgnoreCase(Constant.PASSWORD)) {
            return sharedPreferences.getString(key, "1234");
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    public void setStringPrefrence(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
