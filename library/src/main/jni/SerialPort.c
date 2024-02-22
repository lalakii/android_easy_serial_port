#include <termios.h>
#include <pthread.h>
#include <unistd.h>
#include <android/log.h>
#include "cn_lalaki_SerialPort.h"

#define TAG "cn.lalaki.comm1"
#define LOGd(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
jobject obj;
JavaVM *vm;

void *read_comm(__unused void *_) {
    JNIEnv *env;
    (*vm)->AttachCurrentThread(vm, &env, NULL);
    jclass clazz = (*env)->GetObjectClass(env, obj);
    jmethodID onData = (*env)->GetMethodID(env, clazz, "onNativeData", "([B)V");
    jfieldID mFd = (*env)->GetFieldID(env, clazz, "mFd", "I");
    int fd = -1;
    while (1) {
        if (obj != NULL) {
            fd = (*env)->GetIntField(env, obj, mFd);
        }
        if (fd == -1 || obj == NULL)break;
        int size = 1024;
        jbyte buffer[size];
        int bytesRead = read(fd, buffer, size);
        if (bytesRead > 0) {
            jbyteArray bytes = (*env)->NewByteArray(env, bytesRead);
            (*env)->SetByteArrayRegion(env, bytes, 0, bytesRead, buffer);
            if (obj != NULL) (*env)->CallVoidMethod(env, obj, onData, bytes);
        }
        usleep(10000);
    }
    (*env)->DeleteGlobalRef(env, obj);
    obj = NULL;
    if (vm != NULL) {
        (*vm)->DetachCurrentThread(vm);
        vm = NULL;
    }
    return 0;
}

JNIEXPORT int JNICALL
Java_cn_lalaki_SerialPort_open(__unused JNIEnv *env, __unused jclass clazz, int fd, int speed) {
    if (fd == -1) return -1;
    struct termios cfg;
    tcflush(fd, TCIOFLUSH);
    tcgetattr(fd, &cfg);
    cfmakeraw(&cfg);
    cfsetospeed(&cfg, speed);
    tcsetattr(fd, TCSANOW, &cfg);
    (*env)->GetJavaVM(env, &vm);
    obj = (*env)->NewGlobalRef(env, clazz);
    pthread_t tid;
    pthread_create(&tid, NULL, read_comm, NULL);
    return fd;
}

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_write(__unused JNIEnv *env, __unused jclass clazz, int fd,
                                jbyteArray bytes) {
    jboolean copy;
    jbyte *pCData = (*env)->GetByteArrayElements(env, bytes, &copy);
    int len = (*env)->GetArrayLength(env, bytes);
    write(fd, pCData, len);
    if (copy) {
        (*env)->ReleaseByteArrayElements(env, bytes, pCData, JNI_ABORT);
    }
}

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_close(__unused JNIEnv *env, __unused jclass clazz, int fd) {
    close(fd);
}