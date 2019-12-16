package com.alsa.library.helper;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * PermissionHelper [ 自定义权限帮助类 ]
 * created by alsa on 2019/10/31
 */
public abstract class PermissionHelper {

    private Activity activity;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public static PermissionHelper newInstance(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionHelper(activity);
        }
        return new ActivityPermissionHelper(activity);
    }

    Activity getHost() {
        return activity;
    }

    /**
     * 用户申请权限
     *
     * @param requestCode 请求标识码
     * @param permissions 需要授权的权限组
     */
    public abstract void requestPermissions(int requestCode, String... permissions);

    /**
     * 用户点击拒绝后，下次是否弹出授权提醒
     *
     * @param deniedPermission 拒绝的权限
     * @return 点击了拒绝但没有勾选“不再询问”：true|点击了拒绝且勾选了“不再询问”：false
     */
    public abstract boolean shouldShowRequestPermissionRationale(@NonNull String deniedPermission);

    /**
     * 检查被拒绝的权限组中，是否有勾选了“不再询问”的权限
     *
     * @param deniedPermissions 拒绝的权限组
     * @return 任一权限勾选了“不再询问”：true|所有权限未勾选“不再询问”：false
     */
    public boolean somePermissionPermanentlyDenied(@NonNull List<String> deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (!shouldShowRequestPermissionRationale(deniedPermission)) {
                return true;
            }
        }
        return false;
    }
}
