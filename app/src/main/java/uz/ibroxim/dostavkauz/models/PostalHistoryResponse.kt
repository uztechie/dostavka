package uz.ibroxim.dostavkauz.models

data class PostalHistoryResponse(
    val message:String?,
    val status:Int?,
    val data:List<PostalHistory>?
)
