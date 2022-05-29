package uz.ibroxim.dostavkauz.utils

import com.chibatching.kotpref.KotprefModel

object SharedPref:KotprefModel() {

    val userLoggedIn by booleanPref(false)
    var token by stringPref("")

    var latitude by stringPref("0.0")
    var longitude by stringPref("0.0")


    var customer_latitude by stringPref("0.0")
    var customer_longitude by stringPref("0.0")
    var customer_country by stringPref("")
    var customer_city by stringPref("")
    var customer_state by stringPref("")
    var customer_knownAreaName by stringPref("")

    var receiver_regionId by intPref(-1)
    var receiver_districtId by intPref(-1)
    var receiver_quarterId by intPref(-1)


    var receiver_region by stringPref("")
    var receiver_district by stringPref("")
    var receiver_quarter by stringPref("")
    var receiver_address by stringPref("")
    var receiver_mailTitle by stringPref("")

    var receiver_id by intPref(-1)
    var receiver_name by stringPref("")
    var receiver_lastname by stringPref("")
    var receiver_middlename by stringPref("")
    var receiver_phone1 by stringPref("")
    var receiver_phone2 by stringPref("")
    var receiver_note by stringPref("")
    var receiver_type by intPref(1)




    fun resetCustomerData(){
        customer_latitude = "0.0"
        customer_longitude = "0.0"
        customer_country = ""
        customer_city = ""
        customer_state = ""
        customer_knownAreaName = ""


        receiver_regionId = -1
        receiver_districtId = -1
        receiver_quarterId = -1

        receiver_region = ""
        receiver_district = ""
        receiver_quarter = ""
        receiver_address = ""
        receiver_mailTitle = ""

        receiver_id = -1
        receiver_name = ""
        receiver_lastname = ""
        receiver_middlename = ""
        receiver_phone1 = ""
        receiver_phone2 = ""
        receiver_note = ""
        receiver_type = 1
    }


}