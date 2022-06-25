package uz.ibroxim.dostavkauz.models

data class PaymentResponse(
    val message:String?,
    val status:Int?,
    val data:List<Payment>?
)
