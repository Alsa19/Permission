package com.alsa.library.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * AppSettingDialog [ 自定义设置弹窗 ]
 * created by alsa on 2019/10/31
 */
public class AppSettingDialog implements DialogInterface.OnClickListener {

    /**
     * 跳转设置监听回调标识码
     */
    private static final int SETTING_CODE = 333;

    /**
     * 以下是Builder传递的内容
     */
    private Activity mActivity; // context
    private int mRequestCode;   // 请求标识码
    private String mTitle;  // 对话框标题
    private String mMessage;    // 提示内容
    private String mPositiveButton; // 确定按钮
    private String mNegativeButton; // 取消按钮
    private DialogInterface.OnClickListener mListener;  // 点击监听

    private AppSettingDialog(Builder builder) {
        this.mActivity = builder.activity;
        this.mTitle = builder.title;
        this.mMessage = builder.message;
        this.mPositiveButton = builder.positiveButton;
        this.mNegativeButton = builder.negativeButton;
        this.mListener = builder.listener;
        this.mRequestCode = builder.requestCode;
    }

    /**
     * 显示对话框|对外暴露的方法
     */
    public void show() {
        if (mListener != null) {
            showDialog();
        } else {
            throw new IllegalArgumentException("对话框监听不能为空");
        }
    }

    /**
     * 显示一个真正的对话框
     */
    private void showDialog() {
        new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton(mPositiveButton, this)
                .setNegativeButton(mNegativeButton, mListener)
                .create()
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // 点击跳转设置
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri mUri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(mUri);
        mActivity.startActivityForResult(intent, mRequestCode);
    }

    public static class Builder {
        private Activity activity;
        private String title;
        private String message;
        private String positiveButton;
        private String negativeButton;
        private DialogInterface.OnClickListener listener;
        private int requestCode = -1;

        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setListener(DialogInterface.OnClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public AppSettingDialog build() {
            this.title = "需要的授权";
            this.message = TextUtils.isEmpty(message) ? "打开设置，启动权限" : message;
            this.positiveButton = activity.getString(android.R.string.ok);
            this.negativeButton = activity.getString(android.R.string.cancel);
            this.requestCode = requestCode > 0 ? requestCode : SETTING_CODE;

            return new AppSettingDialog(this);
        }
    }
}
