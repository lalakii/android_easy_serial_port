package com.iamverylovely.comm;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.hash.HashCode;
import com.iamverylovely.ASerialPort;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        OpenSerial();
    }

    public void OpenSerial() {
        ASerialPort serialPort = new ASerialPort("/dev/ttyS3", 0010002, new ASerialPort.IDataCallback() {
            @Override
            public void OnReceive(byte[] bytes, int len) {

                //Hex decode
                String hexStr = HashCode.fromBytes(bytes).toString();
                Log.d("serial_output", hexStr);
            }

            @Override
            public void OnError(int code) {

            }
        });

        //Hex encode
        String trimHex = "EE 00 00 00 01 FF".replaceAll("\\s*", "").toLowerCase();
        byte[] hexData = HashCode.fromString(trimHex).asBytes();

        // Send Hex Data:
        serialPort.send(hexData);

        byte[] rawData = "Hello".getBytes();

        // Send Raw Data:
        serialPort.send(rawData);

        serialPort.close();
    }
}