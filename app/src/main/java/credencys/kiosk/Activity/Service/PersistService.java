package credencys.kiosk.Activity.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import credencys.kiosk.Activity.Util.Constant;
import credencys.kiosk.Activity.Util.MyApplicationClass;

public class PersistService extends Service {

    private static final long INTERVAL = 500;
    //TimeUnit.SECONDS.toMillis(2);
    //TimeUnit.SECONDS.toMillis(2); // periodic interval to check in seconds -> 2 seconds
    private static final String YOUR_APP_PACKAGE_NAME = "credencys.kiosk";
    MyApplicationClass myApplicationClass;

    private int statusBarHeight;
    private CustomViewGroup view;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplicationClass = (MyApplicationClass) getApplicationContext();
        myApplicationClass.setBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE, true);
        statusBarHeight = (int) Math.ceil(25 * getResources().getDisplayMetrics().density);
        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = statusBarHeight;
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        view = new CustomViewGroup(this);
        manager.addView(view, localLayoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constant.TAG, "IS_KIOSK_MODE_ACTIVE :: onDestroy Service" + myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE));
        if (view != null) {
            WindowManager windowManager = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
            if (view.isShown()) {
                windowManager.removeViewImmediate(view);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(Constant.TAG, "IS_KIOSK_MODE_ACTIVE ::onStartCommand " + myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE));
        // Start your (polling) task
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // If you wish to stop the task/polling
                // Log.d(Constant.TAG, "IS_KIOSK_MODE_ACTIVE :: TimerTask " + myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE));
                if (myApplicationClass.getBooleanPrefrence(Constant.IS_KIOSK_MODE_ACTIVE) == false) {
                    cancel();
                    stopSelf();
                } else {
                    // The first in the list of RunningTasks is always the foreground task.
                    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = manager.getRunningTasks(1).get(0);
                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
                    //   Log.d(Constant.TAG, "foregroundTaskPackageName----" + foregroundTaskPackageName);
                    // Check foreground app: If it is not in the foreground... bring it!


//                    if (foregroundTaskPackageName.equals("") || foregroundTaskPackageName.contains("com.android.launcher") || foregroundTaskPackageName.contains("com.android.systemui")) {
//                        //   Log.d("Raja", foregroundTaskPackageName);
//                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(YOUR_APP_PACKAGE_NAME);
//                        startActivity(LaunchIntent);
//                    } else
                    if (foregroundTaskPackageName.equals(YOUR_APP_PACKAGE_NAME) || foregroundTaskPackageName.contains("com.mapfactor.navigator") || foregroundTaskPackageName.contains("com.android.gallery") || foregroundTaskPackageName.equals("com.google.android.music")
                            || foregroundTaskPackageName.equalsIgnoreCase("com.diplomat.cabdroid") || foregroundTaskPackageName.equalsIgnoreCase("android") || foregroundTaskPackageName.equalsIgnoreCase("com.android.music")) {
                    } else {
                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(YOUR_APP_PACKAGE_NAME);
                        startActivity(LaunchIntent);

                    }
                }
            }

        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, INTERVAL);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class CustomViewGroup extends ViewGroup {

        public CustomViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }
}
