package uz.ibroxim.dostavkauz.models

import java.io.Serializable

data class PostalHistory(
    val id:Int,
    val barcode:Long?,
    val sender:Int?,
    val receiver:Int?,
    val receiver_full_name:String?,
    val receiver_phone:String?,
    val truck:Int?,
    val passport:String?,
    val size:String?,
    val price:String?,
    val description:String?,
    val items:List<Item>?,
    val status:List<PostalStatus>?,
    val created_at:String?,
    val sender_address:SenderAddress?,
    val receiver_address:ReceiverAddress?


    ):Serializable

data class ReceiverAddress(
    val street:String?,
    val quarters_name:String?,
    val district_name:String?,
    val region_name:String?,
    val quarters_id:Int?,
    val district_id:Int?,
    val region_id:Int?,
):Serializable
