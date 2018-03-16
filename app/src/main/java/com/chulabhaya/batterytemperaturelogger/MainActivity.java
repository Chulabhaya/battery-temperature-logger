package com.chulabhaya.batterytemperaturelogger;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Obtains necessary permissions. */
        String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        String[] permissionNames = {"READ_PHONE_STATE", "WRITE_EXTERNAL_STORAGE", "READ_EXTERNAL_STORAGE"};
        int permissionsCode = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!permissionsGranted(permissionNames)){
                ActivityCompat.requestPermissions(this, permissions, permissionsCode);
            }
        }

        /* Obtains further permissions needed for NetworkStatsManager. */
        if (!usageAccessGranted()){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        /* Implements functionality for the data logging buttons. */
        OnClickListener listenerLoggerButtons = new OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, TemperatureLoggerService.class);
                switch (view.getId()){
                    case R.id.startLogger:
                        // Start service
                        startService(intent);
                        break;
                    case R.id.stopLogger:
                        stopService(intent);
                        break;
                }
            }
        };
        findViewById(R.id.startLogger).setOnClickListener(listenerLoggerButtons);
        findViewById(R.id.stopLogger).setOnClickListener(listenerLoggerButtons);

        /* Implements functionality for the email data button. */
        OnClickListener listenerEmail = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"chulabhayawijesundara@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Battery Temperature Logger Data");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "This email contains the battery temperature database attached.");
                File root = Environment.getExternalStorageDirectory();
                String pathToAttachedFile = TemperatureDBHelper.DATABASE_NAME;
                File file = new File(root, pathToAttachedFile);
                if (!file.exists()){
                    return;
                }
                Uri uri = Uri.fromFile(file);
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(emailIntent, "Pick an email provider."));
            }
        };
        findViewById(R.id.emailDB).setOnClickListener(listenerEmail);
    }

    /* Check to see if usage access permission has been granted by user. */
    private boolean usageAccessGranted(){
        try{
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                assert appOpsManager != null;
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    /* Check to see if other permissions have been granted. */
    private boolean permissionsGranted(String[] permissionNames){
        boolean permissionsGranted = true;
        for (String permission: permissionNames){
            permissionsGranted = permissionsGranted && (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED);
        }
        return permissionsGranted;
    }
}
