package uz.ibroxim.dostavkauz.models

data class UserRecord(
    val audio:String?,
    val postal:Int?,
    val created_at:String?,
    val isPlaying:Boolean = false
)
