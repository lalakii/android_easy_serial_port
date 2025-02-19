#include <jni.h>

#ifndef _Included_cn_lalaki_SerialPort
#define _Included_cn_lalaki_SerialPort
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_cn_lalaki_SerialPort_open(JNIEnv *, jobject, int, int);

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_write(JNIEnv *, jobject, int, jbyteArray);

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_writeByte(JNIEnv *, jobject, int, jbyte );

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_close(JNIEnv *, jobject, int);

#ifdef __cplusplus
}
#endif
#endif