package ga.mangalarora.testerapplication.start.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import ga.mangalarora.testerapplication.R
import ga.mangalarora.testerapplication.base.activitymain.MainActivity
import ga.mangalarora.testerapplication.loginactivities.LoginActivity
import ga.mangalarora.testerapplication.utilities.PageAdapter
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    private  var currentItem:Int? = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        viewPager.adapter = PageAdapter(this.applicationContext)

        if (FirebaseAuth.getInstance().currentUser!=null)
        {
            startActivity(Intent(applicationContext,MainActivity::class.java))
            finish()
        }
        button3.isVisible = false

        val list = listOf<TextView>(textView4,textView5,textView6,textView7,textView8,textView9)
        for(i in list){
            i.text = Html.fromHtml("&#8226")
            i.textSize = 35F
            i.setTextColor(resources.getColor(R.color.colorAccent))
        }
        textView4.setTextColor(resources.getColor(R.color.colorPrimaryDark))

         button3.setOnClickListener {
             viewPager.currentItem = currentItem!!.minus(1)
         }
         button4.setOnClickListener {
             currentItem = currentItem!!.plus(1)
             viewPager.currentItem = currentItem!!
             if (currentItem == list.size)
             {
                 startActivity(Intent(applicationContext, LoginActivity::class.java))
                 finish()
             }
         }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {




            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {

                for(i in list){
                    i.text = Html.fromHtml("&#8226")
                    i.textSize = 35F
                    i.setTextColor(resources.getColor(R.color.colorAccent))

                }
                currentItem = position
                list[position].setTextColor(resources.getColor(R.color.colorPrimaryDark))

                if (position == list.size - 1){
                   button4.text = "FINISH"

                    btnskip.isVisible = false
                }
                else{
                    button4.text = "NEXT"
                    button3.isVisible = true
                    btnskip.isVisible = true
                }
                if (position == 0){
                    button3.isVisible = false
                }

            }

        })

        btnskip.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

    }

}
