package ga.mangalarora.testerapplication.base.activitymain

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.firestore.FirebaseFirestore
import ga.mangalarora.testerapplication.R


class NewForm : Fragment() {

    private lateinit var ll:LinearLayout
    private var hashMap2 = HashMap<String,Any?>()
    private var hashMap = HashMap<String,Any>()

    private lateinit var listner :Listener
    private lateinit var idForm: EditText
    private var spKey :String? = null
    private var contexts:Context? = null
    private val adRequest = AdRequest.Builder().build()!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        contexts = context
        val view = inflater.inflate(R.layout.fragment_new_form, container, false)
        ll = view.findViewById(R.id.openXml)
        val btn = view.findViewById<View>(R.id.button6) as Button?
        idForm = view.findViewById(R.id.idForm)

        btn?.setOnClickListener {

            Toast.makeText(context,"clicked", Toast.LENGTH_LONG).show()
            FirebaseFirestore.getInstance().collection("Forms").whereEqualTo("formId", idForm.text.toString()).get().addOnCompleteListener {

                if (it.isSuccessful){

                    val data = it.result.documents[0].data
                    hashMap = data as HashMap<String,Any>
                    Toast.makeText(context,hashMap.toString(), Toast.LENGTH_LONG).show()

                    buildForm()

                    Toast.makeText(context,"Found Data", Toast.LENGTH_LONG).show()
                }
                else{

                    Toast.makeText(context,"Didn't Fetched",Toast.LENGTH_LONG).show()
                }
            }

        }




        return view
    }



    @SuppressLint("SetTextI18n")



    private fun buildForm(){


         var i =0
        for ((key,value) in hashMap) {

            if (key != "formId")
            {
                var b :Any? = null
                val rowView = LayoutInflater.from(context).inflate(value.toString().toInt(), null)
                val a = rowView.findViewById<View>(R.id.labelText) as TextView


                when {
                    rowView.findViewById<View>(R.id.fieldText)  is EditText -> b = rowView.findViewById<View>(R.id.fieldText) as EditText
                    rowView.findViewById<View>(R.id.fieldText) is RatingBar -> b = rowView.findViewById<View>(R.id.fieldText) as RatingBar
                    rowView.findViewById<View>(R.id.fieldText) is SeekBar -> b = rowView.findViewById<View>(R.id.fieldText) as SeekBar
                    rowView.findViewById<View>(R.id.fieldText) is AdView -> b = rowView.findViewById<View>(R.id.fieldText) as AdView
                }


                if (b is AdView)
                {
                    b.apply {
                        adUnitId = key
                        adSize = AdSize.BANNER
                        loadAd(adRequest)

                    }

                }


                if(rowView.findViewById<View>(R.id.btnSelect) as Button? != null)
                {
                    val btn = rowView.findViewById<View>(R.id.btnSelect) as Button

                    btn.setOnClickListener {
                        spKey = key
                        if (ContextCompat.checkSelfPermission(this.context!!.applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                              uploadfile()
                        }
                        else{
                            uploadfile()
                            Toast.makeText(context,"Clicked",Toast.LENGTH_LONG).show()
                            val ats = arrayOf("android.Manifest.permission.READ_EXTERNAL_STORAGE")
                            ActivityCompat.requestPermissions(activity as Activity,ats,99)
                        }
                    }
                }

                 hashMap2[key] = b
                if( b !is AdView)
                a.text = "Q.${i+1} $key"
                else
                    a.isVisible = false
                // Add the new row before the add field button.
                // Add the new row before the add field button.
                ll.addView(rowView, i)
                i++
            }
        }
        listner.sens(hashMap,idForm.text.toString(),hashMap2)

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

         Toast.makeText(context,"Reached Here",Toast.LENGTH_LONG).show()
         if (requestCode==99 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
             uploadfile()
         }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



   private fun uploadfile(){
       val intent= Intent()
       //intent.type = "application/octet-stream"
       //intent.type = "text/plain"
       intent.addCategory(Intent.CATEGORY_OPENABLE)
       intent.type = "*/*"
       val mimetypes = arrayOf("image/*", "video/*","application/*","text/*","audio/*")
       intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
       intent.action = Intent.ACTION_GET_CONTENT
       //intent.type = "image/*"
       intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
       startActivityForResult(Intent.createChooser(intent,"Select Picture"), 11)



   }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        try {

            if (requestCode == 11 && resultCode == RESULT_OK && data != null)
            {

                val a = hashMap2[spKey] as EditText
                val bt: Uri? = data.data
                a.setText(bt.toString())
            }

        }
        catch (e:Exception){

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onAttach(context: Context?) {
        try {
            listner = context as Listener
        } catch (e: Exception) {

        }
        super.onAttach(context)
    }


    interface Listener{
        fun sens(hashMap: HashMap<String,Any>,idForm:String?,hashMap2: HashMap<String, Any?>)
    }


}
