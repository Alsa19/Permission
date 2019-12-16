package com.alsa.library.helper;

import android.app.Activity;

import androidx.annotation.NonNull;

/**
 * LowApiPermissionHelper [ 6.0以下系统使用此帮助类 ]
 * created by alsa on 2019/10/31
 */
public class LowApiPermissionHelper extends PermissionHelper {

    LowApiPermissionHelper(Activity activity) {
        super(activity);
    }

    @Override
    public void requestPermissions(int requestCode, String... permissions) {
        throw new IllegalStateException("低于6.0版本无须运行时请求权限");
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String deniedPermission) {
        return false;
    }
}
