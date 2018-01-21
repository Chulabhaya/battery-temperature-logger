package com.chulabhaya.batterytemperaturelogger;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

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
}
