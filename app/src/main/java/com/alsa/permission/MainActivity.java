package com.alsa.permission;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alsa.permission.listener.PermissionCallback;
import com.alsa.permission.manager.PermissionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionCallback {
    /**
     * 相机、联系人权限
     */
    private static final String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS
    };

    /**
     * 权限请求码
     */
    private static final int REQUEST_CAMERA_CONTACT_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(view -> PermissionManager.requestPermissions(this, REQUEST_CAMERA_CONTACT_CODE, permissions));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionResult(REQUEST_CAMERA_CONTACT_CODE, permissions, grantResults, this);
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> permissions) {
        for (int i = 0; i < permissions.size(); i++) {
            Toast.makeText(this, permissions.get(i) + "权限授权通过！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> permissions) {
        PermissionManager.openSettingDialog(this, permissions);
    }
}
