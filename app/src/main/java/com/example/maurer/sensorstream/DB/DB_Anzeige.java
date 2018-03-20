package com.example.maurer.sensorstream.DB;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.maurer.sensorstream.R;

import java.util.ArrayList;

/**
 * Created by Maurer on 12.02.2018.
 */

public class DB_Anzeige extends AppCompatActivity {

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    int counter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_activity);

        /*//löschen (Tbl-Einträge)
        SQLiteDatabase write = db.getWritableDatabase();
        write.delete("Accelerometer",null,null);//*/

        this.setTitle("DatabaseView");
        //ListAdapter => DB-Einträge einfügen
        //Test 1: Kontaktdaten

        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        ListView l = (ListView) findViewById(R.id.list);
        findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItems();
            }
        });
        //l.setAdapter(adapter);
    }

    public void addItems() {
        //DB auslesen
        MTCDatabaseOpenHelper db = new MTCDatabaseOpenHelper(this);
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
    }
}