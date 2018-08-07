package ga.mangalarora.testerapplication.base.activitymain

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ga.mangalarora.testerapplication.R
import ga.mangalarora.testerapplication.loginactivities.LoginActivity
import ga.mangalarora.testerapplication.start.activities.StartActivity
import ga.mangalarora.testerapplication.utilities.DialogClass
import ga.mangalarora.testerapplication.utilities.EditProfileActivity
import ga.mangalarora.testerapplication.utilities.SPagerAdapter
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import org.jetbrains.anko.indeterminateProgressDialog
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NewForm.Listener {



    private lateinit  var hashMap: HashMap<String,Any?>
    private var hashMap2=HashMap<String?,Any?>()
    private var hashMap3=HashMap<String?,Any?>()
    private  var view: View? = null
    private var idForm:String? = null
    private lateinit var gso: GoogleSignInOptions
    private var back:Int = 0



    override fun sens(hashMap: HashMap<String, Any>, idForm: String?, hashMap2: HashMap<String, Any?>) {
        this.hashMap = hashMap2
        this.idForm =idForm
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)
        //
        view = findViewById<View>(R.id.openXml) as LinearLayout?
        fab2.setOnClickListener {
              openDialog()
           // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                   // .setAction("Action", null).show()
        }

         gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()!!



        viewPager2.apply {
            adapter = SPagerAdapter(supportFragmentManager)

        }


        tabLayout.apply {
            setupWithViewPager(viewPager2)
        }


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun openDialog() {
        val dialog = DialogClass()
        dialog.show(this.supportFragmentManager,"Adding new Widget")


    }



    override fun onBackPressed() {
        back += 1
        Handler().postDelayed({
            if (back>=2)
                super.onBackPressed()
            back = 0
        }, 2000)

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            Toast.makeText(applicationContext,"Press back again to Exit",Toast.LENGTH_LONG).show()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.save_button, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_save -> {

                if (idForm!=null) {
                    val progressDialog = indeterminateProgressDialog("Please wait while we are saving ur data ", "Saving Form...")
                    progressDialog.show()
                    // progressDialog.setCanceledOnTouchOutside(false)

                    for ((key, value) in hashMap) {
                        if (key != "formId") {
                            var a: Any? = null

                            when (value) {
                                is EditText -> {
                                    a = value

                                    if (a.text!!.contains("content://com.android.*".toRegex())) {
                                        Toast.makeText(applicationContext, "Get Uri", Toast.LENGTH_LONG).show()
                                        hashMap3[key] = a.text

                                    }
                                    hashMap2[key] = a.text.toString()

                                }
                                is SeekBar -> {

                                    a = value
                                    a.min = 0
                                    a.max = 100
                                    hashMap2[key] = a.progress.toString()

                                }
                                is RatingBar -> {

                                    a = value
                                    a.min = 0
                                    a.max = 5
                                    hashMap2[key] = a.progress.toString()

                                }
                                is AdView -> {
                                    a = value
                                    hashMap2[key] = a.adUnitId.toString()
                                }

                            }


                        }
                    }

                    hashMap2["formId"] = idForm
                    Toast.makeText(applicationContext, hashMap3.size.toString(), Toast.LENGTH_LONG).show()
                    val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("FileUploads/$idForm").child(FirebaseAuth.getInstance().currentUser?.uid!! + Date().toString())

                    for ((k, v) in hashMap3) {

                        Toast.makeText(applicationContext, v.toString(), Toast.LENGTH_LONG).show()
                        storageReference.putFile(Uri.parse(v.toString())).addOnSuccessListener {
                            if (it.task.isSuccessful) {

                                storageReference.downloadUrl.addOnSuccessListener {
                                    val du = it.toString()


                                    Toast.makeText(applicationContext, "Upload Successful", Toast.LENGTH_LONG).show()
                                    hashMap2[k] = du

                                    FirebaseFirestore.getInstance().collection("FilledForms").document(FirebaseAuth.getInstance().currentUser?.uid!! + idForm).update(k.toString(), du).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(applicationContext, k.toString() + "Successfully Uploaded", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                            } else {

                                progressDialog.dismiss()
                            }


                        }.addOnFailureListener {

                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                        }


                    }
                    FirebaseFirestore.getInstance().collection("FilledForms").document(FirebaseAuth.getInstance().currentUser?.uid!! + idForm).set(hashMap2, SetOptions.merge()).addOnCompleteListener {
                        if (it.isSuccessful) {

                            progressDialog.dismiss()
                            Toast.makeText(applicationContext, "Data store successfully", Toast.LENGTH_LONG).show()
                        }
                    }

                }
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                startActivity(Intent(applicationContext,EditProfileActivity::class.java))
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {


            }
            R.id.nav_about ->{
                startActivity(Intent(applicationContext, AboutActivity::class.java))
            }
            R.id.nav_share -> {


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
        back=0
        super.onStart()
    }
}
