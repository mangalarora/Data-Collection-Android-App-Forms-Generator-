package ga.mangalarora.testerapplication.loginactivities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ga.mangalarora.testerapplication.R
import ga.mangalarora.testerapplication.utilities.PhoneAuth
import ga.mangalarora.testerapplication.utilities.ValidationCheck
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.textColor


class SignupActivity : AppCompatActivity(),View.OnClickListener {
    override fun onClick(p0: View?) {
        startActivity(Intent(applicationContext,LoginActivity::class.java))
        finish()
    }

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var country:String
    private lateinit var username:TextInputEditText
    private lateinit var email:TextInputEditText
    private lateinit var phone:TextInputEditText
    private lateinit var dob:EditText
    private lateinit var password: TextInputEditText
    private lateinit var button :TextView

    private var proceed:Boolean = false

    companion object {
        var True:Boolean = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        username = findViewById(R.id.textInputEditText3)
        email = findViewById(R.id.textInputEditText4)
        phone = findViewById(R.id.textInputEditText5)
        dob = findViewById(R.id.editText)
        password = findViewById(R.id.textInputEditText99)
        button = findViewById<View>(R.id.textView12) as TextView



       button.setOnClickListener(this)
       /* button.setOnClickListener {
            startActivity(Intent(applicationContext,LoginActivity::class.java))
            finish()
        }*/

        username.addTextChangedListener(tcl1)
        email.addTextChangedListener(tcl2)
        phone.addTextChangedListener(tcl3)
        dob.addTextChangedListener(tcl)
        password.addTextChangedListener(tcl)



        //------------------------------------------------------------------------------//
        //--------SIgn Up Entry--------------------------------------------------------//

        button5.setOnClickListener {
            True = true
            button5.isVisible = false
            progressBar2.isVisible = true
            signup()
        }

        verifybtn.setOnClickListener {
            PhoneAuth.verifyPhoneNumberWithCode(PhoneAuth.mVerificationId.toString(),editText16.text.toString())
        }
        resendotp.setOnClickListener {
            PhoneAuth.resendVerificationCode(country+phone.text.toString(),PhoneAuth.mResendToken)
        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        position: Int, id: Long) {
                country = (spinner.getItemAtPosition(position).toString())
                country = country.replace("^([a-zA-Z\\s\\-()]+)".toRegex(), "")
                country = country.replace("-", "")
                country = country.replace(")", "")
                Toast.makeText(applicationContext, country, Toast.LENGTH_LONG).show()
            }

        }
    }

    private  fun signup(){
        regularExpressionCheck()
        alreadyExistChecks()


    }

    private fun regularExpressionCheck(){
            proceed = false

            proceed = ValidationCheck.stringCheck("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})\$",email, "email", email.text.toString().trim())

            proceed = ValidationCheck.stringCheck("^(\\+)([0-9]{2,3})([1-9]{1})([0-9]{4})([0-9]{5})", phone, "phone", country +phone.text.toString())

            proceed = ValidationCheck.stringCheck("^([\\w]+[\\.]{0,1}[\\_]{0,}){5,15}", username, "username",  username.text.toString().trim())

    }

    private fun alreadyExistChecks(){


        db.collection("users").whereEqualTo("email",email.text.toString().trim()).addSnapshotListener { querySnapshot, firebaseFirestoreException ->

               if (!querySnapshot!!.isEmpty) {
                   email.error = "Email Already Exist"
                   proceed = false
               }

               db.collection("users").whereEqualTo("username",username.text.toString().trim()).addSnapshotListener { querySnapshot2, firebaseFirestoreException2 ->

                if (!querySnapshot2!!.isEmpty) {
                    username.error = "Username Already Exist"
                    proceed = false

                }
                db.collection("users").whereEqualTo("phone",country + phone.text.toString().trim()).addSnapshotListener { querySnapshot3, firebaseFirestoreException3 ->

                    if (!querySnapshot3!!.isEmpty)
                    {
                        phone.error = "Phone number Already Exist"
                        proceed = false
                    }
                    if(proceed) {
                        if (proceed)
                            PhoneAuth.authPhone(this, country + textInputEditText5.text.toString())
                        else {
                            Toast.makeText(applicationContext, "Error Occured", Toast.LENGTH_LONG).show()

                            button5.isVisible = true
                            progressBar2.isVisible = false
                        }
                        proceed = false
                    }
                    else{
                        button5.isVisible = true
                        progressBar2.isVisible = false
                    }
                }

            }



        }

    }




     private val tcl :TextWatcher= object :TextWatcher{
         override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (username.text.toString().trim().length>=6&&phone.text.toString().trim().length==10&&email.text.toString().trim().length>=6&&dob.text.toString().trim().length==10&&password.text.toString().trim().length>=6){

                button5.isEnabled = true
                button5.textColor = Color.WHITE

            }
             else{
                button5.isEnabled = false
                button5.textColor = Color.parseColor("#FF017CFF")
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

         }

         override fun afterTextChanged(p0: Editable?) {



         }
     }

    private val tcl1 :TextWatcher= object :TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (username.text.toString().trim().length>=6&&phone.text.toString().trim().length==10&&email.text.toString().trim().length>=6&&dob.text.toString().trim().length==10&&password.text.toString().trim().length>=6){

                button5.isEnabled = true
                button5.textColor = Color.WHITE

            }
            else{
                button5.isEnabled = false
                button5.textColor = Color.parseColor("#FF017CFF")
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            db.collection("users").whereEqualTo("username",p0.toString().trim().toLowerCase()).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (!querySnapshot!!.isEmpty)
                    username.error = "Username Already Exists"

            }
        }
    }

    private val tcl2 :TextWatcher= object :TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (username.text.toString().trim().length>=6&&phone.text.toString().trim().length==10&&email.text.toString().trim().length>=6&&dob.text.toString().trim().length==10&&password.text.toString().trim().length>=6){

                button5.isEnabled = true
                button5.textColor = Color.WHITE

            }
            else{
                button5.isEnabled = false
                button5.textColor = Color.parseColor("#FF017CFF")
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            db.collection("users").whereEqualTo("email",p0.toString().trim().toLowerCase()).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (!querySnapshot!!.isEmpty)
                    email.error = "E-mail Already Exists"

            }
        }
    }


    private val tcl3 :TextWatcher= object :TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (username.text.toString().trim().length>=6&&phone.text.toString().trim().length==10&&email.text.toString().trim().length>=6&&dob.text.toString().trim().length==10&&password.text.toString().trim().length>=6){

                button5.isEnabled = true
                button5.textColor = Color.WHITE

            }
            else{
                button5.isEnabled = false
                button5.textColor = Color.parseColor("#FF017CFF")
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            if (p0.toString().trim().length>=10)
            db.collection("users").whereEqualTo("phone",country + p0.toString().trim().toLowerCase()).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (!querySnapshot!!.isEmpty)
                    phone.error = "Phone number Already Exists"

            }
        }
    }



}

