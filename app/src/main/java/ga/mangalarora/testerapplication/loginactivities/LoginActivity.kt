package ga.mangalarora.testerapplication.loginactivities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import ga.mangalarora.testerapplication.R
import ga.mangalarora.testerapplication.base.activitymain.MainActivity
import ga.mangalarora.testerapplication.utilities.GoogleSignInAuth
import ga.mangalarora.testerapplication.utilities.RecoverPassword
import ga.mangalarora.testerapplication.utilities.ValidationCheck
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.textColor
import java.util.*


class LoginActivity : AppCompatActivity() {
    private val callbackManager = CallbackManager.Factory.create()
    private var runs: Boolean = false
    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText
    private val flm = LoginManager.getInstance()
    private var found:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (mAuth.currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        username = findViewById(R.id.textInputEditText)
        password = findViewById(R.id.textInputEditText2)

        username.addTextChangedListener(tcl)
        password.addTextChangedListener(tcl)

       // username.setOnKeyListener(kls)

        //val accessToken = AccessToken.getCurrentAccessToken()
        //val isLoggedIn = accessToken != null && !accessToken.isExpired


        textView13.setOnClickListener {
            startActivity(Intent(applicationContext, RecoverPassword::class.java))
        }


        // Facebook Login
        imageView5.setOnClickListener {
            runs = true
            flm.logInWithReadPermissions(this, Arrays.asList("public_profile"))
        }

        val a = findViewById<View>(R.id.textView12) as TextView

        a.setOnClickListener {
            startActivity(Intent(applicationContext, SignupActivity::class.java))
            finish()
        }

        button2.setOnClickListener {
            ValidationCheck.proceed = true
            val pass = textInputEditText2.text.toString().trim()
            val mail = textInputEditText.text.toString().trim()
                when {
                    ValidationCheck.stringCheck("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})\$", textInputEditText, "email", mail) -> {

                        signinwithEmailPass(mail, pass)
                    }
                    ValidationCheck.stringCheck("^(\\+)([0-9]{2,3})([6-9]{1})([0-9]{4})([0-9]{5})", textInputEditText, "phone", mail) -> {

                        loginUserpp(mail, pass)
                    }
                    ValidationCheck.stringCheck("^([\\w]+[\\.]{0,1}[\\_]{0,}){5,15}", textInputEditText, "username", mail) -> {

                        loginUserup(mail, pass)
                    }
                    else -> {

                        textInputEditText.requestFocus()
                    }

                }

        }

         // Google Login
        imageView6.setOnClickListener {
            GoogleSignInAuth.signin(this)
        }

        //Facebook login Callbacks
        flm.registerCallback(callbackManager,fCallBack)
    }

    private val fCallBack =  object : FacebookCallback<LoginResult> {
        override fun onError(error: FacebookException?) {
            Toast.makeText(applicationContext, "FaileD fACEBOOK", Toast.LENGTH_LONG).show()
        }

        override fun onCancel() {

        }

        override fun onSuccess(result: LoginResult?) {
            var email = ""
            GraphRequest.GraphJSONObjectCallback { `object`, response ->
                try {
                    email = `object`.getString("email").toString()
                } catch (e: Exception) {

                }
            }
            handleFacebookAccessToken(result!!.accessToken, email)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        val progressDialog = indeterminateProgressDialog("Signing in to your Account ", "Logging In")
        progressDialog.show()
        progressDialog.setCanceledOnTouchOutside(false)
        //progressDialog.setIndeterminateDrawable()
        if (runs) {
            runs = false
            callbackManager.onActivityResult(requestCode, resultCode, data)
            super.onActivityResult(requestCode, resultCode, data)
            progressDialog.dismiss()
        }

        //Toast.makeText(applicationContext, "Activity Result 1", Toast.LENGTH_LONG).show()
        if (requestCode == GoogleSignInAuth.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                GoogleSignInAuth.firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                progressDialog.dismiss()

                // Google Sign In failed, update UI appropriately
            }
        }

    }


    private fun handleFacebookAccessToken(token: AccessToken, email: String) {

        //Toast.makeText(applicationContext, "Final Step Reached", Toast.LENGTH_LONG).show()
        val credential = FacebookAuthProvider.getCredential(token.token)
        try {

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information

                            /*val user = mAuth.currentUser
                            if (email != "")
                                user?.updateEmail(email)?.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        //Toast.makeText(applicationContext, "Successful Updated", Toast.LENGTH_LONG).show()
                                    }
                                }*/


                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(this@LoginActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()

                        }

                        // ...
                    }.addOnFailureListener {

                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()

                    }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        runs = false
        super.onBackPressed()
    }


    private fun loginUserpp(trim: String, trim1: String) {
        db.collection("users").whereEqualTo("phone",trim).addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            if (!querySnapshot!!.isEmpty)
            {
                  signInTask(querySnapshot.documents[0].getString("email").toString(),trim1)
            }
            else
            {
                Toast.makeText(applicationContext,"Phone no. Doesn't Exist",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loginUserup(trim: String, trim1: String) {
        db.collection("users").whereEqualTo("username", trim).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (!querySnapshot!!.isEmpty) {
                signInTask(querySnapshot.documents[0].getString("email").toString(), trim1)
            } else {
                Toast.makeText(applicationContext, "Username Doesn't Exist", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signinwithEmailPass(mail: String, pass: String) {

        db.collection("users").whereEqualTo("email", mail).addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            if (!querySnapshot!!.isEmpty) {

                signInTask(mail,pass)
            }
            else
            {
                Toast.makeText(applicationContext,"E-mail Doesn't Exist",Toast.LENGTH_LONG).show()
            }
        }


    }


    private val tcl: TextWatcher = object : TextWatcher {
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val uname: String = p0.toString().trim()
            val pass = password.text.toString().trim()

            if (uname.length >= 6 && pass.length>=6 && !found) {

                button2.isEnabled = true
                button2.textColor = Color.WHITE

            }
            else {

                button2.isEnabled = false
                button2.textColor = Color.parseColor("#FF017CFF")

            }

        }


        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }



    private fun userdata() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Name, email address, and profile photo Url
            val name = user.displayName

            val email = user.email

            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = user.uid
            Toast.makeText(applicationContext, "username : $name \n email : $email \n photourl : $photoUrl\nEmail Verified : $emailVerified\nUID : $uid", Toast.LENGTH_LONG).show()


        }
    }


    private fun signInTask(mail: String,pass: String) {

        try {
            mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    userdata()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    this.finish()
                } else {

                    Toast.makeText(applicationContext, "Authentication Failed",
                            Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {

                Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()

            }
        } catch (e: Exception) {
            if (e is FirebaseAuthRecentLoginRequiredException) {
                val credential = EmailAuthProvider
                        .getCredential(mail, pass)

// Prompt the user to re-provide their sign-in credentials
                mAuth.currentUser?.reauthenticate(credential)!!.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        this.finish()
                    }
                }
            }
        }
    }



}
