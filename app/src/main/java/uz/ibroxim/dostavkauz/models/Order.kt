package uz.ibroxim.dostavkauz.models

import java.io.Serializable

data class Order(
    val id:Int?,
    val barcode:Long?,
    val sender:Int?,
    val created_at:String?,
    val sender_full_name:String?,
    val sender_address:SenderAddress?,
    val sender_phone:String?
):Serializable
