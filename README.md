# Android Easy Serial Port [Open Source](https://github.com/iamverycute/ASerialPort)

Easy to use serial port library for Android.

Prerequisites
+ SDK Version >= 25

## Quick Start

1. Import AAR

+ Gradle or Download [ASerialPort](https://github.com/iamverycute/android_easy_serial_port/releases)

```groovy
dependencies {
    implementation 'com.iamverylovely:ASerialPort.Android:0.4' //release
}
```

2. Code Sample

```java
import com.iamverylovely.ASerialPort;

// Open and receive Data:
ASerialPort serialPort = new ASerialPort("/dev/ttyS0", 0010002, new ASerialPort.IDataCallback() {
    @Override
    public void OnReceive(byte[] data, int len) {
        /**ByteArrayToHexStr
            using Guava https://github.com/google/guava
            */
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

/**HexStrToByteArray
    using Guava https://github.com/google/guava
    */
String trimHex = "EE 00 00 00 01 FF".replaceAll("\\s*", "").toLowerCase();
byte[] hexData = HashCode.fromString(trimHex).asBytes();

// Send Hex Data:
serialPort.send(hexData);

// End
serialPort.close();
```

3. Params:

```java
new ASerialPort(path, baudrate, callback);
```
|  param  | value  | type | example |
|  :----  | ----  | :----: | ---- |
| path  | serial port path; | String | "dev/ttyS0" |
| baudrate  | See [termbits.h](https://sources.debian.org/src/android-platform-development/8.1.0%2Br23-1/ndk/platforms/android-9/arch-x86/include/asm/termbits.h/) | int | B9600 value is 0000015, B115200 value is 0010002 |
| callback | IDataCallback; | callback | ... |

4. ERROR CODE: 

|  code   | status  |
|  :----:  | ----  |
| 0  | no err; |
| 1 | open serial port err; |

## Cautions
```diff
// if you want to set the baudrate to 115200

- new ASerialPort(..., 115200, ...);    × Wrong

+ new ASerialPort(..., 0010002, ...);    √ Right

// The parameter here is the integer value corresponding to the baudrate.
```
## About

Generating electricity for love.

+ feedback：dazen@189.cn


