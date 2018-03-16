package com.chulabhaya.batterytemperaturelogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TemperatureDBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "BatteryTemperatures.db";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + TemperatureContract.TemperatureEntry.TABLE_NAME + " (" +
        TemperatureContract.TemperatureEntry._ID + " INTEGER PRIMARY KEY," +
        TemperatureContract.TemperatureEntry.COLUMN_TIME + " TEXT," +
        TemperatureContract.TemperatureEntry.COLUMN_TEMPERATURE + " REAL," +
        TemperatureContract.TemperatureEntry.COLUMN_LEVEL + " REAL," +
        TemperatureContract.TemperatureEntry.COLUMN_VOLTAGE + " REAL," +
        TemperatureContract.TemperatureEntry.COLUMN_CURRENT + " REAL," +
        TemperatureContract.TemperatureEntry.COLUMN_MEMORY + " REAL," +
        TemperatureContract.TemperatureEntry.COLUMN_CPU + " REAL," +
        TemperatureContract.TemperatureEntry.COLUMN_WIFI_USAGE + " REAL," +
        TemperatureContract.TemperatureEntry.COLUMN_DATA_USAGE + " REAL)";

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + TemperatureContract.TemperatureEntry.TABLE_NAME;

    TemperatureDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.d("Database operations", "Database created!");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // For now the upgrade process just deletes the old database and creates
        // a new one
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        Log.d("Database operations", "Database upgraded!");
    }

    long insertEntry(String time, double temperature, double level, double voltage, double current, double memory, double cpu_load, long wifi_usage, long data_usage){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TemperatureContract.TemperatureEntry.COLUMN_TIME, time);
        values.put(TemperatureContract.TemperatureEntry.COLUMN_TEMPERATURE, temperature);
        values.put(TemperatureContract.TemperatureEntry.COLUMN_LEVEL, level);
        values.put(TemperatureContract.TemperatureEntry.COLUMN_VOLTAGE, voltage);
        values.put(TemperatureContract.TemperatureEntry.COLUMN_CURRENT, current);
        values.put(TemperatureContract.TemperatureEntry.COLUMN_MEMORY, memory);
        values.put(TemperatureContract.TemperatureEntry.COLUMN_CPU, cpu_load);
        values.put(TemperatureContract.TemperatureEntry.COLUMN_WIFI_USAGE, wifi_usage);
        values.put(TemperatureContract.TemperatureEntry.COLUMN_DATA_USAGE, data_usage);
        long newRowId = database.insert(TemperatureContract.TemperatureEntry.TABLE_NAME, null, values);

        Log.d("Database operations", "Row inserted!");
        return newRowId;
    }

    public Cursor getEntry(long id){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor res = database.rawQuery( " SELECT * FROM " + TemperatureContract.TemperatureEntry.TABLE_NAME + " WHERE " +
            TemperatureContract.TemperatureEntry._ID + "=?", new String[]{Long.toString(id)});
        Log.d("Database operations", "Row returned!");
        return res;
    }

    void clearDB(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TemperatureContract.TemperatureEntry.TABLE_NAME, null, null);
        database.close();
    }

    void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "com.chulabhaya.batterytemperaturelogger" + "/databases/" + DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, DATABASE_NAME);
        try{
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(IOException e){
            e.printStackTrace();
        }

    }
}
