package uz.ibroxim.dostavkauz.models

import java.io.Serializable

data class SenderAddress(
    val sender_address_name:String?,
    val latitude:String?,
    val longitude:String?,
): Serializable
