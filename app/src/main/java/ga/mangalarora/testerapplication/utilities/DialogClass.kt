package ga.mangalarora.testerapplication.utilities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import ga.mangalarora.testerapplication.Main3Activity
import ga.mangalarora.testerapplication.R

class DialogClass : AppCompatDialogFragment(){
      private var listener: Listener? = null
      private lateinit var text1 :EditText
      private lateinit var text2 :EditText
      private lateinit var text3 :EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_layout,null)
        text1 = view.findViewById(R.id.editText3)
        text2 = view.findViewById(R.id.editText12)
        text3 = view.findViewById(R.id.editText13)

        builder.setView(view)
               .setTitle("Create New Form")
                .setNegativeButton("cancel") { p0, p1 ->
                   p0.cancel()
                }
                .setPositiveButton("Add"){p0,p1->
                      val a = text1.text.toString()
                      val b  = text2.text.toString()
                      val c = text3.text.toString()
                    //this.listener!!.applyTexts(editText3.text.toString(),editText12.text.toString(),editText13.text.toString())

                    startActivity(Intent(context,Main3Activity::class.java).apply {
                        putStringArrayListExtra("dataE", arrayListOf(a,b,c))
                    })
                }

        return builder.create()
    }

    override fun onAttach(context: Context?) {
        try {
            listener = context as Listener
        } catch (e:ClassCastException) {
             //throw  ClassCastException("""${context.toString()}Must Implement the DialogClass First""")
        }
        super.onAttach(context)
    }

    interface Listener{
        fun applyTexts(name:String?,formId:String?,Purpose:String?)

    }
}
