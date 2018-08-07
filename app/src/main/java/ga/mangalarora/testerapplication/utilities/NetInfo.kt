package ga.mangalarora.testerapplication.utilities


import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import java.net.NetworkInterface
import java.net.SocketException

/**
 * Created by Aylar-HP on 26/08/2015.
 */
class NetInfo(context: Activity) {

    private var connManager: ConnectivityManager? = null
    private var wifiManager: WifiManager? = null
    private var wifiInfo: WifiInfo? = null

    val currentNetworkType: Int
        get() {
            if (null == connManager)
                return 0
            val netinfo = connManager!!.activeNetworkInfo
            return netinfo.type
        }

    val wifiIpAddress: String
        get() {
            if (null == wifiManager || null == wifiInfo)
                return ""

            val ipAddress = wifiInfo!!.ipAddress
            return String.format("%d.%d.%d.%d",
                    ipAddress and 0xff,
                    ipAddress shr 8 and 0xff,
                    ipAddress shr 16 and 0xff,
                    ipAddress shr 24 and 0xff)
        }

    val wiFiMACAddress: String
        get() = if (null == wifiManager || null == wifiInfo) "" else wifiInfo!!.macAddress

    val wiFiSSID: String
        get() = if (null == wifiManager || null == wifiInfo) "" else wifiInfo!!.ssid


    val ipAddress: String
        get() {
            var ipaddress = ""
            try {
                val enumnet = NetworkInterface.getNetworkInterfaces()
                var netinterface: NetworkInterface?
                while (enumnet.hasMoreElements()) {
                    netinterface = enumnet.nextElement()

                    val enumip = netinterface!!.inetAddresses
                    while (enumip.hasMoreElements()) {
                        val inetAddress = enumip.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            ipaddress = inetAddress.hostAddress

                            break
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }

            return ipaddress


        }

    init {
        connManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiInfo = wifiManager!!.connectionInfo
    }
}
