package ga.mangalarora.testerapplication.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import ga.mangalarora.testerapplication.R

class PageAdapter(private val context:Context) : PagerAdapter(){



    //------------------Data Goes Here --------------------//

    private val list = listOf("\uD83D\uDC90 \uD83D\uDC96 \uD83D\uDC96 \uD83D\uDC96 \uD83D\uDC90 \n Saluting Kalam, a man who valued persistence, determination","Create New Form","Select Elements and Create New Questions","Save the Form","Open Form to Fill, with FormId","Great, Earn With your Form By Applying Ads in your form")

    private val list2 = listOf<Int>(R.drawable.a1,R.drawable.a2,R.drawable.a3,R.drawable.a4,R.drawable.a5,R.drawable.a6)
    //----------------------------------------//



    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view == `object` as View

    }

    override fun getCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.pager_slider,container,false)


        val tvLower: ImageView = view.findViewById(R.id.imageView)
        val tvLUpper: TextView = view.findViewById(R.id.textView)
        val tvFooter: TextView = view.findViewById(R.id.textView2)

        // Set the text views text
        tvLower.setImageResource(list2[position])
        tvLUpper.text = list[position].toUpperCase()
        tvFooter.text = "Page ${position+1} of ${list.size}"



        // Add the view to the parent
        container.addView(view)

        // Return the view
        return view
    }
    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        // Remove the view from view group specified position
        parent.removeView(`object` as View)
    }


}