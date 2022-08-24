package uz.techie.airshop.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import uz.ibroxim.dostavkauz.models.*
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.SharedPref
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

    //passport
    @Multipart
    @POST("customer-edit-or-create-passport-data/")
    suspend fun uploadCustomerPassport(
        @PartMap map:HashMap<String, RequestBody>,
        @Part file:MultipartBody.Part,
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<PostalResponse>



    //postal

    @Multipart
    @POST("customer-and-passport-create/")
    suspend fun createReceiver(
        @PartMap map:HashMap<String, RequestBody>,
        @Part file:MultipartBody.Part,
        @Header("Authorization") token: String = SharedPref.token,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<Login>


    @POST("postal-create/")
    suspend fun uploadPostal(
        @Body map:HashMap<String, Any>,
        @Header("Authorization") token: String = SharedPref.token,
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



    //driver part

    //new order

    @GET("driver-new-postal-list/")
    suspend fun loadNewOrders(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<OrderResponse>

    @FormUrlEncoded
    @POST("driver-search-barcode-id/")
    suspend fun searchByBarcode(
        @Field("barcode_id") barCode:String,
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<SearchOrderResponse>


    @Multipart
    @POST("driver-edit-postal/")
    suspend fun updateOrder(
        @PartMap map:HashMap<String, RequestBody>,
        @Part file:MultipartBody.Part,
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<OrderResponse>

    @FormUrlEncoded
    @POST("driver-delete-postal/")
    suspend fun deleteOrder(
       @Field("barcode_id") barCode:String,
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<DeleteOrderResponse>




    @GET("driver-postal-hisotry/")
    suspend fun loadOrdersHistory(
        @Header("Authorization") token: String = SharedPref.token,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<OrderResponse>


    //status
    @FormUrlEncoded
    @POST("driver-postal-add-status/")
    suspend fun updateOrderStatus(
        @Field("status") status:Int,
        @Field("postal") postal:Int,
        @Header("Authorization") token: String = SharedPref.token,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<UpdateStatusResponse>


    //audio part
    @Multipart
    @POST("driver-audio-create/")
    suspend fun uploadAudio(
        @Part("postal") postal:Int,
        @Part file:MultipartBody.Part,
        @Header("Authorization") token: String = SharedPref.token,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<PostalResponse>


    //items
    @FormUrlEncoded
    @POST("postal-add-items/")
    suspend fun addItem(
        @FieldMap map:HashMap<String, Any>,
        @Header("Authorization") token: String = SharedPref.token,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<ItemResponse>

    @FormUrlEncoded
    @POST("postal-update-items/")
    suspend fun updateItem(
        @FieldMap map:HashMap<String, Any>,
        @Header("Authorization") token: String = SharedPref.token,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<ItemResponse>

    @FormUrlEncoded
    @POST("postal-delete-items/")
    suspend fun deleteItem(
        @Field("item_id") itemId:Int,
        @Header("Authorization") token: String = SharedPref.token,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<ItemResponse>


    //payment
    @FormUrlEncoded
    @POST("driver-payment-create/")
    suspend fun createPayment(
        @FieldMap map:HashMap<String, Any>,
        @Header("Authorization") token: String = SharedPref.token,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<PaymentResponse>


    //privacy
    @GET("condition/")
    suspend fun loadPrivacy():Response<PrivacyResponse>


    //contact
    @GET("contact/")
    suspend fun loadContact():Response<PrivacyResponse>


}