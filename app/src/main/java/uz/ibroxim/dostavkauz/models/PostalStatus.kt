package uz.ibroxim.dostavkauz.models

import java.io.Serializable

data class PostalStatus(
    val id:Int,
    val name:String,
    val created_at:String,
    val status:Int,

): Serializable
