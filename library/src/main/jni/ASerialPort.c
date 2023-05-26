#include <unistd.h>
#include <termios.h>
#include "com_iamverylovely_ASerialPort.h"

JNIEXPORT int JNICALL
Java_com_iamverylovely_ASerialPort_read
        (__unused JNIEnv *env, __unused jclass clazz, jint fd, jint speed) {
    if (fd == -1) {
        return -1;
    }

    struct termios cfg;
    if (tcgetattr(fd, &cfg)) {
        close(fd);
        return -1;
    }

    cfmakeraw(&cfg);
    cfsetispeed(&cfg, speed);
    cfsetospeed(&cfg, speed);

    if (tcsetattr(fd, TCSANOW, &cfg)) {
        close(fd);
        return -1;
    }

    return fd;
}