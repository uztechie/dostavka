package uz.ibroxim.dostavkauz.models

import java.io.Serializable

data class Payment(
    val id:Int?,
    val type_name:String?,
    val type:Int?,
    val amount:String?,
    val created_at:String?

):Serializable
