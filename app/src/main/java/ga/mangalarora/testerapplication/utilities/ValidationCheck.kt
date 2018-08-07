package ga.mangalarora.testerapplication.utilities

import android.annotation.SuppressLint
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import ga.mangalarora.testerapplication.loginactivities.LoginActivity
import java.util.regex.*

class ValidationCheck {

    companion object {
        var proceed: Boolean = true
        var flag: Boolean = false
        @SuppressLint("StaticFieldLeak")
        private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

        fun stringCheck(regex: String, view: TextInputEditText, str: String, chr: String): Boolean {
            if (emptyCheck(view)) {
                return if (!Pattern.compile(regex).matcher(chr).matches()) {
                    flag = false
                    proceed = false
                    view.requestFocus()

                    false
                } else true
            }
            return false
        }

        private fun emptyCheck(view: TextInputEditText): Boolean {
            if (view.text.toString().trim().isEmpty()) {
                view.error = "Field Required"
                view.requestFocus()
                proceed = false
                flag = false
                return false
            }
            return true
        }


        private fun alreadyExists(str: String, chr: String): Boolean {

            var boolean:Boolean =false
            db.collection("users").whereEqualTo(str, chr).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (!querySnapshot!!.isEmpty)
                    boolean = true

            }
            return boolean
        }
    }

}