package credencys.kiosk.Activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.credencys.kiosk.R;

import credencys.kiosk.Activity.Service.PersistService;
import credencys.kiosk.Activity.Util.Constant;


public class SettingActivity extends BaseActivity {//UI objects//

    //Seek bar object
    private SeekBar seekbarBrightness, seekbarVolume;
    //Variable to store brightness value
    private int brightness;
    //Content resolver used as a handle to the system's settings
    private ContentResolver cResolver;
    //Window object, that will store a reference to the current window
    private Window window;

    private AudioManager audioManager = null;

    private TextView txtLogin;
    private Dialog dialog;
    private SettingsContentObserver mSettingsContentObserver;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_setting);
        //Instantiate seekbar object
        mSettingsContentObserver = new SettingsContentObserver(new Handler());
        this.getApplicationContext().getContentResolver().registerContentObserver(
                System.CONTENT_URI, true,
                mSettingsContentObserver);

        manageBrightness();
        manageVolume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
//                myApplicationClass.setBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE, false);
//                stopService(new Intent(SettingActivity.this, PersistService.class));
//                getPackageManager().clearPackagePreferredActivities(getPackageName());
//                finish();
            }
        });
    }

    private void showDialog() {
        dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_login);
        dialog.setCancelable(true);
        // final EditText edtUserName = (EditText) dialog.findViewById(R.id.edtUserName);
        final EditText edtPasswrod = (EditText) dialog.findViewById(R.id.edtPassword);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPasswrod.getText().toString().equalsIgnoreCase(myApplicationClass.getStringPrefrence(Constant.PASSWORD))) {
                    myApplicationClass.setBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE, false);
                    stopService(new Intent(SettingActivity.this, PersistService.class));
                    Intent i = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    getPackageManager().clearPackagePreferredActivities(getPackageName());
                    dialog.dismiss();
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(SettingActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                    edtPasswrod.setText("");
                    edtPasswrod.requestFocus();
                }


            }
        });
        edtPasswrod.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (edtPasswrod.getText().toString().equalsIgnoreCase(myApplicationClass.getStringPrefrence(Constant.PASSWORD))) {
                        myApplicationClass.setBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE, false);
                        stopService(new Intent(SettingActivity.this, PersistService.class));
                        getPackageManager().clearPackagePreferredActivities(getPackageName());
                        dialog.dismiss();
                        Intent i = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(SettingActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                        edtPasswrod.setText("");
                        edtPasswrod.requestFocus();
                    }
                }
                return false;
            }
        });
        dialog.show();
    }


    private void manageBrightness() {
        seekbarBrightness = (SeekBar) findViewById(R.id.seekbarBrightness);
        //Get the content resolver
        cResolver = getContentResolver();
        //Get the current window
        window = getWindow();
        //Set the seekbar range between 0 and 255
        //seek bar settings//
        //sets the range between 0 and 255
        seekbarBrightness.setMax(255);
        //set the seek bar progress to 1
        seekbarBrightness.setKeyProgressIncrement(1);
        try {
            //Get the current system brightness
            brightness = System.getInt(cResolver, System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            //Throw an error case it couldn't be retrieved
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }
        //Set the progress of the seek bar based on the system's brightness
        seekbarBrightness.setProgress(brightness);
        //Register OnSeekBarChangeListener, so it can actually change values
        seekbarBrightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Set the system brightness using the brightness variable value
                System.putInt(cResolver, System.SCREEN_BRIGHTNESS, brightness);
                //Get the current window attributes
                LayoutParams layoutpars = window.getAttributes();
                //Set the brightness of this window
                layoutpars.screenBrightness = brightness / (float) 255;
                //Apply attribute changes to this window
                window.setAttributes(layoutpars);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing handled here
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Set the minimal brightness level
                //if seek bar is 20 or any value below
                if (progress <= 20) {
                    //Set the brightness to 20
                    brightness = 20;
                } else //brightness is greater than 20
                {
                    //Set brightness variable based on the progress bar
                    brightness = progress;
                }
                //Calculate the brightness percentage
                float perc = (brightness / (float) 255) * 100;
                //Set the brightness percentage
            }
        });
    }

    private void manageVolume() {
        try {
            seekbarVolume = (SeekBar) findViewById(R.id.seekbarVolume);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            seekbarVolume.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            seekbarVolume.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
            //   seekbarVolume.setProgress(myApplicationClass.getIntPrefrence(Constant.VOLUME));
            seekbarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                    //  myApplicationClass.storeIntPrefrence(Constant.VOLUME, progress);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
        super.onDestroy();
    }

    public class SettingsContentObserver extends ContentObserver {
        private AudioManager audioManager;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public SettingsContentObserver(Handler handler) {
            super(handler);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

//        public SettingsContentObserver(Context context, Handler handler) {
//        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            seekbarVolume.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
            Log.d(Constant.TAG, "Volume now " + currentVolume);
        }
    }
}
