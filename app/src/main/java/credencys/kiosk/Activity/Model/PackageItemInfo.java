package credencys.kiosk.Activity.Model;

import android.graphics.drawable.Drawable;

/**
 * Created by Credencys on 18/07/2015.
 */
public class PackageItemInfo {
    /**
     * Public name of this item. From the "android:name" attribute.
     */
    public String name;
    /**
     * Name of the package that this item is in.
     */
    public String packageName;
    /**
     * Icon of the package that this item is in.
     */
    public Drawable icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
