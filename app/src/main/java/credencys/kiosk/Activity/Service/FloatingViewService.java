package credencys.kiosk.Activity.Service;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.credencys.kiosk.R;

import credencys.kiosk.Activity.Util.Constant;

public class FloatingViewService extends Service {

    private WindowManager windowManager;
    private boolean mIsFloatingViewAttached = false;
    private ImageButton imageButton;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("ResourceAsColor")
    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        imageButton = new ImageButton(this);
        imageButton.setImageResource(R.mipmap.ic_back_to_kuber);
        //imageButton.setBackground(R.drawable.back_to_kuber);
        imageButton.setBackgroundColor(R.drawable.back_to_kuber);


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.CENTER|Gravity.RIGHT;

        windowManager.addView(imageButton, params);

        imageButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                     {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constant.LAUNCH_KUBER);
                            startActivity(LaunchIntent);
                            /*String defaultApplication = Settings.Secure.getString(getContentResolver(), Constant.LAUNCH_KUBER);
                            PackageManager pm = getPackageManager();
                            Intent intent = pm.getLaunchIntentForPackage(defaultApplication );
                            if (intent != null) {
                                startActivity(intent);
                            }*/
                        } else {
                            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(Constant.LAUNCH_KUBER);
                            startActivity(LaunchIntent);
                            /*String defaultApplication = Settings.Secure.getString(getContentResolver(), Constant.LAUNCH_KUBER);
                            PackageManager pm = getPackageManager();
                            Intent intent = pm.getLaunchIntentForPackage(defaultApplication );
                            if (intent != null) {
                                startActivity(intent);
                            }*/
                            /*Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setType(Constant.LAUNCH_KUBER);
                            startActivity(intent);*/
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        mIsFloatingViewAttached = true;

    }

    public void removeView(){
        if (imageButton != null){
            windowManager.removeView(imageButton);
            mIsFloatingViewAttached = false;
        }
    }


    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT);
        super.onDestroy();
        removeView();
    }


}
