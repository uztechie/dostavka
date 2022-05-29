package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "district")
data class District(
    @PrimaryKey
    val id:Int,
    val name:String,
    val region:Int

)
