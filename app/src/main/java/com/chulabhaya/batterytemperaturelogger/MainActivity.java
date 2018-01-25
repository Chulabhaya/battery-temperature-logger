package com.chulabhaya.batterytemperaturelogger;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Obtains necessary permissions. */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

        /* Implements functionality for regex threads button, currently a WIP. */
        OnClickListener listenerRegexThread = new OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Run 10 threads for 120 seconds */
                int NUM_THREADS = 10, RUNNING_TIME = 120;
                for(int i = 0; i < 2; ++i){
                    new RegexThread();
                }
                try {
                    Thread.sleep(1000*RUNNING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        findViewById(R.id.startRegex).setOnClickListener(listenerRegexThread);
    }
}
