# Android Easy Serial Port

## Quick Start

1. Import aar
> import aar 

2. Code Sample
```java
ASerialPort serialPort = new ASerialPort("/dev/ttyS0", 0010002, new IComDataListener() {
    @Override
    public void OnReceive(byte[] data) {
        String hexStr = HashCode.fromBytes(data).toString();
        Log.d("serial_output", hexStr);
    }

    @Override
    public void OnError(int code) {
    
    }
});
```
