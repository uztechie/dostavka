package uz.ibroxim.dostavkauz.models

data class OrderResponse(
    val message:String?,
    val status:Int?,
    val data:List<Order>?
)
