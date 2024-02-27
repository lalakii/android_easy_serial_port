package cn.lalaki.comm

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.lalaki.SerialPort
import cn.lalaki.comm.databinding.MainBinding
import com.google.common.hash.HashCode
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: MainBinding
    private var serialPort: SerialPort? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main)
        binding.context = this
        val ports = File("/dev/").listFiles { _, s -> s.contains("ttys", ignoreCase = true) }
            ?.sortedBy { it.name }?.map { it.absolutePath }
        if (ports != null)
            binding.serialPorts.adapter = ArrayAdapter(this, R.layout.item, ports)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.open -> {
                val port = binding.serialPorts.selectedItem.toString().trim()
                if (port.isNotEmpty()) {
                    //BaudRate: B115200, see BaudRate value: https://github.com/torvalds/linux/blob/master/include/uapi/asm-generic/termbits.h
                    val b115200 = "0010002".toInt(8)
                    serialPort = SerialPort(port, b115200, object : SerialPort.DataCallback {
                        override fun onData(data: ByteArray) {
                            //Hex decode
                            val hexStr = HashCode.fromBytes(data).toString()
                            runOnUiThread {
                                val sc = binding.scrollView
                                sc.fullScroll(View.FOCUS_DOWN)
                                sc.post { binding.input.requestFocus() }
                                binding.output.append(hexStr + "\n")
                            }
                        }
                    })
                    p0.visibility = View.GONE
                    binding.close.visibility = View.VISIBLE
                }
            }

            R.id.send -> {
                if (serialPort == null) {
                    Toast.makeText(this, "Serial port not open", Toast.LENGTH_SHORT).show()
                    return
                }
                //Hex encode
                val trimText =
                    binding.input.text.toString().replace("\\s*".toRegex(), "").lowercase()
                try {
                    val hexData = HashCode.fromString(trimText).asBytes()
                    // Write Hex data:
                    serialPort!!.write(hexData)
                    binding.output.append(trimText + "\n")
                } catch (ignored: IllegalArgumentException) {
                    Toast.makeText(this, "Wrong hex string!", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.clear -> {
                binding.output.text = ""
            }

            R.id.close -> {
                serialPort?.close()
                serialPort = null
                p0.visibility = View.GONE
                binding.open.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serialPort?.close()
        serialPort = null
    }
}