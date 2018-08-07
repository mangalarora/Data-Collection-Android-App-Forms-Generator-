package ga.mangalarora.testerapplication.start.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ga.mangalarora.testerapplication.R
import ga.mangalarora.testerapplication.base.activitymain.MainActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        } else {
            Handler().postDelayed({
                val i = Intent(this, StartActivity::class.java)
                startActivity(i)
                finish()
            }, 5000)
        }
    }
}
