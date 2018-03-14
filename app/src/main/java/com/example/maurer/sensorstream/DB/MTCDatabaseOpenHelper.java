package com.example.maurer.sensorstream.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maurer on 20.01.2018.
 */

public class MTCDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MTC_DB";
    private static final int DATABASE_VERSION = 2;

    public MTCDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Test"+
                "val TEXT");
        db.execSQL("CREATE TABLE Temperature "+
                "Time TEXT,"+
                "val INT");
        db.execSQL("CREATE TABLE Accelerometer"+
                "Time TEXT,"+
                "valueX REAL,"+
                "valueY REAL,"+
                "valueZ REAL");
        db.execSQL("CREATE TABLE Gyroscope"+
                "Time TEXT,"+
                "valueX REAL,"+
                "valueY REAL,"+
                "valueZ REAL");
        db.execSQL("CREATE TABLE Baro_Hoehe"+
                "Time TEXT,"+
                "value REAL");
        db.execSQL("CREATE TABLE Baro_Druck"+
                "Time TEXT,"+
                "value REAL");
        db.execSQL("CREATE TABLE Kontaktdaten"+
                "Vorname TEXT,"+
                "Nachname TEXT,"+
                "TelefonNr TEXT,"+
                "EMail TEXT,"+
                "Senden_als INTEGER,");
        //db.execSQL("INSERT INTO Test VALUES (1,'hallo','Welt'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Bei Update der App; alte DB noch vorhanden, diese auf neue Version upgraden
    }
}