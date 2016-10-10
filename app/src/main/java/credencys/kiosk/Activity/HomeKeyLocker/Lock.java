package credencys.kiosk.Activity.HomeKeyLocker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.telephony.TelephonyManager;
import android.text.Spanned;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mosaic on 10/7/16.
 */
@SuppressLint({"SimpleDateFormat", "HandlerLeak"})
@SuppressWarnings("deprecated")
@TargetApi(Build.VERSION_CODES.KITKAT)
public class Lock extends Activity implements GestureOverlayView.OnGesturePerformedListener {

    private Handler mainHandler;
    private HomeKeyLocker homeKeyLocker;
    private GestureLibrary gestureLibrary;
    private static boolean isLightOn = false;
    private static Camera camera;

    boolean admin;
    boolean mobileDataEnabled = false;
    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

    int col, colbg;
    int iii = 0;
    int i = 0;

    static TextView battery, Date, Time, text, carrier, data, msgs, calls,
            bluetooth, wifi, sound, whats, pmm;

    static GestureOverlayView gestureOverlayView;
    static TableLayout tableLayout;
    static Bitmap bitmap;

    String Pin, load2, txt, img, color, colorbg, XYZ, carrierName, E,
            proximity, tap, skips;

    Context context;
    AudioManager audiomanage;
    Policy.Parameters p;
    GestureDetectorCompat gDetector;
    PackageManager pm;
    SharedPreferences sse;
    WindowManager.LayoutParams params;
    TelephonyManager manager;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf;
    Intent closeDialog;
    Runnable runnable;
    DevicePolicyManager policyManager;
    ComponentName adminReceiver;
    Window window;
    WindowManager wmanager;
    CustomViewGroup view;
    BluetoothAdapter mBluetoothAdapter;
    WifiManager twifi;
    ConnectivityManager cm;
    AudioManager am;
    TableLayout tab, r1;
    ArrayList<String> al;
    TextView textview;
    Spanned update;
    View v1, v2;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {

    }

    @SuppressLint({ "SimpleDateFormat", "HandlerLeak", "DefaultLocale" })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Lock Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://credencys.kiosk.Activity.HomeKeyLocker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Lock Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://credencys.kiosk.Activity.HomeKeyLocker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // Clears the preferences and all customizations
    //To Avoid Crashes
    public void onCan() {

//        startService(new Intent(Lock.this, LockerService.class));
        finish();

        DataOutputStream dout;

        File mFile = new File(getBaseContext().getFilesDir().getPath() + "Lock");

        try {

            mFile.createNewFile();

        } catch (IOException e1) {

            e1.printStackTrace();

        }

        String Com = "Last Unlocked : " + Time.getText().toString();

        try {

            dout = new DataOutputStream(new FileOutputStream(mFile));
            dout.writeBytes(Com);

            dout.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    /**
     *
     * To Avoid Screen Rotation and Force Closes
     *
     * */
    @Override
    public void onConfigurationChanged(Configuration myConfig){
        super.onConfigurationChanged(myConfig);

        int orient = getResources().getConfiguration().orientation;

        switch(orient){

            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;

            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    }
}
