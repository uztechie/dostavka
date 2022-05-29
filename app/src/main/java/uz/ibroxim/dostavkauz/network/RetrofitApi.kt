package uz.techie.airshop.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.ibroxim.dostavkauz.models.*
import uz.ibroxim.dostavkauz.utils.Constants
import uz.techie.mexmash.models.District
import uz.techie.mexmash.models.Quarter
import uz.techie.mexmash.models.Region


interface RetrofitApi {

    //regions
    @GET("regions/")
    suspend fun loadRegions(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<List<Region>>

    @GET("districts/")
    suspend fun loadDistricts(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<List<District>>

    @GET("quarters/")
    suspend fun loadQuarters(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<List<Quarter>>



    //login - register

    @POST("check-phone/")
    suspend fun checkPhone(
        @Field("phone") phone:String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<Login>


    @FormUrlEncoded
    @POST("customer-login/")
    suspend fun loginCustomer(
        @Field("phone") phone:String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<Login>

    @FormUrlEncoded
    @POST("customer-create/")
    suspend fun registerCustomer(
        @FieldMap map:HashMap<String, Any>,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<Login>

    @FormUrlEncoded
    @POST("customer-edit-profile/")
    suspend fun updateCustomer(
        @FieldMap map:HashMap<String, Any>,
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<Login>



    //postal

    @Multipart
    @POST("postal-customer-create/")
    suspend fun uploadPostalWithCustomer(
        @PartMap map:HashMap<String, RequestBody>,
        @Part file:MultipartBody.Part,
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<PostalResponse>

    @Multipart
    @POST("postal-create/")
    suspend fun uploadPostal(
        @PartMap map:HashMap<String, RequestBody>,
        @Part file:MultipartBody.Part,
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<PostalResponse>

    @Multipart
    @POST("postal-create/")
    suspend fun uploadPostal2(
        @Part("sender_address_name") senderAddress:RequestBody,
        @Part("sender_longitude") lon:RequestBody,
        @Part("sender_latitude") lat:RequestBody,
        @Part("description") desc:RequestBody,
        @Part("quarters_id") quarterId:RequestBody,
        @Part("street") street:RequestBody,
        @Part("receiver_id") receiverId:RequestBody,

        @Part file:MultipartBody.Part,
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<PostalResponse>





    //tarif
    @GET("tariff-list/")
    suspend fun loadTariff(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<List<Tariff>>


    //news
    @GET("news-list/")
    suspend fun loadNews(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<List<News>>


    //postal history
    @GET("postal-history/")
    suspend fun loadPostalHistory(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<PostalHistoryResponse>

}