package com.example.maurer.sensorstream.Scanner;

import android.bluetooth.BluetoothClass;

/**
 * Created by Maurer on 26.01.2018.
 */

public class Device_Information {
    String name;
    String address;
    int type;
    BluetoothClass clas;

    public Device_Information(String address, BluetoothClass clas, String name, int type) {
        this.address = address;
        this.clas = clas;
        this.name = name;
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BluetoothClass getClas() {
        return clas;
    }

    public void setClas(BluetoothClass clas) {
        this.clas = clas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        if  (type == 1)
            return " 1\n Power: 100 mW (20dBm)\n Reichweite: 100 Meter";
        else if (type == 2)
            return " 2\n Power: 2,5 mW (4dBm) \n Reichweite: 10 Meter";
        else
            return " 3\n Power: 1  (0dBm) \n Reichweite: 1 Meter";
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ""+address;
    }
}