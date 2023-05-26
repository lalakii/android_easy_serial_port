package com.iamverylovely;

import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ASerialPort implements Runnable {
    private FileInputStream inputStream;
    private FileOutputStream outputStream;
    private ParcelFileDescriptor fromFd;
    private final IDataCallback _callback;

    public ASerialPort(String path, int speed, IDataCallback _callback) {
        System.loadLibrary("comm1");
        this._callback = _callback;
        try (ParcelFileDescriptor pFd = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_WRITE)) {
            fromFd = ParcelFileDescriptor.fromFd(read(pFd.detachFd(), speed));
            SetError(0);
        } catch (IOException ignored) {
            SetError(1);
        }
        if (fromFd != null) {
            FileDescriptor fd = fromFd.getFileDescriptor();
            outputStream = new FileOutputStream(fd);
            inputStream = new FileInputStream(fd);
            new Thread(this).start();
        }
    }

    public void send(byte[] data) {
        if (outputStream != null) {
            try {
                outputStream.write(data);
            } catch (IOException ignored) {
                SetError(2);
            }
        }
    }

    @Override
    public void run() {
        while (inputStream != null) {
            int available;
            try {
                available = inputStream.available();
                if (available > 0) {
                    byte[] data = new byte[available];
                    int length = inputStream.read(data);
                    if (_callback != null)
                        _callback.OnReceive(data, length);
                }
            } catch (IOException ignored) {
                SetError(3);
            }
        }
    }

    public void close() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (fromFd != null) fromFd.close();
        } catch (IOException ignored) {
            SetError(4);
        }
    }

    private void SetError(int code) {
        if (_callback != null) _callback.OnError(code);
    }

    private native int read(int fd, int speed);

    public interface IDataCallback {
        void OnReceive(byte[] data, int len);

        void OnError(int code);
    }
}