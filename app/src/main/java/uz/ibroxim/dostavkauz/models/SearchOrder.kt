package uz.ibroxim.dostavkauz.models

data class SearchOrder(
    val id:Int?,
    val barcode:String?,
    val sender_phone:String?,
    val sender:Int?,
    val receiver:Int?,
    val truck:Int?,
    val passport:String?,
    val size:String?,
    val price:String?,
    val description:String?,
    val status:List<PostalStatus>?,
    val recording:List<String>?,
    val created_at:String?,
    val updated_at:String?,
    val sender_address:SenderAddress?,
    val receiver_address:ReceiverAddress?
)
