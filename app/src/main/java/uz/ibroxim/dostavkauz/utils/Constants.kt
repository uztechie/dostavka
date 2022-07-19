package uz.ibroxim.dostavkauz.utils

import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.models.Payment

object Constants {
    val BASE_URL_WITH_API = "https://dostavka-ru.uz/api/"
    val BASE_URL = "https://dostavka-ru.uz/"
    const val MY_TOKEN = "pbkdf2_sha256260000YJVKOo3A5OhFWpScvpo4231E12faPw4bjWv0lNMTdPx0UGONrpg8123JaCWyW34SYPG9khok=asda123wdzasdwqeq"
    const val USER_TYPE_DRIVER = 2
    const val USER_TYPE_USER = 3


    var orderId = -1
    var barcode = ""
    var orderItems = mutableListOf<Item>()
    var paymentList = mutableListOf<Payment>()

    var TOPIC_DRIVER = "driver"
    var TOPIC_CUSTOMER = "customer"
    var TOPIC_ALL = "all"

}