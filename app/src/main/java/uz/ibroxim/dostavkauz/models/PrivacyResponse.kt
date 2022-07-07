package uz.ibroxim.dostavkauz.models

data class PrivacyResponse(
    val status:Int?,
    val data:Privacy?
)

data class Privacy(
    val name:String?,
    val description:String?
)
