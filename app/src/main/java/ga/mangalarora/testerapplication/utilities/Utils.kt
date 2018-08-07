package ga.mangalarora.testerapplication.utilities

import java.io.*
import java.net.*
import java.util.*
import kotlin.experimental.and

//import org.apache.http.conn.util.InetAddressUtils;

object Utils {

    /**
     * Convert byte array to hex string
     * @param bytes
     * @return
     */
    fun bytesToHex(bytes: ByteArray): String {
        val sbuf = StringBuilder()
        for (idx in bytes.indices) {
            val intVal = bytes[idx] and 0xff.toByte()
            if (intVal < 0x10) sbuf.append("0")
            sbuf.append(Integer.toHexString(intVal.toInt()).toUpperCase())
        }
        return sbuf.toString()
    }

    /**
     * Get utf8 byte array.
     * @param str
     * @return  array of NULL if error was found
     */
    fun getUTF8Bytes(str: String): ByteArray? {
        try {
            return str.toByteArray(charset("UTF-8"))
        } catch (ex: Exception) {
            return null
        }

    }

    /**
     * Load UTF8withBOM or any ansi text file.
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    @Throws(java.io.IOException::class)
    fun loadFileAsString(filename: String): String {
        val BUFLEN = 1024
        val bis = BufferedInputStream(FileInputStream(filename), BUFLEN)
        try {
            val baos = ByteArrayOutputStream(BUFLEN)
            val bytes = ByteArray(BUFLEN)
            var isUTF8 = false
            var read: Int
            var count = 0
            while ((bis.read(bytes)) != -1) {
                read = bis.read(bytes)
                if (count == 0 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
                    isUTF8 = true
                    baos.write(bytes, 3, read - 3) // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read)
                }
                count += read
            }
            return if (isUTF8) {
                String(baos.toByteArray())
            } else String(baos.toByteArray())
        } finally {
            try {
                bis.close()
            } catch (ex: Exception) {
            }

        }
    }


    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    fun getMACAddress(interfaceName: String?): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (interfaceName != null) {
                    if (!intf.name.equals(interfaceName, ignoreCase = true)) continue
                }
                val mac = intf.hardwareAddress ?: return ""
                val buf = StringBuilder()
                for (idx in mac.indices)
                    buf.append(String.format("%02X:", mac[idx]))
                if (buf.isNotEmpty()) buf.deleteCharAt(buf.length - 1)
                return buf.toString()
            }
        } catch (ex: Exception) {
        }
        // for now eat exceptions
        return ""
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        // for now eat exceptions
        return ""
    }

}