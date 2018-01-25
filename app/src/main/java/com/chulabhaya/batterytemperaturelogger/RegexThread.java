package com.chulabhaya.batterytemperaturelogger;

import java.util.regex.Pattern;

public class RegexThread extends Thread{
    private Pattern p;
    RegexThread(){
        /* Create a new, second thread */
        super("Regex Thread");
        this.p = Pattern.compile("a*b");
        start();
    }

    /* Thread compiles regex string to do stress CPU. */
    public void run(){
        while(true){
            p = Pattern.compile("a*b");
        }
    }
}
