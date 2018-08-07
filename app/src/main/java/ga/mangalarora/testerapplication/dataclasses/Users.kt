package ga.mangalarora.testerapplication.dataclasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Users(var username: String = "",var dob: String = "", var email: String = "", var phone: String = "", var userId: String = "", var pass: String = "", var photourl: String = "", var gender: String = "", var website: String = "", var displayname: String = "", var thumbPhoto: String = "", var dateofreg: String = "", var time: String = "", var countrylocale: String = "", var countrytelephone: String = "", var ipadd: String = "", var deviceinfo: String = "", var macadd: String = "") : Parcelable