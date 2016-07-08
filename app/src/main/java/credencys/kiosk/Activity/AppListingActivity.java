package credencys.kiosk.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.credencys.kiosk.R;

import java.util.ArrayList;
import java.util.List;

import credencys.kiosk.Activity.Adapter.GridApplicationAdapter;

public class AppListingActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private GridApplicationAdapter listadaptor = null;
    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_listing);
        gridView = (GridView) findViewById(R.id.gridView);
        float scalefactor = getResources().getDisplayMetrics().density * 100;
        int number = getWindowManager().getDefaultDisplay().getWidth();
        int columns = (int) ((float) number / (float) scalefactor);
        gridView.setNumColumns(columns);
        gridView.setOnItemClickListener(this);
        packageManager = getPackageManager();
        new LoadApplications().execute();

    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ApplicationInfo app = applist.get(position);
        try {
            Intent intent = packageManager
                    .getLaunchIntentForPackage(app.packageName);
/**/
            if (null != intent) {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(AppListingActivity.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(AppListingActivity.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                // if (null != packageManager.getLaunchIntentForPackage(info.packageName) && info.packageName.equalsIgnoreCase("com.diplomat.cabdroid.cabdroid")) {
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    applist.add(info);
               // }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    return applist;
}


private class LoadApplications extends AsyncTask<Void, Void, Void> {
    private ProgressDialog progress = null;

    @Override
    protected Void doInBackground(Void... params) {
        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        listadaptor = new GridApplicationAdapter(AppListingActivity.this,
                R.layout.snippet_grid_row, applist);

        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (applist.size() > 0) {
            gridView.setAdapter(listadaptor);
        } else {
            Toast.makeText(AppListingActivity.this, "No apps available", Toast.LENGTH_SHORT);
        }
        progress.dismiss();
//            if (!isMyAppLauncherDefault()) {
//                makePrefered();
//            }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = ProgressDialog.show(AppListingActivity.this, null,
                "Loading application info...");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}


}
