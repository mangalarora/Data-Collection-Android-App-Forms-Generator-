package ga.mangalarora.testerapplication.utilities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ga.mangalarora.testerapplication.base.activitymain.MyForm
import ga.mangalarora.testerapplication.base.activitymain.NewForm
import ga.mangalarora.testerapplication.base.activitymain.SubmitedForm

class SPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    private val myforms = MyForm()
    private val submitedforms = SubmitedForm()
    private val newform = NewForm()

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> myforms
            1 -> submitedforms
            2 -> newform
            else -> myforms
        }
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "My Forms"
            1 -> "Sub Forms"
            2 -> "New Forms"
            else -> "My Forms"

        }

    }


}
