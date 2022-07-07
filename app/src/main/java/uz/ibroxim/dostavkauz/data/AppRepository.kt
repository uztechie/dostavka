package uz.techie.mexmash.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.models.User
import uz.techie.airshop.db.AppDatabase
import uz.techie.airshop.network.RetrofitApi
import uz.techie.mexmash.models.District
import uz.techie.mexmash.models.Quarter
import uz.techie.mexmash.models.Region

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val retrofitApi: RetrofitApi,
    private val db: AppDatabase
) {
    private val dao = db.AppDao()

    //regions
    suspend fun loadRegions() = retrofitApi.loadRegions()
    suspend fun loadDistricts() = retrofitApi.loadDistricts()
    suspend fun loadQuarters() = retrofitApi.loadQuarters()


    //login - register
    suspend fun checkPhone(phone:String) = retrofitApi.checkPhone(phone)
    suspend fun loginCustomer(phone:String) = retrofitApi.loginCustomer(phone)
    suspend fun registerCustomer(map:HashMap<String, Any>) = retrofitApi.registerCustomer(map)
    suspend fun updateCustomer(map:HashMap<String, Any>, token:String) = retrofitApi.updateCustomer(map, token)

    suspend fun uploadCustomerPassport(
        map:HashMap<String, RequestBody>,
        file: MultipartBody.Part,
         token: String
    ) = retrofitApi.uploadCustomerPassport(map, file, token)

    //postal

    suspend fun createReceiver(
        map:HashMap<String, RequestBody>,
        file: MultipartBody.Part
    ) = retrofitApi.createReceiver(map, file)

    suspend fun uploadPostal(
        map:HashMap<String, Any>
    ) = retrofitApi.uploadPostal(map)

    suspend fun uploadPostal2(
        senderAddress: RequestBody,
        lon:RequestBody,
        lat:RequestBody,
        desc:RequestBody,
        quarterId:RequestBody,
        street:RequestBody,
        receiverId:RequestBody,
        file: MultipartBody.Part,
        token: String
    ) = retrofitApi.uploadPostal2(senderAddress, lon, lat, desc, quarterId, street, receiverId, file, token)


    //tariff
    suspend fun loadTariff(token: String) = retrofitApi.loadTariff(token)
    suspend fun loadNews(token: String) = retrofitApi.loadNews(token)
    suspend fun loadPostalHistory(token: String) = retrofitApi.loadPostalHistory(token)

    //driver part
    //new orders
    suspend fun loadNewOrders(token: String) = retrofitApi.loadNewOrders(token)
    suspend fun searchByBarcode(barcode:String, token: String) = retrofitApi.searchByBarcode(barcode, token)
    suspend fun updateOrder(map:HashMap<String, RequestBody>, file:MultipartBody.Part, token: String) = retrofitApi.updateOrder(map, file, token)
    suspend fun deleteOrder(barcode: String, token: String) = retrofitApi.deleteOrder(barcode, token)
    suspend fun loadOrdersHistory() = retrofitApi.loadOrdersHistory()


    //status
    suspend fun updateOrderStatus(status:Int, postalId:Int) = retrofitApi.updateOrderStatus(status, postalId)


    //audio
    suspend fun uploadAudio(postalId:Int, file:MultipartBody.Part) = retrofitApi.uploadAudio(postalId, file)

    //items
    suspend fun addItem(map:HashMap<String, Any>) = retrofitApi.addItem(map)
    suspend fun updateItem(map:HashMap<String, Any>) = retrofitApi.updateItem(map)
    suspend fun deleteItem(itemId:Int) = retrofitApi.deleteItem(itemId)

    //payment
    suspend fun createPayment(map:HashMap<String, Any>) = retrofitApi.createPayment(map)

    //privacy
    suspend fun loadPrivacy() = retrofitApi.loadPrivacy()


    //database


    //regisons
    suspend fun insertRegions(list:List<Region>) = dao.insertRegions(list)
    suspend fun insertDistricts(list:List<District>) = dao.insertDistricts(list)
    suspend fun insertQuarters(list:List<Quarter>) = dao.insertQuarters(list)

    fun getRegions() = dao.getRegions()
    fun getDistricts(id:Int) = dao.getDistricts(id)
    fun getQuarters(id:Int) = dao.getQuarters(id)

    //profile
    suspend fun insertUser(user: User) = dao.deleteInsertUser(user)
    suspend fun deleteUser() = dao.deleteUser()
    fun getUserLive() = dao.getUserLive()
    fun getUser() = dao.getUser()


}