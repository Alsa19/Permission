package com.alsa.library;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.alsa.library.annotations.IPermission;
import com.alsa.library.dialog.AppSettingDialog;
import com.alsa.library.helper.PermissionHelper;
import com.alsa.library.listener.PermissionCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * PermissionManager [ 权限请求管理器 ]
 * created by alsa on 2019/10/31
 */
public class PermissionManager {

    /**
     * 向用户申请权限
     *
     * @param activity    当前activity
     * @param requestCode 请求标识码（必须<256）
     * @param permissions 需要授权的一组权限
     */
    public static void requestPermissions(@NonNull Activity activity, int requestCode,
                                          @NonNull String... permissions) {
        // 发起权限请求前检查权限状态
        if (hasPermissions(activity, permissions)) {
            // 全部请求通过
            notifyHasPermissions(activity, requestCode, permissions);
            return;
        }

        // 请求权限
        PermissionHelper helper = PermissionHelper.newInstance(activity);
        helper.requestPermissions(requestCode, permissions);
    }

    /**
     * 打开权限设置提示框
     *
     * @param activity    activity
     * @param permissions 需要请求的权限
     */
    public static void openSettingDialog(Activity activity, List<String> permissions) {
        // 检查用户是否拒绝过某个权限，并点击了“不在询问”
        if (PermissionManager.somePermissionPermanentlyDenied(activity, permissions)) {
            // 如满足条件，则显示对话框引导用户开启设置中的权限
            new AppSettingDialog.Builder(activity)
                    .setListener((dialog, which) -> Log.e("Permission", "onPermissionDenied >>> hasDeniedForever"))
                    .build()
                    .show();
        }
    }

    /**
     * 检测请求的权限是否被授予
     *
     * @param activity    context
     * @param permissions 权限组
     * @return 全部授权：true|任一权限未授权：false
     */
    private static boolean hasPermissions(Activity activity, @NonNull String... permissions) {
        if (activity == null) {
            throw new IllegalArgumentException("不能传入一个空的activity");
        }

        // 6.0以下版本无须做运行时权限判断
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // 6.0及以上版本需要逐一检测每个权限是否都被授权
        for (String permission : permissions) {
            // 如果循环中任一权限没有授权则返回false
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 如果全部已被授权则进入onRequestPermissionResult方法返回结果
     *
     * @param activity    当前activity
     * @param requestCode 请求标识码
     * @param permissions 授权通过的权限
     */
    private static void notifyHasPermissions(Activity activity, int requestCode, String[] permissions) {
        // 二次检查，将授权通过的权限组转参告知处理权限结果方法
        int[] grantResults = new int[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }
        onRequestPermissionResult(requestCode, permissions, grantResults, activity);
    }

    /**
     * 处理权限请求结果方法
     * 如果授予或者拒绝任何权限，将通过PermissionCallback回调接受结果
     * 以及运行有@Permission注解的方法
     *
     * @param requestCode  回调请求标识码
     * @param permissions  回调权限组
     * @param grantResults 回调授权结果
     * @param activity     拥有实现PermissionCallback接口或者有@Permission注解的Activity
     */
    public static void onRequestPermissionResult(int requestCode, String[] permissions,
                                                 int[] grantResults, Activity activity) {
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            // 遍历权限请求结果，分类加入集合
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(permission);
            } else {
                denied.add(permission);
            }
        }

        // 回调授权通过结果
        if (!granted.isEmpty()) {
            if (activity instanceof PermissionCallback) {
                ((PermissionCallback) activity).onPermissionGranted(requestCode, granted);
            }
        }

        // 回调授权拒绝结果
        if (!denied.isEmpty()) {
            if (activity instanceof PermissionCallback) {
                ((PermissionCallback) activity).onPermissionDenied(requestCode, denied);
            }
        }

        // 如果授权全部都通过，才执行注解方法，任一个权限被拒绝都不执行该方法
        if (!granted.isEmpty() && denied.isEmpty()) {
            reflectAnnotationMethod(activity, requestCode);
        }
    }

    /**
     * 找到指定activity中，有IPermission注解和请求标识码参数的正确方法
     *
     * @param activity    指定activity
     * @param requestCode 请求标识码
     */
    private static void reflectAnnotationMethod(Activity activity, int requestCode) {
        // 获取类
        Class<? extends Activity> clazz = activity.getClass();
        // 获取类中的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        // 遍历所有方法
        for (Method method : methods) {
            // 如果方法中有IPermission注解
            if (method.isAnnotationPresent(IPermission.class)) {
                // 获取注解
                IPermission iPermission = method.getAnnotation(IPermission.class);
                // 如果注解的值等于请求标识码（第两次匹配，避免框架冲突）
                if (iPermission.value() == requestCode) {
                    // 严格控制方法格式和规范
                    // 方法必须是返回void（第三次匹配）
                    Type returnType = method.getGenericReturnType();
                    if (!"void".equals(returnType.toString())) {
                        throw new RuntimeException(method.getName() + "方法返回类型必须是void");
                    }
                    // 方法必须是无参的（第四次匹配）
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length > 0) {
                        throw new RuntimeException(method.getName() + "方法必须是无参数的");
                    }

                    // 如果自定义方法是有私有修饰符，则设置可以访问
                    try {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(activity);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 检查被拒绝的权限组中，是否有点击了“不再询问”选项
     *
     * @param activity          当前activity
     * @param deniedPermissions 被拒绝的权限组
     * @return 如果有任一权限勾选了“不再询问”：true|如果所有权限都没有勾选“不再询问”：false
     */
    private static boolean somePermissionPermanentlyDenied(Activity activity, List<String> deniedPermissions) {
        return PermissionHelper.newInstance(activity).somePermissionPermanentlyDenied(deniedPermissions);
    }
}
