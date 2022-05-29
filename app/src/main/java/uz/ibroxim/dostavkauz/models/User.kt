package uz.ibroxim.dostavkauz.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id:Int,
    var phone:String? = null,
    var phone2:String? = null,
    var first_name:String? = null,
    var last_name:String? = null,
    var surname:String? = null,
    var token:String? = null,
    var type:Int? = null,
    var type_name:String? = null
)
