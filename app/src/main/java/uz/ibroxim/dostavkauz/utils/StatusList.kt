package uz.ibroxim.dostavkauz.utils

enum class StatusList(
    val id:Int,
    val title:String
){
    Accepted(1, "Қабул қилинди"),
    PaymentConfirm(2, "Тўлов тасдиқланди")
}
