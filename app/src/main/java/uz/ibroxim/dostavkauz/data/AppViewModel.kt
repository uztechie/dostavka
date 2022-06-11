package uz.techie.mexmash.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import uz.ibroxim.dostavkauz.models.*
import uz.ibroxim.dostavkauz.utils.Resource
import uz.techie.mexmash.models.District
import uz.techie.mexmash.models.Quarter
import uz.techie.mexmash.models.Region
import java.io.InterruptedIOException
import java.net.UnknownHostException
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {

    private val TAG = "AppViewModel"

    val regions:MutableLiveData<Resource<List<Region>>> = MutableLiveData()
    val districts:MutableLiveData<Resource<List<District>>> = MutableLiveData()
    val quarters:MutableLiveData<Resource<List<Quarter>>> = MutableLiveData()

    val checkPhoneResponse:MutableLiveData<Resource<Login>> = MutableLiveData()
    val loginCustomerResponse:MutableLiveData<Resource<Login>> = MutableLiveData()
    val registerCustomerResponse:MutableLiveData<Resource<Login>> = MutableLiveData()
    var updateCustomerResponse:MutableLiveData<Resource<Login>> = MutableLiveData()

    val postalResponse:MutableLiveData<Resource<PostalResponse>> = MutableLiveData()

    val tariffResponse:MutableLiveData<Resource<List<Tariff>>> = MutableLiveData()
    val newsResponse:MutableLiveData<Resource<List<News>>> = MutableLiveData()

    val postalHistoryResponse:MutableLiveData<Resource<PostalHistoryResponse>> = MutableLiveData()

    val newOrderResponse:MutableLiveData<Resource<OrderResponse>> = MutableLiveData()
    val searchByBarcodeResponse:MutableLiveData<Resource<SearchOrderResponse>> = MutableLiveData()
    val updateOrderResponse:MutableLiveData<Resource<OrderResponse>> = MutableLiveData()
    val deleteOrderResponse:MutableLiveData<Resource<DeleteOrderResponse>> = MutableLiveData()
    val orderHistoryResponse:MutableLiveData<Resource<OrderResponse>> = MutableLiveData()

    val updateStatusResponse:MutableLiveData<Resource<UpdateStatusResponse>> = MutableLiveData()

    val uploadCustomerPassportResponse:MutableLiveData<Resource<PostalResponse>> = MutableLiveData()





    //regions
    fun loadRegions() = viewModelScope.launch {
        regions.postValue(Resource.Loading())
        try {
            val response = repository.loadRegions()
            regions.postValue(handleregions(response))
        }catch (e: UnknownHostException) {
            regions.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            regions.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            regions.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun loadDistricts() = viewModelScope.launch {
        districts.postValue(Resource.Loading())
        try {
            val response = repository.loadDistricts()
            districts.postValue(handleDistricts(response))
        }catch (e: UnknownHostException) {
            districts.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            districts.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            districts.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun loadQuarters() = viewModelScope.launch {
        quarters.postValue(Resource.Loading())
        try {
            val response = repository.loadQuarters()
            quarters.postValue(handleQuarters(response))
        }catch (e: UnknownHostException) {
            quarters.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            quarters.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            quarters.postValue(Resource.Error(message = e.toString()))
        }
    }


    //login - register

    fun checkPhone(phone:String) = viewModelScope.launch {
        checkPhoneResponse.postValue(Resource.Loading())
        try {
            val response = repository.checkPhone(phone)
            checkPhoneResponse.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            checkPhoneResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            checkPhoneResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            checkPhoneResponse.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun loginCustomer(phone:String) = viewModelScope.launch {
        loginCustomerResponse.postValue(Resource.Loading())
        try {
            val response = repository.loginCustomer(phone)
            loginCustomerResponse.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            loginCustomerResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            loginCustomerResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            loginCustomerResponse.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun registerCustomer(map:HashMap<String, Any>) = viewModelScope.launch {
        registerCustomerResponse.postValue(Resource.Loading())
        try {
            val response = repository.registerCustomer(map)
            registerCustomerResponse.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            registerCustomerResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            registerCustomerResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            registerCustomerResponse.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun updateCustomer(map:HashMap<String, Any>, token:String) = viewModelScope.launch {
        updateCustomerResponse.postValue(Resource.Loading())
        try {
            val response = repository.updateCustomer(map, token)
            updateCustomerResponse.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            updateCustomerResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            updateCustomerResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            updateCustomerResponse.postValue(Resource.Error(message = e.toString()))
        }
    }

    fun uploadCustomerPassport(  map:HashMap<String, RequestBody>, file: MultipartBody.Part) = viewModelScope.launch {
        uploadCustomerPassportResponse.postValue(Resource.Loading())
        try {
            val response = repository.uploadCustomerPassport(map, file)
            uploadCustomerPassportResponse.postValue(handleUploadCustomerPassport(response))
        }catch (e: UnknownHostException) {
            uploadCustomerPassportResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            uploadCustomerPassportResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            uploadCustomerPassportResponse.postValue(Resource.Error(message = e.toString()))
        }
    }


    //postal

    fun uploadPostalWithCustomer(
        map:HashMap<String, RequestBody>,
        file: MultipartBody.Part,
        token: String
    ) = viewModelScope.launch {
        postalResponse.postValue(Resource.Loading())
        try {
            val response = repository.uploadPostalWithCustomer(map, file, token)
            postalResponse.postValue(handlePostalResponse(response))
        }catch (e: UnknownHostException) {
            postalResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            postalResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            postalResponse.postValue(Resource.Error(message = e.toString()))
        }
    }

    fun uploadPostal(
        map:HashMap<String, RequestBody>,
        file: MultipartBody.Part,
        token: String
    ) = viewModelScope.launch {
        postalResponse.postValue(Resource.Loading())
        try {
            val response = repository.uploadPostal(map, file, token)
            postalResponse.postValue(handlePostalResponse(response))
        }catch (e: UnknownHostException) {
            postalResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            postalResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            postalResponse.postValue(Resource.Error(message = e.toString()))
        }
    }


    //tariff
    fun loadTariff(token: String) = viewModelScope.launch {
        tariffResponse.postValue(Resource.Loading())
        try {
            val response = repository.loadTariff(token)
            tariffResponse.postValue(handleTariffResponse(response))
        }catch (e: UnknownHostException) {
            tariffResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            tariffResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            tariffResponse.postValue(Resource.Error(message = e.toString()))
        }
    }

    //news
    fun loadNews(token: String) = viewModelScope.launch {
        newsResponse.postValue(Resource.Loading())
        try {
            val response = repository.loadNews(token)
            newsResponse.postValue(handleNewsResponse(response))
        }catch (e: UnknownHostException) {
            newsResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            newsResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            newsResponse.postValue(Resource.Error(message = e.toString()))
        }
    }

    //postal history
    fun loadPostalHistory(token: String) = viewModelScope.launch {
        postalHistoryResponse.postValue(Resource.Loading())
        try {
            val response = repository.loadPostalHistory(token)
            postalHistoryResponse.postValue(handlePostalHistoryResponse(response))
        }catch (e: UnknownHostException) {
            postalHistoryResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            postalHistoryResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            postalHistoryResponse.postValue(Resource.Error(message = e.toString()))
        }
    }


    //driver part
    //new order

    fun loadNewOrders(token: String) = viewModelScope.launch {
        newOrderResponse.postValue(Resource.Loading())
        try {
            val response = repository.loadNewOrders(token)
            newOrderResponse.postValue(handleNewOrderResponse(response))
        }catch (e: UnknownHostException) {
            newOrderResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            newOrderResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            newOrderResponse.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun searchByBarcode(barcode:String, token: String) = viewModelScope.launch {
        searchByBarcodeResponse.postValue(Resource.Loading())
        try {
            val response = repository.searchByBarcode(barcode, token)
            searchByBarcodeResponse.postValue(handleSearchOrderResponse(response))
        }catch (e: UnknownHostException) {
            searchByBarcodeResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            searchByBarcodeResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            searchByBarcodeResponse.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun updateOrder(map:HashMap<String, RequestBody>, file:MultipartBody.Part, token: String) = viewModelScope.launch {
        updateOrderResponse.postValue(Resource.Loading())
        try {
            val response = repository.updateOrder(map, file, token)
            updateOrderResponse.postValue(handleNewOrderResponse(response))
        }catch (e: UnknownHostException) {
            updateOrderResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            updateOrderResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            updateOrderResponse.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun deleteOrder(barcode: String, token: String) = viewModelScope.launch {
        deleteOrderResponse.postValue(Resource.Loading())
        try {
            val response = repository.deleteOrder(barcode, token)
            deleteOrderResponse.postValue(handleDeleteOrderResponse(response))
        }catch (e: UnknownHostException) {
            deleteOrderResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            deleteOrderResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            deleteOrderResponse.postValue(Resource.Error(message = e.toString()))
        }
    }

    fun loadOrderHistory() = viewModelScope.launch {
        orderHistoryResponse.postValue(Resource.Loading())
        try {
            val response = repository.loadOrdersHistory()
            orderHistoryResponse.postValue(handleNewOrderResponse(response))
        }catch (e: UnknownHostException) {
            orderHistoryResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            orderHistoryResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            orderHistoryResponse.postValue(Resource.Error(message = e.toString()))
        }
    }

    //status

    fun updateOrderStatus(status:Int, postalId:Int) = viewModelScope.launch {
        updateStatusResponse.postValue(Resource.Loading())
        try {
            val response = repository.updateOrderStatus(status, postalId)
            updateStatusResponse.postValue(handleUpdateStatusResponse(response))
        }catch (e: UnknownHostException) {
            updateStatusResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            updateStatusResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            updateStatusResponse.postValue(Resource.Error(message = e.toString()))
        }
    }








    //handlers

    private fun handleregions(response:Response<List<Region>>):Resource<List<Region>>{
        Log.d(TAG, "handleregions: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleDistricts(response:Response<List<District>>):Resource<List<District>>{
        Log.d(TAG, "handleDistricts: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleQuarters(response:Response<List<Quarter>>):Resource<List<Quarter>>{
        Log.d(TAG, "handleQuarters: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleLogin(response:Response<Login>):Resource<Login>{
        Log.d(TAG, "handleLogin: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleUploadCustomerPassport(response:Response<PostalResponse>):Resource<PostalResponse>{
        Log.d(TAG, "handleUploadCustomerPassport: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePostalResponse(response:Response<PostalResponse>):Resource<PostalResponse>{
        Log.d(TAG, "handlePostalResponse: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleTariffResponse(response:Response<List<Tariff>>):Resource<List<Tariff>>{
        Log.d(TAG, "handleTariffResponse: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleNewsResponse(response:Response<List<News>>):Resource<List<News>>{
        Log.d(TAG, "handleNewsResponse: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePostalHistoryResponse(response:Response<PostalHistoryResponse>):Resource<PostalHistoryResponse>{
        Log.d(TAG, "handlePostalHistoryResponse: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }


    private fun handleNewOrderResponse(response:Response<OrderResponse>):Resource<OrderResponse>{
        Log.d(TAG, "handleNewOrderResponse: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
    private fun handleDeleteOrderResponse(response:Response<DeleteOrderResponse>):Resource<DeleteOrderResponse>{
        Log.d(TAG, "handleDeleteOrderResponse: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchOrderResponse(response:Response<SearchOrderResponse>):Resource<SearchOrderResponse>{
        Log.d(TAG, "handleSearchOrderResponse: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleUpdateStatusResponse(response:Response<UpdateStatusResponse>):Resource<UpdateStatusResponse>{
        Log.d(TAG, "handleUpdateStatusResponse: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }




    //database

    fun insertRegions(list:List<Region>) = viewModelScope.launch {
        repository.insertRegions(list)
    }
    fun insertDistricts(list:List<District>) = viewModelScope.launch {
        repository.insertDistricts(list)
    }
    fun insertQuarters(list:List<Quarter>) = viewModelScope.launch {
        repository.insertQuarters(list)
    }
    fun getRegions() = repository.getRegions()
    fun getDistricts(id:Int) = repository.getDistricts(id)
    fun getQuarters(id:Int) = repository.getQuarters(id)


    //profile
    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }
    fun deleteUser() = viewModelScope.launch {
        repository.deleteUser()
    }
    fun getUserLive() = repository.getUserLive()
    fun getUser() = repository.getUser()






}