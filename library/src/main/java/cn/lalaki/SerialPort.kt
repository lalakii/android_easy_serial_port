package cn.lalaki

import android.os.ParcelFileDescriptor
import java.io.File

class SerialPort(path: String, speed: Int, private val mDataCallBack: DataCallback?) {
    private var mFd = -1

    init {
        System.loadLibrary("comm1")
        try {
            val pfd = ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_WRITE)
            mFd = open(pfd.detachFd(), speed)
            pfd.close()
        } catch (_: Exception) {
            mDataCallBack?.onData(byteArrayOf(0xE))
        }
    }

    private fun onNativeData(data: ByteArray) {
        val fd = mFd
        if (fd != -1)
            mDataCallBack?.onData(data)
    }

    fun write(data: ByteArray) {
        val fd = mFd
        if (fd != -1)
            write(fd, data)
    }

    fun close() {
        val fd = mFd
        if (fd != -1) {
            close(fd)
            mFd = -1
        }
    }

    interface DataCallback {
        fun onData(data: ByteArray?)
    }

    private external fun open(fd: Int, speed: Int): Int
    private external fun write(fd: Int, data: ByteArray)
    private external fun close(fd: Int)
}