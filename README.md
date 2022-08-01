# Android Easy Serial Port
 
Android serial port to send and receive data, easy to use.

Prerequisites
+ SDK Version >= 25

## Quick Start

1. Import aar

> import aar 

2. Code Sample

```java
ASerialPort serialPort = new ASerialPort("/dev/ttyS0", 0010002, new IComDataListener() {
    @Override
    public void OnReceive(byte[] data) {
        //ByteArrayToHexStr using Guava https://github.com/google/guava
        String hexStr = HashCode.fromBytes(data).toString();
        Log.d("serial_output", hexStr);
    }

    @Override
    public void OnError(int code) {
        // Error Code
        if (code == 0) {
            // No Error;
        }
    }
});

//End 
serialPort.close();
```

3. Params:

```java
new ASerialPort(path, baudrate, listener);
```
| :---- param ----   | value  | example |
|  ----  | ----  | ---- |
| path  | serial port path; | "dev/ttyS0" |
| baudrate  | See [termbits.h](https://sources.debian.org/src/android-platform-development/8.1.0%2Br23-1/ndk/platforms/android-9/arch-x86/include/asm/termbits.h/) |  B9600 value is 0000015, B115200 value is 0010002 |
| listener | IComDataListener; | ... |

4. ERROR CODE: 

|  code   | status  |
|  :----:  | ----  |
| -1  | load library err; |
| 0  | no err; |
| 1 | open serial port err; |


## About

Generating electricity for love.

+ feedback：dazen@189.cn
