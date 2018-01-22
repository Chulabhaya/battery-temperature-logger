package com.chulabhaya.batterytemperaturelogger;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ckwij on 10/5/2017.
 */

public class TemperatureLoggerService extends Service{
    private TemperatureDBHelper temperatureDatabase;
    private Context context;
    private static final String TAG = "TempLoggerService";
    private boolean isRunning = false;
    private Looper looper;
    private TemperatureLoggerServiceHandler TemperatureLoggerServiceHandler;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

    @Override
    public void onCreate(){
        HandlerThread handlerThread = new HandlerThread("TemperatureLoggerThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        looper = handlerThread.getLooper();
        TemperatureLoggerServiceHandler = new TemperatureLoggerServiceHandler(looper);
        isRunning = true;
        context = getApplicationContext();
        this.temperatureDatabase = new TemperatureDBHelper(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Message message = TemperatureLoggerServiceHandler.obtainMessage();
        message.arg1 = startId;
        TemperatureLoggerServiceHandler.sendMessage(message);
        Toast.makeText(this, "Temperature logging started.", Toast.LENGTH_SHORT).show();

        // If service is killed while starting, it restarts
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onDestroy(){
        isRunning = false;
        Toast.makeText(this, "Temperature logging stopped.", Toast.LENGTH_SHORT).show();
        temperatureDatabase.exportDB(context);
        temperatureDatabase.clearDB();
    }

    // Obtains battery temperature
    private double getBatteryTemperature(){
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        double celsius = ((double)intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)) / 10;
        double fahrenheit = Double.valueOf(decimalFormat.format(((celsius * 9) / 5) + 32));
        return fahrenheit;
    }

    private double getBatteryLevel(){
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        double level = (double)intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
        return level;
    }

    private double getBatteryVoltage(){
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        double voltage = (double)intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
        return voltage;
    }

    private float getCPULoad() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(500);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            float raw_load = (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
            return raw_load*100;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    private final class TemperatureLoggerServiceHandler extends Handler{
        public TemperatureLoggerServiceHandler(Looper looper){
            super(looper);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message message){
            synchronized (this){
                while(isRunning){
                    try {
                        Date currentTime = Calendar.getInstance().getTime();
                        String currentTimeString = dateFormat.format(currentTime);
                        double battery_temp = getBatteryTemperature();
                        double battery_level = getBatteryLevel();
                        double battery_voltage = getBatteryVoltage();
                        float cpu_load = getCPULoad();
                        temperatureDatabase.insertEntry(currentTimeString, battery_temp, battery_level, battery_voltage, cpu_load);
                        Log.i(TAG, "TemperatureLoggerService running! " +currentTimeString+ " " +battery_temp+ " " +battery_level+ " " +battery_voltage+ " " +cpu_load);
                        Thread.sleep(475);
                    }
                    catch(Exception e){
                        Log.i(TAG, e.getMessage());
                    }
                }
            }
            // Stops the service for the start Id
            stopSelfResult(message.arg1);
        }
    }
}
