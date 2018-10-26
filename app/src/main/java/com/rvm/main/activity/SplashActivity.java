package com.rvm.main.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.rvm.main.MainActivity;
import com.rvm.main.R;
import com.rvm.main.util.PermissionUtils;

import java.io.File;

public class SplashActivity extends AppCompatActivity {
    private final int EXTERNAL_STORAGE_PERMISSIONS = 12;
    private static final int PICK_MEDIA_REQUEST = 44;
    public final static String ACTION_OPEN_ALBUM = "com.rvm.main.OPEN_ALBUM";
    private boolean pickMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String action = getIntent().getAction();
        if (action != null) {
            pickMode = action.equals(Intent.ACTION_GET_CONTENT) || action.equals(Intent.ACTION_PICK);
        }
        if (PermissionUtils.isStoragePermissionsGranted(this)) {


            if (action != null && action.equals(ACTION_OPEN_ALBUM)) {
                Bundle data = getIntent().getExtras();
                if (data != null) {
                    String ab = data.getString("albumPath");
                    if (ab != null) {
                        File dir = new File(ab);
                        //tmpAlbum = new Album(getApplicationContext(), dir.getAbsolutePath(), data.getInt("albumId", -1), dir.getName(), -1);
                        // TODO: 4/10/17 handle
                        start();
                    }

                } else   Toast.makeText(SplashActivity.this, getString(R.string.album_not), Toast.LENGTH_LONG).show();
            } else {  // default intent
                start();
            }


        } else
            PermissionUtils.requestPermissions(this, EXTERNAL_STORAGE_PERMISSIONS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    private void start() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        if (pickMode) {
            startActivityForResult(intent, PICK_MEDIA_REQUEST);
        } else {
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSIONS:
                boolean gotPermission = grantResults.length > 0;

                for (int result : grantResults) {
                    gotPermission &= result == PackageManager.PERMISSION_GRANTED;
                }

                if (gotPermission) {
                    start();
                } else {
                    Toast.makeText(SplashActivity.this, getString(R.string.storage_permission_denied), Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
