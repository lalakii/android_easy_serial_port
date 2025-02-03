#include "cn_lalaki_SerialPort.h"
#include <termios.h>
#include <pthread.h>
#include <unistd.h>

#define MAX_THREAD_LIMIT 64
struct {
    pthread_t tid;
    jobject clazz;
} threads[MAX_THREAD_LIMIT];
JavaVM *vm;
jfieldID mFD;
jmethodID onNativeData;
pthread_mutex_t mutex;

void *comm_read() {
    JNIEnv *env;
    (*vm)->AttachCurrentThread(vm, &env, NULL);
    jclass clazz = NULL;
    for (int i = 0; i < MAX_THREAD_LIMIT; i++) {
        if (pthread_equal(pthread_self(), threads[i].tid)) {
            clazz = threads[i].clazz;
            if (mFD == NULL || onNativeData == NULL) {
                jclass obj = (*env)->GetObjectClass(env, clazz);
                mFD = (*env)->GetFieldID(env, obj, "mFD", "I");
                onNativeData = (*env)->GetMethodID(env, obj, "onNativeData", "([B)V");
            }
            break;
        }
    }
    if (clazz != NULL) {
        int fd;
        while ((fd = (*env)->GetIntField(env, clazz, mFD)) != -1) {
            int size = 512;
            jbyte data[size];
            int len = read(fd, data, size);
            if (len > 0) {
                jbyteArray bytes = (*env)->NewByteArray(env, len);
                (*env)->SetByteArrayRegion(env, bytes, 0, len, data);
                (*env)->CallVoidMethod(env, clazz, onNativeData, bytes);
            }
            usleep(10000);
        }
    }
    if (vm != NULL) {
        pthread_mutex_lock(&mutex);
        int isFree = 1;
        for (int i = 0; i < MAX_THREAD_LIMIT; i++) {
            if (threads[i].clazz != NULL) {
                if (threads[i].clazz == clazz) {
                    (*env)->DeleteGlobalRef(env, clazz);
                    threads[i].clazz = clazz = NULL;
                } else {
                    isFree = 0;
                    if (clazz == NULL) {
                        break;
                    }
                }
            }
        }
        (*vm)->DetachCurrentThread(vm);
        pthread_mutex_unlock(&mutex);
        if (isFree) {
            onNativeData = NULL;
            mFD = NULL;
            vm = NULL;
            pthread_mutex_destroy(&mutex);
        }
    }
    return 0;
}

JNIEXPORT jint JNICALL
Java_cn_lalaki_SerialPort_open(
        JNIEnv *env,
        jclass clazz,
        int fd,
        int speed) {
    if (fd == -1) {
        return -1;
    }
    tcflush(fd, TCIOFLUSH);
    struct termios cfg;
    if (tcgetattr(fd, &cfg) != 0) {
        close(fd);
        return -1;
    }
    cfmakeraw(&cfg);
    cfsetospeed(&cfg, speed);
    cfsetispeed(&cfg, speed);
    if (tcsetattr(fd, TCSANOW, &cfg) != 0) {
        close(fd);
        return -1;
    }
    for (int i = 0; i < MAX_THREAD_LIMIT; i++) {
        if (threads[i].clazz == NULL) {
            if (vm == NULL) {
                pthread_mutex_init(&mutex, NULL);
                (*env)->GetJavaVM(env, &vm);
            }
            threads[i].clazz = (*env)->NewGlobalRef(env, clazz);
            pthread_create(&threads[i].tid, NULL, comm_read, NULL);
            break;
        }
    }
    return fd;
}

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_write(
        JNIEnv *env,
        __unused jclass _,
        int fd,
        jbyteArray bytes) {
    jbyte *data = (*env)->GetByteArrayElements(env, bytes, NULL);
    if (data != NULL) {
        write(fd, data, (*env)->GetArrayLength(env, bytes));
        (*env)->ReleaseByteArrayElements(env, bytes, data, JNI_ABORT);
    }
}

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_writeByte(
        __unused JNIEnv *e,
        __unused jclass _,
        int fd,
        jbyte data) {
    write(fd, &data, 1);
}

JNIEXPORT void JNICALL
Java_cn_lalaki_SerialPort_close(
        __unused JNIEnv *e,
        __unused jclass _,
        int fd) {
    close(fd);
}