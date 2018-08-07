package ga.mangalarora.testerapplication.utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ga.mangalarora.testerapplication.R

class UpdatePhone : AppCompatActivity() {

    companion object {
        var True:Boolean = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_phone)
    }
}
