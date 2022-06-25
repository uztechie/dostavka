package uz.ibroxim.dostavkauz.models

data class SearchOrder(
    val id:Int?,
    val barcode:String?,
    val sender_phone:String?,
    val sender_full_name:String?,
    val receiver_full_name:String?,
    val receiver_phone:String?,
    val sender:Int?,
    val receiver:Int?,
    val truck:Int?,
    val size:String?,
    val price:String?,
    val description:String?,
    val status:List<PostalStatus>?,
    val recording:List<UserRecord>?,
    val items:List<Item>?,
    val created_at:String?,
    val updated_at:String?,
    val sender_address:SenderAddress?,
    val receiver_address:ReceiverAddress?,
    val sender_passport:Passport?,
    val receiver_passport:Passport?,

    val payment:List<Payment>?
)

data class Passport(
    val image:String?,
    val serial:String?,
    val number:String?,
    val status:String?
)
