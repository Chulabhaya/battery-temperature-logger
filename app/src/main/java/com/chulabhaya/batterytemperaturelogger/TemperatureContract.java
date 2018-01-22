package com.chulabhaya.batterytemperaturelogger;

import android.provider.BaseColumns;

final class TemperatureContract {
    // Prevent someone from accidentally instantiating the contract class; make
    // constructor private.
    private TemperatureContract(){
    }

    /* Inner class that defines the table contents */
    static class TemperatureEntry implements BaseColumns{
        static final String TABLE_NAME = "temperature_database";
        static final String COLUMN_TIME = "time";
        static final String COLUMN_TEMPERATURE = "battery_temperature";
        static final String COLUMN_LEVEL = "battery_level";
        static final String COLUMN_VOLTAGE = "battery_voltage";
        static final String COLUMN_CPU = "cpu_load";
    }
}
