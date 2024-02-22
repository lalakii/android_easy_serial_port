package cn.lalaki.comm;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.common.hash.HashCode;

import java.io.File;

import cn.lalaki.SerialPort;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SerialPort serialPort;
    private TextView dataOutput;
    private TextView dataInput;
    private Button btnOpen;
    private Button btnClose;
    private Spinner ports;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.send).setOnClickListener(this);
        ports = findViewById(R.id.serialPorts);
        dataOutput = findViewById(R.id.output);
        dataInput = findViewById(R.id.input);
        btnOpen = findViewById(R.id.open);
        btnClose = findViewById(R.id.close);
        btnOpen.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item);
        File[] dev = new File("/dev/").listFiles(it -> it.getName().contains("ttyS"));
        if (dev != null) {
            for (File it : dev) {
                adapter.add(it.getAbsolutePath());
            }
            ports.setAdapter(adapter);
        }
    }
    @SuppressWarnings("OctalInteger")
    public void OpenSerialPort() {
        Object selectedPort = ports.getSelectedItem();
        if (selectedPort == null) return;
        String port = selectedPort.toString().trim();
        if (port.isEmpty()) {
            return;
        }
        //BaudRate: B115200, see BaudRate value: https://github.com/torvalds/linux/blob/master/include/uapi/asm-generic/termbits.h
        serialPort = new SerialPort(port, 0010002, new SerialPort.DataCallback() {
            @Override
            public void onData(@Nullable byte[] data) {
                if (data != null) {
                    //Hex decode
                    String hexStr = HashCode.fromBytes(data).toString();
                    runOnUiThread(() -> {
                        NestedScrollView sc = findViewById(R.id.scrollView);
                        sc.fullScroll(View.FOCUS_DOWN);
                        sc.post(() -> dataInput.requestFocus());
                        dataOutput.append(hexStr + "\n");
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.open) {
            OpenSerialPort();
            v.setVisibility(View.GONE);
            btnClose.setVisibility(View.VISIBLE);
        } else if (id == R.id.send) {
            if (dataInput.getText() == null || dataInput.getText().toString().trim().isEmpty()) {
                return;
            }
            if (serialPort == null) {
                Toast.makeText(this, "Serial port not open", Toast.LENGTH_SHORT).show();
                return;
            }
            String trimText = dataInput.getText().toString().replaceAll("\\s*", "").toLowerCase();
            //Hex encode
            try {
                byte[] hexEncode = HashCode.fromString(trimText).asBytes();
                // Send Hex data:
                serialPort.write(hexEncode);
                runOnUiThread(() -> dataOutput.append(trimText + "\n"));
            } catch (IllegalArgumentException ignored) {
                Toast.makeText(this, "Wrong hex string!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.close) {
            if (serialPort != null) {
                serialPort.close();
                serialPort = null;
            }
            v.setVisibility(View.GONE);
            btnOpen.setVisibility(View.VISIBLE);
        } else {
            dataOutput.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }
}