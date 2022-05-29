package uz.ibroxim.dostavkauz.models

import java.io.Serializable

data class PostalHistory(
    val id:Int,
    val barcode:Long?,
    val sender:Int?,
    val receiver:Int?,
    val truck:Int?,
    val passport:String?,
    val size:String?,
    val price:String?,
    val description:String?,
    val status:List<PostalStatus>?,
    val created_at:String?,
    val sender_address:SenderAddress?,
    val receiver_address:ReceiverAddress?


    ):Serializable

data class SenderAddress(
    val sender_address_name:String?,
    val latitude:String?,
    val longitude:String?,
):Serializable

data class ReceiverAddress(
    val street:String?,
    val quarters_name:String?,
    val district_name:String?,
    val region_name:String?
):Serializable
