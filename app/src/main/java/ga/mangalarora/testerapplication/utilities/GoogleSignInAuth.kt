package ga.mangalarora.testerapplication.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import ga.mangalarora.testerapplication.R
import ga.mangalarora.testerapplication.base.activitymain.MainActivity
import ga.mangalarora.testerapplication.dataclasses.Users
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

class GoogleSignInAuth {

    companion object {


        const val RC_SIGN_IN = 123
        @SuppressLint("StaticFieldLeak")
        private lateinit var fa: Activity
        private val mAuth = FirebaseAuth.getInstance()
        @SuppressLint("StaticFieldLeak")
        private val db = FirebaseFirestore.getInstance()

        fun signin(fa: Activity) {

            this.fa = fa
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(fa.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build()!!
            val mGoogleSignInClient = GoogleSignIn.getClient(fa, gso)!!
            val signInIntent = mGoogleSignInClient.signInIntent


            fa.startActivityForResult(signInIntent, RC_SIGN_IN)


        }

        fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            mAuth.signInWithCredential(credential).addOnCompleteListener(fa) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(fa.applicationContext, "Done", Toast.LENGTH_LONG).show()


                    val docRef = db.document("email/${acct.email.toString()}")

                    docRef.get().addOnSuccessListener {
                        if (it.exists()) {
                            val intent = Intent(fa, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            fa.startActivity(intent)
                            fa.finish()
                        } else {
                            val user = mAuth.currentUser


                            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
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


                            //val user1 = Users(username, mail, user?.phoneNumber.toString(), user?.uid!!, pass, user.photoUrl.toString(), "Hey There! I'm using Himawari", "I'm Awesome", "Male", "mangalarora.esy.es", user.displayName.toString(),0,0,0,user.photoUrl.toString(),date,time,countrylocale = locale,countrytelephone = countryCodeValue,ipadd = ipadd.toString(),deviceinfo = infoBuffer.toString(), macadd = Utils.getMACAddress("wlan0"))
                            // Create a new user with a first and last name


                            val user1 = Users(acct.id.toString(),"12.12.1995", user?.email.toString(), user?.phoneNumber.toString(), user?.uid!!, "GoogleLogIn", user.photoUrl.toString(), "Male", "mangalarora.esy.es", user.displayName.toString(), user.photoUrl.toString(), date, time, countrylocale = locale, countrytelephone = countryCodeValue, ipadd = NetInfo(fa).ipAddress, deviceinfo = infoBuffer.toString(), macadd = Utils.getMACAddress("wlan0"))
                            // Create a new user with a first and last name
                            try {
                                db.collection("email").document(acct.email.toString()).set(user1)
                                db.collection("users").document(user.uid).set(user1).addOnSuccessListener {
                                    val intent = Intent(fa, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    fa.startActivity(intent)
                                    fa.finish()

                                }.addOnFailureListener {

                                }
                            } catch (e: Exception) {

                            }
                            //Snackbar.make(findViewById(R.id.), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
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


}

