package uz.techie.mexmash.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    //postal

    suspend fun uploadPostalWithCustomer(
        map:HashMap<String, RequestBody>,
        file: MultipartBody.Part,
        token: String
    ) = retrofitApi.uploadPostalWithCustomer(map, file, token)

    suspend fun uploadPostal(
        map:HashMap<String, RequestBody>,
        file: MultipartBody.Part,
        token: String
    ) = retrofitApi.uploadPostal(map, file, token)

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