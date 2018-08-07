package ga.mangalarora.testerapplication.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import ga.mangalarora.testerapplication.R
import ga.mangalarora.testerapplication.base.activitymain.MainActivity
import ga.mangalarora.testerapplication.dataclasses.Users
import kotlinx.android.synthetic.main.activity_signup.*
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*


class Registration(private val fa: Activity) {
    private val db = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    @SuppressLint("HardwareIds")

    fun saveuserdata(phn: String) {


        val username = fa.textInputEditText3.text.toString().trim()
        val pass = fa.textInputEditText99.text.toString().trim()
        val mail = fa.textInputEditText4.text.toString().trim()
        val gender:String = if( fa.radioButton4.isSelected) "Female" else "Male"
        val dob:String = fa.editText.text.toString()
        // registeruser(mail,pass,username)
        val user = mAuth.currentUser
        val date = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("hh:mm:ss:SS", Locale.getDefault()).format(Date())

        val locale = fa.resources.configuration.locale.country
        val tm = fa.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue = tm.networkCountryIso

        val ipadd = getLocalIpAddress()


        val infoBuffer = StringBuffer()

        infoBuffer.append("-------------------------------------\n")
        infoBuffer.append("Model :" + Build.MODEL + "\n")//The end-user-visible name for the end product.
        infoBuffer.append("Device: " + Build.DEVICE + "\n")//The name of the industrial design.
        infoBuffer.append("Manufacturer: " + Build.MANUFACTURER + "\n")//The manufacturer of the product/hardware.
        infoBuffer.append("Board: " + Build.BOARD + "\n")//The name of the underlying board, like "goldfish".
        infoBuffer.append("Brand: " + Build.BRAND + "\n")//The consumer-visible brand with which the product/hardware will be associated, if any.
        infoBuffer.append("Serial: " + Build.SERIAL + "\n")
        infoBuffer.append("-------------------------------------\n")


        val user1 = Users(username,dob, mail, user!!.phoneNumber.toString(), user.uid, pass, user.photoUrl.toString(), gender,"mangalarora.ga/id", user.displayName.toString(),  user.photoUrl.toString(), date, time, countrylocale = locale, countrytelephone = countryCodeValue, ipadd = NetInfo(fa).ipAddress, deviceinfo = infoBuffer.toString(), macadd = Utils.getMACAddress("wlan0"))
        // Create a new user with a first and last name

        try {
// Add a new document with a generated ID
            db.collection("users").document(user.uid)
                    .set(user1)
                    .addOnSuccessListener { Toast.makeText(fa.applicationContext, "DataStore Success ", Toast.LENGTH_LONG).show() }
                    .addOnFailureListener { Toast.makeText(fa.applicationContext, "DataStore failed ", Toast.LENGTH_LONG).show() }

            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(Uri.parse("android.resource://ga.mangalarora.testerapplication/" + R.drawable.logpnssssg))
                    .build()

            user.updateProfile(profileUpdates).addOnCompleteListener {
                if (it.isSuccessful) {

                    user.updateEmail(mail)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {

                                    user.sendEmailVerification()
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {

                                                    user.updatePassword(pass)
                                                            .addOnCompleteListener {
                                                                if (it.isSuccessful) {
                                                                 Toast.makeText(fa,"Reached Final Step",Toast.LENGTH_LONG).show()
                                                                    val intent = Intent(fa, MainActivity::class.java)
                                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                                    fa.startActivity(intent)
                                                                    fa.finish()

                                                                }
                                                            }


                                                }
                                            }


                                }
                            }


                }
            }


        } catch (e: Exception) {
            Toast.makeText(fa.applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: Exception) {

        }

        return null
    }


}