package uz.ibroxim.dostavkauz.models

data class SearchOrderResponse(
    val message: String?,
    val status: Int?,
    val data: List<SearchOrder>?,
)