package uz.ibroxim.dostavkauz.models

data class PostalResponse(
    val message:String?,
    val status:Int?,
    val data:PostalBarcode?,
)

data class PostalBarcode(
    val barcode:String?
)
