package com.iamverylovely.comm;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.hash.HashCode;
import com.iamverylovely.ASerialPort;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView tvOutput;
    private TextView edInput;
    private ASerialPort serialPort;
    private final ArrayList<String> listPorts = new ArrayList<>();
    private Button btnOpen;
    private Button btnClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tvOutput = findViewById(R.id.output);
        edInput = findViewById(R.id.input);
        btnOpen = findViewById(R.id.open);
        btnClose = findViewById(R.id.close);
        Button btnSend = findViewById(R.id.send);
        btnOpen.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        Spinner ports = findViewById(R.id.serialPorts);
        File f = new File("/dev/");
        File[] listFiles = f.listFiles();
        if (listFiles != null) {
            Arrays.stream(listFiles).filter(item -> item.getName().contains("ttyS")).forEach(
                    item -> listPorts.add(item.getAbsolutePath())
            );
        }
        ports.setAdapter(new ArrayAdapter<>(this, R.layout.item, listPorts));
        ports.setOnItemSelectedListener(this);
    }

    public void OpenSerial() {
        if (currentPort == null) {
            if (listPorts.size() > 0) {
                currentPort = listPorts.get(0);
            }
        }
        if (currentPort == null) {
            Toast.makeText(this, "Can not find serial port.", Toast.LENGTH_SHORT).show();
            return;
        }
        //baud_rate: 115200
        serialPort = new ASerialPort(currentPort, 0010002, new ASerialPort.IDataCallback() {
            @Override
            public void OnReceive(byte[] bytes, int len) {
                //Hex decode
                String hexStr = HashCode.fromBytes(bytes).toString();
                runOnUiThread(() -> tvOutput.append(hexStr + "\n"));
            }

            @Override
            public void OnError(int code) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.open) {
            OpenSerial();
            v.setVisibility(View.GONE);
            btnClose.setVisibility(View.VISIBLE);

        } else if (id == R.id.send) {
            if (serialPort == null) {
                Toast.makeText(this, "Serial port not open", Toast.LENGTH_SHORT).show();
                return;
            }
            if (edInput.getText() == null || edInput.getText().toString().trim().equals("")) {
                return;
            }
            String trimHex = edInput.getText().toString().replaceAll("\\s*", "").toLowerCase();
            //Hex encode
            try {
                byte[] hexData = HashCode.fromString(trimHex).asBytes();
                // Send Hex Data:
                serialPort.send(hexData);
                runOnUiThread(() -> edInput.setText(""));
            } catch (IllegalArgumentException ignored) {
                Toast.makeText(this, "Must hex string!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.close) {
            serialPort.close();
            serialPort = null;
            v.setVisibility(View.GONE);
            btnOpen.setVisibility(View.VISIBLE);
        }
    }

    private String currentPort;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentPort = listPorts.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}