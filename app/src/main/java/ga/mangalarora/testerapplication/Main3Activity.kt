package ga.mangalarora.testerapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.facebook.login.LoginManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ga.mangalarora.testerapplication.base.activitymain.AboutActivity
import ga.mangalarora.testerapplication.base.activitymain.MainActivity
import ga.mangalarora.testerapplication.loginactivities.LoginActivity
import ga.mangalarora.testerapplication.start.activities.StartActivity
import ga.mangalarora.testerapplication.utilities.DialogClassAdd
import ga.mangalarora.testerapplication.utilities.EditProfileActivity
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.app_bar_main3.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import org.jetbrains.anko.toast


class Main3Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DialogClassAdd.Listener {
    private lateinit var formId:String
    private lateinit var ll:LinearLayout
    private var hashMap =  HashMap<String,Any>()
    private lateinit var gso: GoogleSignInOptions
    private val adRequest = AdRequest.Builder().build()!!
    private var saved:Boolean = false
    private var back:Int=0

    override fun applyTexts( id: Int?, type: String, labelname: String?) {
            if (id != null) {
                val rowView = LayoutInflater.from(this).inflate(id,null)
                val a = rowView.findViewById<View>(R.id.labelText) as TextView

                if (rowView.findViewById<View>(R.id.fieldText) is AdView)
                {
                    val adView = rowView.findViewById<View>(R.id.fieldText) as AdView
                    adView.apply {
                        adUnitId = labelname.toString().trim()
                        adSize = AdSize.BANNER
                        loadAd(adRequest)
                    }



                }

                a.text = labelname

                rowView.onLongClick {
                    ll.removeView(rowView)
                    hashMap.remove(labelname)
                }

                val button = Button(this)
                button.text = "Ad Button"
                button.setOnClickListener {
                    Toast.makeText(applicationContext,"It's Great",Toast.LENGTH_LONG).show()
                }

                hashMap[labelname.toString()] = id
                // Add the new row before the add field button.
                ll.addView(rowView, ll.childCount)
                //ll.addView(button, ll.childCount)

            }
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        setSupportActionBar(toolbar)
         ll = findViewById(R.id.mainXml)

        val a : ArrayList<*>? = intent.extras.get("dataE") as ArrayList<*>?
        toolbar.title= a!![0].toString()
        formId = a[1].toString()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()!!

        fab2.setOnClickListener {
            openDialog()
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //      .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun openDialog() {
        val dialogClass = DialogClassAdd()
        dialogClass.show(supportFragmentManager,"Add Element")

    }

    override fun onBackPressed() {
        back += 1
        Handler().postDelayed({
            if (back>=2) {
                super.onBackPressed()
            }
            back = 0
        }, 2000)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        if(saved ){

            super.onBackPressed()
        }
        else{
            Toast.makeText(applicationContext,"Please save your form first Or Press Back button again to Exit",Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.save_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_save -> {
                  hashMap["formId"] = formId
                 FirebaseFirestore.getInstance().collection("Forms").document(formId).set(hashMap, SetOptions.merge()).addOnCompleteListener {
                     if (it.isSuccessful)
                     {
                         saved = true
                         Toast.makeText(applicationContext,"Your form has been successfully saved",Toast.LENGTH_LONG).show()
                         startActivity(Intent(applicationContext,MainActivity::class.java))
                         this.finish()
                         return@addOnCompleteListener
                     }
                     else{
                         toast("FAILED TO Save")
                     }
                 }

                 return true

            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_camera -> {
                // Handle the camera action
                startActivity(Intent(applicationContext, EditProfileActivity::class.java))
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {


            }
            R.id.nav_share -> {


            }
            R.id.nav_about ->{
                startActivity(Intent(applicationContext, AboutActivity::class.java))
            }
            R.id.nav_send -> {
                FirebaseAuth.getInstance().signOut()
                GoogleSignIn.getClient(this,gso).signOut()
                LoginManager.getInstance().logOut()
                val progressDialog = indeterminateProgressDialog("Please wait while we safely Loging you out ", "Logging Out")
                progressDialog.show()
                progressDialog.setCanceledOnTouchOutside(false)
                Handler().postDelayed({
                    progressDialog.dismiss()
                    val i = Intent(this, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                }, 4000)


            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onStart() {
        back  = 0
        val a : ArrayList<*>? = intent.extras.get("dataE") as ArrayList<*>?
        toolbar.title= a!![0].toString()
        formId = a[1].toString()

        super.onStart()

    }





}
