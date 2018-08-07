package ga.mangalarora.testerapplication.utilities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import ga.mangalarora.testerapplication.R
import ga.mangalarora.testerapplication.base.activitymain.MainActivity
import ga.mangalarora.testerapplication.loginactivities.LoginActivity
import ga.mangalarora.testerapplication.start.activities.StartActivity
import kotlinx.android.synthetic.main.activity_recover_password.*

class RecoverPassword : AppCompatActivity() {

    companion object {
         var True:Boolean = false
     }
    private var mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var querySnapshot:QuerySnapshot
    private lateinit var code:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)
        True = false
        getCode.isVisible = false
        setSupportActionBar(toolbar2)
        passwordgroup.isVisible = false
        passwordChangeSuccessful.isVisible =false
        insertCodegroup.isVisible = false



        radioButton.setOnClickListener {
            radioButton.isClickable = false
            radioButton2.isClickable = false
            val mail = querySnapshot.documents[0].getString("email")
            mAuth.sendPasswordResetEmail(mail.toString())
            Toast.makeText(applicationContext,"Reset Password Email has been sent ",Toast.LENGTH_LONG).show()
            startActivity(Intent(applicationContext,LoginActivity::class.java))
            this.finish()
        }

        radioButton2.setOnClickListener {
            radioButton.isClickable = false
            radioButton2.isClickable = false
            val phone = querySnapshot.documents[0].getString("phone")
            True = true
            PhoneAuth.authPhone(this,phone.toString())
        }

        button7.setOnClickListener {
            if (editText10.text.toString()==editText11.text.toString())
            {
                mAuth.currentUser?.updatePassword(editText10.text.toString())?.addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        passwordgroup.isVisible = false
                        passwordChangeSuccessful.isVisible = true
                        Handler().postDelayed({
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }, 5000)

                    }
                    else{
                        Toast.makeText(applicationContext,"Retry password",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        editText4.addTextChangedListener(tcl)
        editText5.addTextChangedListener(tcl1)
        editText6.addTextChangedListener(tcl2)
        editText8.addTextChangedListener(tcl3)
        editText9.addTextChangedListener(tcl4)
        editText7.addTextChangedListener(tcl5)



    }


    fun btnPlayClicked(view:View){
        val mail = editText2.text.toString().trim()

        when{

            ValidationCheck.stringCheck("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})\$", editText2, "email", mail) -> {
                db.collection("users").whereEqualTo("email",mail).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (!querySnapshot!!.isEmpty){
                        this.querySnapshot = querySnapshot
                        getCode.isVisible = true
                        editText2.isEnabled = false
                    }
                }
            }
            ValidationCheck.stringCheck("^(\\+)([0-9]{2,3})([6-9]{1})([0-9]{4})([0-9]{5})", editText2, "phone", mail) -> {
                db.collection("users").whereEqualTo("phone",mail).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (!querySnapshot!!.isEmpty) {
                        this.querySnapshot = querySnapshot
                        getCode.isVisible = true
                        editText2.isEnabled = false
                    }
                }

            }
            ValidationCheck.stringCheck("^([\\w]+[\\.]{0,1}[\\_]{0,}){5,15}", editText2, "username", mail) -> {
                db.collection("users").whereEqualTo("username",mail).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (!querySnapshot!!.isEmpty) {
                        this.querySnapshot = querySnapshot
                        getCode.isVisible = true
                        getCode.visibility = View.VISIBLE
                        editText2.isEnabled = false
                    }
                }

            }
            else -> {
                editText2.isEnabled = true
                editText2.error = "No data Found"
                editText2.requestFocus()
            }

        }





        
    }
    private val tcl = object : TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                  if (!p0!!.isEmpty())
                      editText5.requestFocus()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            code = editText4.text.toString().trim()+editText5.text.toString().trim()+editText6.text.toString().trim()+editText8.text.toString().trim()+editText9.text.toString().trim()+editText7.text.toString().trim()
            if (code.length == 6)
                PhoneAuth.verifyPhoneNumberWithCode(PhoneAuth.mVerificationId.toString(),code)
        }

    }
    private val tcl1 = object : TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!p0!!.isEmpty())
                editText6.requestFocus()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            code = editText4.text.toString().trim()+editText5.text.toString().trim()+editText6.text.toString().trim()+editText8.text.toString().trim()+editText9.text.toString().trim()+editText7.text.toString().trim()
            if (code.length == 6)
                PhoneAuth.verifyPhoneNumberWithCode(PhoneAuth.mVerificationId.toString(),code)


        }

    }
    private val tcl2 = object : TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!p0!!.isEmpty())
                editText8.requestFocus()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            code = editText4.text.toString().trim()+editText5.text.toString().trim()+editText6.text.toString().trim()+editText8.text.toString().trim()+editText9.text.toString().trim()+editText7.text.toString().trim()
            if (code.length == 6)
                PhoneAuth.verifyPhoneNumberWithCode(PhoneAuth.mVerificationId.toString(),code)


        }

    }
    private val tcl3 = object : TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!p0!!.isEmpty())
                editText9.requestFocus()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            code = editText4.text.toString().trim()+editText5.text.toString().trim()+editText6.text.toString().trim()+editText8.text.toString().trim()+editText9.text.toString().trim()+editText7.text.toString().trim()
            if (code.length == 6)
                PhoneAuth.verifyPhoneNumberWithCode(PhoneAuth.mVerificationId.toString(),code)


        }

    }
    private val tcl4 = object : TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!p0!!.isEmpty())
                editText7.requestFocus()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            code = editText4.text.toString().trim()+editText5.text.toString().trim()+editText6.text.toString().trim()+editText8.text.toString().trim()+editText9.text.toString().trim()+editText7.text.toString().trim()
            if (code.length == 6)
                PhoneAuth.verifyPhoneNumberWithCode(PhoneAuth.mVerificationId.toString(),code)

        }

    }
    private val tcl5 = object : TextWatcher{
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!p0!!.isEmpty())
                editText7.requestFocus()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            code = editText4.text.toString().trim()+editText5.text.toString().trim()+editText6.text.toString().trim()+editText8.text.toString().trim()+editText9.text.toString().trim()+editText7.text.toString().trim()
            if (code.length == 6)
                PhoneAuth.verifyPhoneNumberWithCode(PhoneAuth.mVerificationId.toString(),code)


        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.reset, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(applicationContext,RecoverPassword::class.java))
                finish()
                super.onOptionsItemSelected(item)

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        RecoverPassword.True = false
        mAuth.signOut()
        if (mAuth.currentUser != null) {
            mAuth.signOut()
        }
        super.onBackPressed()
    }

}
