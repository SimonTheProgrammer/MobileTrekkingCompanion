package com.example.maurer.sensorstream.DB;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.maurer.sensorstream.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maurer on 12.02.2018.
 */

public class DB_Anzeige extends AppCompatActivity {

    List<String> listItems=new ArrayList<>();
    Activity a = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_activity);

        this.setTitle("DatabaseView");
        //ListAdapter => DB-Einträge einfügen
        //Test: Kontaktdaten
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(this);
        SQLiteDatabase read = db.getReadableDatabase();
        Cursor c = read.query("Kontaktdaten",new String[] {"Vorname","Nachname","TelefonNr","EMail"},null,null,null,null,"Vorname", "4");
        while (c.moveToNext()){
            try {
                Log.i("SQLITE", "vn: "+c.getString(0)+", nn: "+c.getString(1));
                listItems.add(c.getString(0)+", "+c.getString(1)+", "+c.getString(2)+", "+c.getString(3));
            }catch (RuntimeException e){
                Log.e("SQLITE","failed");
            }
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<>(a,
                android.R.layout.simple_list_item_1,
                listItems);
        adapter.notifyDataSetChanged();

        try {
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
        findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItems();
            }
        });
    }

    public void addItems() {
        Log.i("Database","klick");
        //DB auslesen
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(this);
        //delete first row:
        db.getWritableDatabase().delete("Kontaktdaten","Senden_Sms=?",new String[]{String.valueOf(1)});
        db.getWritableDatabase().delete("Kontaktdaten","Senden_Sms=?",new String[]{String.valueOf(0)});//*/
        SQLiteDatabase read = db.getReadableDatabase();
        Cursor c = read.query("Kontaktdaten",new String[] {"Vorname","Nachname"},null,null,null,null,"Vorname", "14");
        c.moveToFirst();
        while (c.moveToNext()){
            try {
                Log.i("SQLITE", "vn: "+c.getString(0)+", nn: "+c.getString(1));
            }catch (RuntimeException e){
                Log.e("SQLITE","failed");
            }
        }
        db.close();
        Log.i("Database","quit");
    }
}