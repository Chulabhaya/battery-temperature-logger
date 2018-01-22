package com.chulabhaya.batterytemperaturelogger;

import android.provider.BaseColumns;

/**
 * Created by ckwij on 10/5/2017.
 */

public final class TemperatureContract {
    // Prevent someone from accidentally instantiating the contract class; make
    // constructor private.
    private TemperatureContract(){
    }

    /* Inner class that defines the table contents */
    public static class TemperatureEntry implements BaseColumns{
        public static final String TABLE_NAME = "temperature_database";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TEMPERATURE = "battery_temperature";
        public static final String COLUMN_LEVEL = "battery_level";
        public static final String COLUMN_VOLTAGE = "battery_voltage";
        public static final String COLUMN_CPU = "cpu_load";
    }
}
