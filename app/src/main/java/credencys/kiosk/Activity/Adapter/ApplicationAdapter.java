package credencys.kiosk.Activity.Adapter;

/**
 * Created by Credencys on 17/07/2015.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.credencys.kiosk.R;

import java.util.ArrayList;
import java.util.List;

import credencys.kiosk.Activity.Util.Constant;
import credencys.kiosk.Activity.Util.MyApplicationClass;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
    MyApplicationClass myApplicationClass;
    List<String> strList;
    private List<ApplicationInfo> appsList = null;
    private Context context;
    private PackageManager packageManager;

    public ApplicationAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        myApplicationClass = (MyApplicationClass) context;
        if (myApplicationClass.getStringSetPrefrence(Constant.APPLIST) != null) {
            strList = new ArrayList<String>(myApplicationClass.getStringSetPrefrence(Constant.APPLIST));
        }
        this.appsList = appsList;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_list_row, null);
        }

        ApplicationInfo applicationInfo = appsList.get(position);
        if (null != applicationInfo) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            appName.setText(applicationInfo.loadLabel(packageManager));
            packageName.setText(applicationInfo.packageName);
            iconview.setImageDrawable(applicationInfo.loadIcon(packageManager));
        }
        return view;
    }
};
