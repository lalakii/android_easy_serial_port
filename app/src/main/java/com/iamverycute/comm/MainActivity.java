package com.iamverycute.comm;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.hash.HashCode;
import com.iamverylovely.ASerialPort.ASerialPort;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        OpenSerial();
    }

    public void OpenSerial() {
        ASerialPort serialPort = new ASerialPort("/dev/ttyS3", 0010002, new ASerialPort.IComDataListener() {
            @Override
            public void OnReceive(byte[] data) {
                String hexStr = HashCode.fromBytes(data).toString();
                Log.d("serial_output", hexStr);
            }

            @Override
            public void OnError(int code) {
                if (code == 0) {
                }
            }
        });


        String hexStr = "EE 00 00 00 00 FF FF".replaceAll("\\s*", "").toLowerCase();
        byte[] data = HashCode.fromString(hexStr).asBytes();
        serialPort.send(data);


        serialPort.close();
    }
}