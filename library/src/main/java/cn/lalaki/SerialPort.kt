package cn.lalaki

import android.os.ParcelFileDescriptor

class SerialPort(path: String, speed: Int, private val mDataCallBack: DataCallback?) {
    private var mFD = -1

    init {
        System.loadLibrary("comm1")
        try {
            val pFD =
                ParcelFileDescriptor.open(java.io.File(path), ParcelFileDescriptor.MODE_READ_WRITE)
            mFD = pFD.detachFd()
            try {
                pFD.close()
            } catch (_: Exception) {
            }
            if (mFD == -1) {
                throw Exception()
            }
            open(mFD, speed)
        } catch (_: Exception) {
            mDataCallBack?.onData(byteArrayOf(0xE))
        }
    }

    private fun onNativeData(data: ByteArray) {
        if (mFD != -1) {
            mDataCallBack?.onData(data)
        }
    }

    fun write(data: ByteArray) {
        val fd = mFD
        if (fd != -1) {
            write(fd, data)
        }
    }

    fun close() {
        val fd = mFD
        if (fd != -1) {
            close(fd)
            mFD = -1
        }
    }

    interface DataCallback {
        fun onData(data: ByteArray)
    }

    private external fun open(fd: Int, speed: Int)
    private external fun write(fd: Int, data: ByteArray)
    private external fun close(fd: Int)
}