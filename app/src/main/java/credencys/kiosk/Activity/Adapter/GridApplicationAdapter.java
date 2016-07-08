package credencys.kiosk.Activity.Adapter;

/**
 * Created by Credencys on 17/07/2015.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.credencys.kiosk.R;

import java.util.List;

import credencys.kiosk.Activity.Util.Constant;


public class GridApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
    int iconSize;
    LayoutInflater mLayoutInflater;
    private List<ApplicationInfo> appsLists = null;
    private Context context;
    private PackageManager packageManager;

    public GridApplicationAdapter(Context context, int textViewResourceId,
                                  List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.appsLists = appsList;
//        for (ApplicationInfo appinfo :
//                appsList) {
//            if (appinfo.packageName.equalsIgnoreCase("com.diplomat.cabdroid.cabdroid")) ;
//            appsLists.add(appinfo);
//
//        }
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != appsLists) ? appsLists.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsLists) ? appsLists.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.snippet_grid_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.appName = (TextView) convertView.findViewById(R.id.app_name);
            viewHolder.iconview = (ImageView) convertView.findViewById(R.id.app_icon);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ApplicationInfo applicationInfo = appsLists.get(position);
        if (null != applicationInfo) {
            viewHolder.appName.setText(applicationInfo.loadLabel(packageManager));
            Log.d(Constant.TAG, "" + applicationInfo.loadLabel(packageManager) + " packagename :: " + applicationInfo.packageName);
            viewHolder.iconview.setImageDrawable(applicationInfo.loadIcon(packageManager));
        }
        return convertView;
    }

    class ViewHolder {
        TextView appName;
        ImageView iconview;

    }
};
