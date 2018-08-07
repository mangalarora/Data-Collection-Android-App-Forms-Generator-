package ga.mangalarora.testerapplication.utilities


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import ga.mangalarora.testerapplication.R
import kotlinx.android.synthetic.main.dialog_layout2.view.*


class DialogClassAdd : AppCompatDialogFragment(){
    private var listener: Listener? = null
    private var iD:Int? = null
    private var widget:String? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        val views = LayoutInflater.from(context).inflate(R.layout.dialog_layout2,null)
        val staticAdapter: ArrayAdapter<CharSequence> = ArrayAdapter
                .createFromResource(context, R.array.widgets_data,
                        android.R.layout.simple_spinner_item)
         views.group.isVisible = false
        views.group2.isVisible = false

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        views.spinner3.adapter = staticAdapter

        views.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        position: Int, id: Long) {
               widget = views.spinner3.getItemAtPosition(position).toString()
               when(widget){
                    "Plain Text"->
                    {views.editText15.isVisible = true
                        views.group.isVisible=false
                        views.group2.isVisible=false
                        iD =R.layout.widget_text_input}
                     "Password" ->
                     {views.editText15.isVisible = true
                         views.group.isVisible=false
                         views.group2.isVisible=false
                         iD =R.layout.widget_password}
                   "Phone" ->
                   {views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false
                       iD =R.layout.widget_phone}
                   "Number"->
                   {views.editText15.isVisible = true
                       views.group.isVisible=false
                       views.group2.isVisible=false
                       iD =R.layout.widget_number}

                   "Website"->{views.editText15.isVisible = false
                        iD =R.layout.widget_website
                       views.group.isVisible=false
                       views.group2.isVisible=false}
                   "Date"->{
                       views.editText15.isVisible = false
                           iD =R.layout.widget_date
                       views.group2.isVisible=true
                       views.group.isVisible=false
                   }
                   "Time"->

                   {
                       views.editText15.isVisible = false
                       iD =R.layout.widget_time
                       views.group2.isVisible=true
                       views.group.isVisible=false

                   }
                   "Rating Star Bar"->{
                       iD = R.layout.widget_rating_bar
                       views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false

                   }
                   "Seek Bar"->{
                       iD = R.layout.widget_seek_bar
                       views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false
                   }
                   "Apply Ads"->{
                       iD = R.layout.widget_ads
                       views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false
                   }
                   "E-mail"->{
                       iD = R.layout.widget_email
                       views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false
                   }
                   "Radio Button Group"->
                   {views.editText15.isVisible = true}

                  "Image Picker"->{
                       iD = R.layout.widget_upload_file
                       views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false

                    }
                    "Video Picker"->{
                        iD = R.layout.widget_upload_file
                        views.editText15.isVisible = false
                        views.group.isVisible=false
                        views.group2.isVisible=false
                    }
                   "Audio Picker"->{
                       iD = R.layout.widget_upload_file
                       views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false
                   }

                   "Put An Image"->
                   {views.group.isVisible =true
                       views.editText15.isVisible = false
                       views.group2.isVisible=false}
                   "Put a Video"->
                   {views.group.isVisible =true
                       views.editText15.isVisible = false
                       views.group2.isVisible=false}
                   "Put an Audio"->
                   {views.group.isVisible =true
                       views.editText15.isVisible = false
                       views.group2.isVisible=false
                   }
                   "No Input"->{
                       iD = null
                       views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false

                   }
                   "Google Map View Picker"->{
                       iD = R.layout.widget_map_view
                       views.editText15.isVisible = false
                       views.group.isVisible=false
                       views.group2.isVisible=false


                   }
                   "Paragraph"->{
                       iD = R.layout.widget_paragraph
                       views.editText15.isVisible = true
                       views.group.isVisible=false
                       views.group2.isVisible=false

                   }
                   else ->
                   {views.editText15.isVisible = false
                    views.group.isVisible=false
                     views.group2.isVisible=false}
                }

            }

        }



        builder.setView(views)
                .setTitle("Add New Question or Plain Text")
                .setNegativeButton("cancel") { p0, p1 ->
                    p0.cancel()
                }
                .setPositiveButton("Add"){p0,p1->

                    this.listener!!.applyTexts(iD,widget.toString(),views.editText14.text.toString())


                }

        return builder.create()
    }

    override fun onAttach(context: Context?) {
        try {
            listener = context as Listener
        } catch (e: Exception) {

        }
        super.onAttach(context)
    }



    interface Listener{
        fun applyTexts(id:Int?,type:String,labelname:String?)

    }





}
