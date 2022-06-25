package uz.ibroxim.dostavkauz.models

import java.io.Serializable

data class Item(
    var id:Int?,
    var name:String,
    var amount:String
):Serializable
