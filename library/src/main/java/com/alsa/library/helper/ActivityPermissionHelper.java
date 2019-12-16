package com.alsa.library.helper;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * ActivityPermissionHelper [ 6.0及以上系统使用该帮助类 ]
 * created by alsa on 2019/10/31
 */
public class ActivityPermissionHelper extends PermissionHelper {

    ActivityPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(getHost(), permissions, requestCode);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String deniedPermission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(getHost(), deniedPermission);
    }
}
