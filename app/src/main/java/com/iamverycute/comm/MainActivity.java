package com.iamverycute.comm;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.hash.HashCode;

import ASerialPort.ASerialPort;
import ASerialPort.IComDataListener;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        OpenSerial();
    }

    public void OpenSerial() {
        ASerialPort serialPort = new ASerialPort("/dev/ttyS3", 0010002, new IComDataListener() {
            @Override
            public void OnReceive(byte[] data) {
                String hexString = HashCode.fromBytes(data).toString().replaceAll("[0]+$", "");
                Log.d("serial_output", hexString);
            }

            @Override
            public void OnError(int code) {
                //
            }
        });
    }
}