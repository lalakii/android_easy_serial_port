# Android Easy Serial Port
[![Maven Central](https://img.shields.io/maven-central/v/cn.lalaki/SerialPort.Android.svg?label=Maven%20Central&logo=sonatype)](https://central.sonatype.com/artifact/cn.lalaki/SerialPort.Android/) ![API: 21+ (shields.io)](https://img.shields.io/badge/API-21+-2f9b45?logo=android) ![License: Apache-2.0 (shields.io)](https://img.shields.io/badge/License-Apache--2.0-c02041?logo=apache)

Android serial port read/write library.

## Quick Start

1. Import AAR

    Gradle or Download [SerialPort](https://github.com/lalakii/android_easy_serial_port/releases)

    ```kotlin
   //kotlin
    dependencies {
        implementation("cn.lalaki:SerialPort.Android:1.4")
    }
    ```
    ```groovy
   //groovy
    dependencies {
        implementation 'cn.lalaki:SerialPort.Android:1.4'
    }
    ```

2. Code Sample

   ```kotlin
   //Kotlin
   val b115200 = "0010002".toInt(8)
   val serialPort = SerialPort("/dev/ttySX", b115200, object : SerialPort.DataCallback {
       override fun onData(data: ByteArray) {
           //...
       }
   })
   
   serialPort.write(byte[])
   
   serialPort.close()
   ```
    
   ```java 
   //Java
   import cn.lalaki.SerialPort;
   
   SerialPort serialPort1 = new SerialPort("/dev/ttySX",0010002,null);  // write only
   serialPort1.write(byte[]);
   
   SerialPort serialPort2 = new SerialPort("/dev/ttySX", 0010002, new SerialPort.DataCallback() {
       @Override
       public void onData(@Nullable byte[] data) {
           //...
       }
   });
   serialPort2.write(byte[]);
   serialPort2.close();
   ```

3. Params

    ```java
    new SerialPort(path, speed, callback);
    ```
   
   | param           | value  |   type    | example |
   |:----------------| ----  |:---------:| ---- |
   | path            | Serial port path |  String   | "dev/ttyS0" |
   | speed is baud rate | See [termbits.h](https://github.com/torvalds/linux/blob/master/include/uapi/asm-generic/termbits.h) |  int  | B9600 value is 0000015, B115200 value is 0010002 |
   | callback        | DataCallback | interface | ... |


4. Common problems

    ```bash
    ### Checking Permissions
    adb shell
    su
    setenforce 0
    chmod 0766 /dev/ttyS*
    ```
## Demo
![demo.gif](https://cdn.jsdelivr.net/gh/lalakii/android_easy_serial_port/video/demo.gif)

## About

Generating electricity for love.

+ feedback：dazen@189.cn
