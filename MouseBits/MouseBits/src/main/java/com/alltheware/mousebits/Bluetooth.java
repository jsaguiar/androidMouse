package com.alltheware.mousebits;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;

import java.util.Set;

/**
 * Created by joao on 4/10/14.
 */
public class Bluetooth {
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;

    public Bluetooth(BluetoothAdapter BA, Set<BluetoothDevice> pairedDevices) {
        this.BA = BluetoothAdapter.getDefaultAdapter();;
        this.pairedDevices = pairedDevices;
    }


    public void on(View view){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(turnOn, 0);
            //Toast.makeText(MouseActivity.this, "Turned on", Toast.LENGTH_LONG).show();
        }
        else{
           // Toast.makeText(getApplicationContext(),"Already on",
            //Toast.LENGTH_LONG).show();
        }
    }
}
