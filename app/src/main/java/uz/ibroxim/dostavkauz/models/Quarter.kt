package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quarter")
data class Quarter(

    @PrimaryKey
    val id:Int,
    val name:String,
    val district:Int

)
