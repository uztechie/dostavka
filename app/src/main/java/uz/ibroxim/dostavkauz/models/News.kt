package uz.ibroxim.dostavkauz.models

data class News(
    val id:Int,
    val created_at:String,
    val updated_at:String,
    val name:String,
    val description:String,
    val image:String

)
