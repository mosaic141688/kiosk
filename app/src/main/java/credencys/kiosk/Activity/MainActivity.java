package credencys.kiosk.Activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.credencys.kiosk.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import credencys.kiosk.Activity.Service.FloatingViewService;
import credencys.kiosk.Activity.Service.PersistService;
import credencys.kiosk.Activity.Util.Constant;
import credencys.kiosk.Activity.HomeKeyLocker.HomeKeyLocker;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    static final int REQUEST_TAKE_PHOTO = 1;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    MyPhoneStateListener myPhoneStateListener;
    String strGSMData;
    String operatorName;
    String mCurrentPhotoPath;
    private RelativeLayout btnMap;
    private RelativeLayout btnCall;
    private RelativeLayout btnApp;
    private RelativeLayout btnMusic;
    private RelativeLayout btnSetting;
    private RelativeLayout btnCapture;
    private TextView txtCurrentDate, txtCurrentTime, txtLocation;
    private RelativeLayout rel_FirstView, rel_SecondView;
    private AudioManager audioManager;
    private Dialog dialog;
    private TelephonyManager telephonyManager;

    private HomeKeyLocker homeKeyLocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        homeKeyLocker = new HomeKeyLocker();

        myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        operatorName = telephonyManager.getNetworkOperatorName();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myApplicationClass.storeIntPrefrence(Constant.VOLUME, audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        initView();
        threadForShowTimeOnDashboard();
        if (myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE) == false) {
            startService(new Intent(this, PersistService.class));
        }
//        if (!isMyAppLauncherDefault()) {
//            makePrefered();
//        }
//        Toast.makeText(this, "Please select this application as default launcher", Toast.LENGTH_LONG).show();
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo foregroundTaskInfo = manager.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

/*        if (!foregroundTaskPackageName.contains("com.mapfactor.navigator")){
            stopService(new Intent(getApplicationContext(), FloatingViewService.class));
        }else if (foregroundTaskPackageName.equalsIgnoreCase("com.diplomat.cabdroid")){
            stopService(new Intent(getApplicationContext(), FloatingViewService.class));
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE) == false) {
            finish();
        }
    }

    public void makePrefered1() {
        final PackageManager packageManager = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        startActivity(selector);
    }


    @Override
    public void onBackPressed() {
        Log.d(Constant.TAG, "IS_KIOSK_MODE_ACTIVE :: onBackPressed" + myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE));
        if (myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE) == false) {
            finish();
        }
    }

    /**
     * This method is used for the set the time on dashborad with using thread.
     */
    private void threadForShowTimeOnDashboard() {
        Thread myThread;
        Runnable myRunnableThread = new CountDownRunner();
        myThread = new Thread(myRunnableThread);
        myThread.start();
    }

    /**
     * initialization of all views
     */
    private void initView() {
        // initCameraView();
        btnMap = (RelativeLayout) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(this);
        btnApp = (RelativeLayout) findViewById(R.id.btnApp);
        btnApp.setOnClickListener(this);
        btnMusic = (RelativeLayout) findViewById(R.id.btnMusic);
        btnMusic.setOnClickListener(this);
        btnCall = (RelativeLayout) findViewById(R.id.btnCall);
        btnCall.setOnClickListener(this);
        btnSetting = (RelativeLayout) findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(this);
        btnCapture = (RelativeLayout) findViewById(R.id.btnCapture2);
        btnCapture.setOnClickListener(this);
        txtCurrentTime = (TextView) findViewById(R.id.txtTime);
        txtCurrentDate = (TextView) findViewById(R.id.txtDate);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
//        if (locationUser != null) {
//            txtLocation.setText("Gps : " + locationUser.getLatitude() + " " + locationUser.getLongitude());
//        }
        rel_FirstView = (RelativeLayout) findViewById(R.id.rel_FirstView);
        rel_FirstView.setVisibility(View.VISIBLE);


    }


    /**
     * This method is used for the change the time in textview.
     */
    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                    Date now = new Date();
                    String curTime = sdfTime.format(now);
                    txtCurrentTime.setText(curTime);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd");
                    String date = simpleDateFormat.format(now);
                    txtCurrentDate.setText(date.toUpperCase());
                    if (locationUser != null) {
                        double lat = locationUser.getLatitude();
                        lat = Double.parseDouble(new DecimalFormat("##.#######").format(lat));
                        double lon = locationUser.getLongitude();
                        lon = Double.parseDouble(new DecimalFormat("##.#######").format(lon));
                        txtLocation.setText("Gps : " + lat + ", " + lon + ",  " + operatorName + " " + strGSMData);
                    } else {
                        txtLocation.setText(operatorName + " " + strGSMData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCapture2:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
                break;
            case R.id.btnMap:
                try {
                    startService(new Intent(getApplicationContext(), FloatingViewService.class));
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.mapfactor.navigator");
                    startActivity(LaunchIntent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnCall:
//                counter++;
//                if (counter >= 3) {
//                    stopService(new Intent(MainActivity.this, PersistService.class));
//                }
                break;
            case R.id.btnApp:

//                Intent intentApp = new Intent(this, AppListingActivity.class);
//                startActivity(intentApp);
                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ActivityManager.RunningTaskInfo foregroundTaskInfo = manager.getRunningTasks(1).get(0);
                String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

                if (!foregroundTaskPackageName.contains("com.mapfactor.navigator")){
                    stopService(new Intent(getApplicationContext(), FloatingViewService.class));
                }else if (foregroundTaskPackageName.equalsIgnoreCase("com.diplomat.cabdroid")){
                    stopService(new Intent(getApplicationContext(), FloatingViewService.class));
                }
                try {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.diplomat.cabdroid");
                    startActivity(LaunchIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnMusic:
//                Intent intentMusic = new Intent("android.intent.action.MUSIC_PLAYER");
//                startActivity(intentMusic);
                break;
            case R.id.btnSetting:
                Intent intentSetting = new Intent(this, SettingActivity.class);
                startActivity(intentSetting);
                // showDialog();
                break;
            default:
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "KIOSK_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

//    private void showDialog1() {
//        dialog = new Dialog(MainActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_login);
//        dialog.setCancelable(true);
//        // final EditText edtUserName = (EditText) dialog.findViewById(R.id.edtUserName);
//        final EditText edtPasswrod = (EditText) dialog.findViewById(R.id.edtPassword);
//        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (edtPasswrod.getText().toString().equalsIgnoreCase(myApplicationClass.getStringPrefrence(Constant.PASSWORD))) {
//                    dialog.dismiss();
//                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
//                } else {
//                    Toast.makeText(MainActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
//                    edtPasswrod.setText("");
//                    edtPasswrod.requestFocus();
//                }
//
//
//            }
//        });
//        edtPasswrod.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    if (edtPasswrod.getText().toString().equalsIgnoreCase(myApplicationClass.getStringPrefrence(Constant.PASSWORD))) {
//                        dialog.dismiss();
//                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
//                    } else {
//                        Toast.makeText(MainActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
//                        edtPasswrod.setText("");
//                        edtPasswrod.requestFocus();
//                    }
//                }
//                return false;
//            }
//        });
//        dialog.show();
//    }


    public void setCameraDisplayOrientation(
            int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = this.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private String checkSignal() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        String str = null;
        //For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();

        if (is3g) {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            CellInfoGsm cellinfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
            CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
            str = "GSM Signal Strength" + cellSignalStrengthGsm.getDbm();
        }
        //For WiFi Check

        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
        if (isWifi) {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            str = "Wifi Signal Strength" + wifiManager.getConnectionInfo().getRssi();
        }
        return str;
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        /* Get the Signal strength from the provider, each tiome there is an update */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            boolean isGsm = signalStrength.isGsm();
            // Get the CDMA RSSI value in dBm
            int iCdmaDbm = signalStrength.getCdmaDbm();
            // Get the CDMA Ec/Io value in dB*10
            int iCdmaEcio = signalStrength.getCdmaEcio();
            // Get the EVDO RSSI value in dBm
            int iEvdoDbm = signalStrength.getEvdoDbm();
            // Get the EVDO Ec/Io value in dB*10
            int iEvdoEcio = signalStrength.getEvdoEcio();
            // Get the signal to noise ratio. Valid values are 0-8. 8 is the highest.
            int iEvdoSnr = signalStrength.getEvdoSnr();
            // Get the GSM bit error rate (0-7, 99) as defined in TS 27.007 8.5
            int iGsmBitErrorRate = signalStrength.getGsmBitErrorRate();
            // Get the GSM Signal Strength, valid values are (0-31, 99) as defined in TS 27.007 8.5
            int iGsmSignalStrength = signalStrength.getGsmSignalStrength();

            String str = "GSM=" + isGsm
                    + ",GSM Signal Strength=" + iGsmSignalStrength
                    + ",GSM Bit Error Rate=" + iGsmBitErrorRate
                    + ",CDMA RSSI=" + iCdmaDbm + "dBm"
                    + ",CDMA Ec/Io=" + iCdmaEcio + "dB*10"
                    + ",EVDO RSSI=" + iEvdoDbm + "dBm"
                    + ",EVDO Ec/Io=" + iEvdoEcio + "dB*10"
                    + ",EVDO SNR=" + iEvdoSnr
                    + "\n";
            strGSMData = "\n Tel Signal Strength " + signalStrength.getGsmSignalStrength();
            Log.v(Constant.TAG, str);

        }

    }

    /**
     * Timer class call every 1 minute to update the UI.
     */
    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(60000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

}

