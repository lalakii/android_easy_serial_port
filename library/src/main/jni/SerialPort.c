#include "cn_lalaki_SerialPort.h"
#include <termios.h>
#include <pthread.h>
#include <unistd.h>

#define MAX_LEN 32
struct {
    pthread_t tid;
    jobject clazz;
} threads[MAX_LEN];
JavaVM *vm;
jfieldID mFD;
jmethodID onNativeData;

void *comm_read() {
    JNIEnv *env;
    (*vm)->AttachCurrentThread(vm, &env, NULL);
    if (mFD == NULL || onNativeData == NULL) {
        jclass obj = (*env)->GetObjectClass(env, threads[0].clazz);
        mFD = (*env)->GetFieldID(env, obj, "mFD", "I");
        onNativeData = (*env)->GetMethodID(env, obj, "onNativeData", "([B)V");
    }
    jclass clazz = NULL;
    for (int i = 0; i < MAX_LEN; i++) {
        if (pthread_equal(pthread_self(), threads[i].tid)) {
            clazz = threads[i].clazz;
            break;
        }
    }
    if (clazz != NULL) {
        int fd;
        while ((fd = (*env)->GetIntField(env, clazz, mFD)) != -1) {
            int size = 1024;
            jbyte buffer[size];
            int len = read(fd, buffer, size);
            if (len > 0) {
                jbyteArray bytes = (*env)->NewByteArray(env, len);
                (*env)->SetByteArrayRegion(env, bytes, 0, len, buffer);
                (*env)->CallVoidMethod(env, clazz, onNativeData, bytes);
            }
            usleep(10000);
        }
        for (int i = 0; i < MAX_LEN; i++) {
            if (threads[i].clazz == clazz) {
                (*env)->DeleteGlobalRef(env, clazz);
                threads[i].clazz = NULL;
                break;
            }
        }
    }
    if (vm != NULL) {
        (*vm)->DetachCurrentThread(vm);
        int isClean = 1;
        for (int i = 0; i < MAX_LEN; i++) {
            if (threads[i].clazz != NULL)isClean = 0;
        }
        if (isClean) {
            onNativeData = NULL;
            mFD = NULL;
            vm = NULL;
        }
    }
    return 0;
}

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_open(__unused JNIEnv *env, __unused jclass clazz, int fd, int speed) {
    tcflush(fd, TCIOFLUSH);
    struct termios cfg;
    tcgetattr(fd, &cfg);
    cfmakeraw(&cfg);
    cfsetospeed(&cfg, speed);
    tcsetattr(fd, TCSANOW, &cfg);
    if (vm == NULL) {
        (*env)->GetJavaVM(env, &vm);
    }
    for (int i = 0; i < MAX_LEN; i++) {
        if (threads[i].clazz == NULL) {
            threads[i].clazz = (*env)->NewGlobalRef(env, clazz);
            pthread_create(&threads[i].tid, NULL, comm_read, NULL);
            break;
        }
    }
}

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_write(__unused JNIEnv *env, __unused jclass clazz, int fd,
                                jbyteArray bytes) {
    jbyte *data = (*env)->GetByteArrayElements(env, bytes, NULL);
    if (data != NULL) {
        write(fd, data, (*env)->GetArrayLength(env, bytes));
        (*env)->ReleaseByteArrayElements(env, bytes, data, JNI_ABORT);
    }
}

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_close(__unused JNIEnv *env, __unused jclass clazz, int fd) {
    close(fd);
}