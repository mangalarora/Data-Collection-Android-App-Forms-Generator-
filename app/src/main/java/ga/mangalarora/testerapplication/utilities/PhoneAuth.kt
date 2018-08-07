package ga.mangalarora.testerapplication.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import ga.mangalarora.testerapplication.loginactivities.SignupActivity
import kotlinx.android.synthetic.main.activity_recover_password.*
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.concurrent.TimeUnit

class PhoneAuth {

    companion object {


        @SuppressLint("StaticFieldLeak")
        var fa: Activity? = null
        var mVerificationId: String? = null
        private val mAuth = FirebaseAuth.getInstance()
        @SuppressLint("StaticFieldLeak")
        private val mStroe = FirebaseFirestore.getInstance()
        private var phn: String? = null
        var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
        private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


        fun authPhone(context: Activity, phoneNumber: String) {
            this.fa = context
            verificationcallbacks()
            phn = phoneNumber
            Toast.makeText(fa!!, "Auth Reached", Toast.LENGTH_LONG).show()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    context,               // Activity (for callback binding)
                    mCallbacks)        // OnVerificationStateChangedCallbacks

        }


        //------------------------------------------------------------------------------//
        //------------------------------------------------------------------------------//
        //--------------------------------------------------------------------------------//
        fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
            // [START verify_with_code]

            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            // [END verify_with_code]
            try {



                if (UpdatePhone.True && credential != null) {
                    Toast.makeText(fa!!.applicationContext, "Done Till", Toast.LENGTH_LONG).show()
                    mAuth.currentUser!!.updatePhoneNumber(credential).addOnCompleteListener {
                        if (!it.isSuccessful) {
                            UpdatePhone.True = false
                            fa!!.finish()
                            Toast.makeText(fa!!.applicationContext, "Failed", Toast.LENGTH_LONG).show()
                        } else {
                            UpdatePhone.True = false
                            mStroe.collection("users").document(mAuth.currentUser!!.uid).update("phone", phn).addOnSuccessListener {
                                mStroe.collection("phone").document(mAuth.currentUser!!.phoneNumber.toString()).delete()
                                val map: kotlin.collections.HashMap<kotlin.String, kotlin.Any> = HashMap()
                                map["email"] = mAuth.currentUser?.email.toString()
                                mStroe.collection("phone").document(phn.toString()).set(map)

                                fa!!.finish()
                            }
                        }
                    }
                } else {
                    UpdatePhone.True = false

                    if (RecoverPassword.True && credential != null ){
                        fa!!.querygroup.isVisible = false
                        fa!!.getCode.isVisible =false
                        fa!!.insertCodegroup.isVisible = false
                    }

                    if (RecoverPassword.True && credential == null)
                    {
                        fa!!.querygroup.isVisible = true
                        fa!!.getCode.isVisible = true
                        fa!!.insertCodegroup.isVisible = true
                        Toast.makeText(fa!!,"Invalid Credential",Toast.LENGTH_LONG).show()
                    }

                    if (SignupActivity.True) {
                        fa!!.scrollView2.isVisible = false
                        fa!!.otpgroup.isVisible = false
                        fa!!.verCom.isVisible = true
                    }

                    Toast.makeText(fa!!, "Verification is done with Code", Toast.LENGTH_LONG).show()
                    if (credential != null)
                    signInWithPhoneAuthCredential(credential)
                }
            } catch (e: Exception) {
                UpdatePhone.True = false
                RecoverPassword.True = false
                SignupActivity.True = false
            }

        }
//---------------------------------------------------------------------------------------//


        fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken?) {
            verificationcallbacks()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    fa!!,               // Activity (for callback binding)
                    mCallbacks,         // OnVerificationStateChangedCallbacks
                    token)            // ForceResendingToken from callbacks
        }

//-----------------------------------------------------------------------------------------//


        private fun verificationcallbacks() {


            mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                    if (SignupActivity.True) {
                        fa!!.scrollView2.isVisible = false
                        fa!!.otpgroup.isVisible = false
                        fa!!.verCom.isVisible = true

                    }
                    if (RecoverPassword.True) {
                        fa!!.querygroup.isVisible = false
                        fa!!.getCode.isVisible =false
                        fa!!.insertCodegroup.isVisible = false

                    }


                    if (UpdatePhone.True) {
                        Toast.makeText(fa!!.applicationContext, "Done In", Toast.LENGTH_LONG).show()

                        mAuth.currentUser!!.updatePhoneNumber(credential).addOnCompleteListener(fa!!) {
                            if (!it.isSuccessful) {

                                fa!!.finish()
                                Toast.makeText(fa!!.applicationContext, "Failed Tasksss", Toast.LENGTH_LONG).show()
                            } else {
                                mStroe.collection("users").document(mAuth.currentUser!!.uid).update("phone", phn)
                                fa!!.finish()
                                UpdatePhone.True = false
                            }
                        }

                    } else {
                        Toast.makeText(fa!!, "Verification Completed", Toast.LENGTH_LONG).show()
                        signInWithPhoneAuthCredential(credential)
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {

                    if (e is FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        if (SignupActivity.True) {
                            fa!!.scrollView2.isVisible = true
                            fa!!.otpgroup.isVisible = false
                        }

                        if (RecoverPassword.True) {
                            fa!!.querygroup.isVisible = true
                            fa!!.getCode.isVisible = true
                            fa!!.insertCodegroup.isVisible = true

                        }
                        if (UpdatePhone.True) {

                        }

                        // ...
                    } else if (e is FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        if (SignupActivity.True) {

                            fa!!.scrollView2.isVisible = true
                            fa!!.otpgroup.isVisible = false


                        }

                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {

                    mVerificationId = verificationId
                    mResendToken = token
                    Toast.makeText(fa!!, "Code Has Been Sent", Toast.LENGTH_LONG).show()
                    //verifyPhoneNumberWithCode(verificationId!!, "123456")

                    if (SignupActivity.True) {
                        fa!!.scrollView2.isVisible = false
                        fa!!.otpgroup.isVisible = true
                        fa!!.otpsentmsg.text = "Enter the O.T.P. has been sent to " + phn.toString()
                    }

                    if (RecoverPassword.True) {

                        fa!!.insertCodegroup.isVisible = true

                    }

                    if (UpdatePhone.True) {

                    }


                }
            }
        }

//-----------------------------------------------------------------------------------------//
        //------------------------------------------------------------------------------------//

        private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
            mAuth.signInWithCredential(credential).addOnCompleteListener(fa!!) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(fa!!, "SignIn Successful", Toast.LENGTH_LONG).show()
                    // Sign in success, update UI with the signed-in user's information

                    if (SignupActivity.True) {
                        Registration(fa!!).saveuserdata(phn.toString())
                        SignupActivity.True = false
                    }
                    if (RecoverPassword.True) {

                        fa!!.passwordgroup.isVisible = true
                        RecoverPassword.True = false
                    }


                    // ...
                } else {
                    SignupActivity.True = false
                    if(RecoverPassword.True){
                        fa!!.querygroup.isVisible = true
                        fa!!.getCode.isVisible = true
                        fa!!.insertCodegroup.isVisible = true
                    }

                    Toast.makeText(fa!!, "Verification failed", Toast.LENGTH_LONG).show()
                    // Sign in failed, display a message and update the UI

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(fa!!, "Auth Invalid Code", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
}





