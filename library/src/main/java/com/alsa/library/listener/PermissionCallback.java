package com.alsa.library.listener;

import java.util.List;

/**
 * PermissionCallback [ 自定义权限回调接口 ]
 * created by alsa on 2019/10/31
 */
public interface PermissionCallback {
    /**
     * 授权通过回调
     *
     * @param requestCode 请求标识码
     * @param permissions 请求的权限组
     */
    void onPermissionGranted(int requestCode, List<String> permissions);

    /**
     * 授权拒绝回调
     *
     * @param requestCode 请求标识码
     * @param permissions 请求的权限组
     */
    void onPermissionDenied(int requestCode, List<String> permissions);
}
