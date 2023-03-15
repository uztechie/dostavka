package uz.ibroxim.dostavkauz.fragments.driver

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_accepted_order_details.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.Order
import uz.ibroxim.dostavkauz.models.SearchOrder
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel


@AndroidEntryPoint
class AcceptedOrderDetailsFragment : Fragment(R.layout.fragment_accepted_order_details) {
    private val TAG = "AcceptedOrderDetails"
    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var successFailedDialogForUpdate: SuccessFailedDialog
    private lateinit var successFailedDialogForMap: SuccessFailedDialog
    private lateinit var successFailedDialogForDeleteRequest: SuccessFailedDialog
    private lateinit var successFailedDialogForDelete: SuccessFailedDialog
    var latitude = 0.0
    var longitude = 0.0

    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        arguments?.let {
            order = AcceptedOrderDetailsFragmentArgs.fromBundle(it).order

            accepted_order_details_tv_name.text = order?.sender_full_name
            accepted_order_details_tv_phone.text = order?.sender_phone
            accepted_order_details_tv_address.text = order?.sender_address?.sender_address_name

            val latLon =
                "Lat: ${order?.sender_address?.latitude}  \nLon: ${order?.sender_address?.longitude}"
            accepted_order_details_tv_latlon.text = latLon
        }

        customProgressDialog = CustomProgressDialog(requireContext())


        successFailedDialogForMap = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {

            }

            override fun onActionButton2Click(clickAction: String) {

            }
        })

        successFailedDialogForUpdate = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){
                    findNavController().popBackStack()
                }
            }

            override fun onActionButton2Click(clickAction: String) {

            }

        })

        successFailedDialogForDeleteRequest = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){
                    order?.barcode?.let {
                        viewModel.deleteOrder(it.toString(), SharedPref.token)
                    }
                }
            }

            override fun onActionButton2Click(clickAction: String) {

            }
        })

        successFailedDialogForDelete = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {

            }

            override fun onActionButton2Click(clickAction: String) {

            }
        })






        if (order?.sender_address?.latitude?.isNotEmpty() == true) {
            latitude = order?.sender_address?.latitude!!.toDouble()
        }
        if (order?.sender_address?.longitude?.isNotEmpty() == true) {
            longitude = order?.sender_address?.longitude!!.toDouble()
        }

        drawLocationMark(latitude, longitude)


        accepted_order_details_map.map.move(
            CameraPosition(Point(latitude, longitude), 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )


        accepted_order_details_btn_call.setOnClickListener {
            var phone = order?.sender_phone
            if (phone.isNullOrEmpty()) {
                return@setOnClickListener
            }
            phone = "+$phone"
            Utils.call(requireActivity(), phone)
        }

        accepted_order_details_btn_map.setOnClickListener {
            openMap()
        }

        accepted_order_details_btn_update.setOnClickListener {
            order?.let {
                findNavController().navigate(AcceptedOrderDetailsFragmentDirections.actionAcceptedOrderDetailsFragmentToOrderUpdateFragment(it))
            }
        }

        updateOrderStatus()
        deleteOrder()



    }

    private fun updateOrderStatus() {
        viewModel.updateStatusResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()

                    successFailedDialogForUpdate.show()
                    successFailedDialogForUpdate.setStatusImage(R.drawable.error)
                    successFailedDialogForUpdate.setTitle(getString(R.string.buyurtma_qabul_qilish))
                    successFailedDialogForUpdate.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialogForUpdate.setButton1Text(getString(R.string.yopish))
                    successFailedDialogForUpdate.showCloseButton(false)
                    successFailedDialogForUpdate.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            successFailedDialogForUpdate.show()
                            successFailedDialogForUpdate.setStatusImage(R.drawable.success)
                            successFailedDialogForUpdate.setTitle(getString(R.string.buyurtma_qabul_qilish))
                            successFailedDialogForUpdate.setMessage(getString(R.string.siz_ushbu_buyurtmani_qabul_qildingiz))
                            successFailedDialogForUpdate.setButton1Text(getString(R.string.yopish))
                            successFailedDialogForUpdate.showCloseButton(false)
                            successFailedDialogForUpdate.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
                        }
                        else {
                            successFailedDialogForUpdate.show()
                            successFailedDialogForUpdate.setStatusImage(R.drawable.error)
                            successFailedDialogForUpdate.setTitle(getString(R.string.buyurtma_qabul_qilish))
                            successFailedDialogForUpdate.setMessage(it.message?:getString(R.string.xatolik))
                            successFailedDialogForUpdate.setButton1Text(getString(R.string.yopish))
                            successFailedDialogForUpdate.showCloseButton(false)
                            successFailedDialogForUpdate.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }
                    }
                }
            }
        }
    }

    private fun searchOrder() {
        viewModel.searchByBarcodeResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()

                    successFailedDialogForUpdate.show()
                    successFailedDialogForUpdate.setStatusImage(R.drawable.error)
                    successFailedDialogForUpdate.setTitle(getString(R.string.buyurtma_izlash))
                    successFailedDialogForUpdate.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialogForUpdate.setButton1Text(getString(R.string.yopish))
                    successFailedDialogForUpdate.showCloseButton(false)
                    successFailedDialogForUpdate.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            it.data?.let { items->
                                if (items.isNotEmpty()){
                                    updateOrder(items[0])
                                }
                            }


                        }
                        else {
                            successFailedDialogForUpdate.show()
                            successFailedDialogForUpdate.setStatusImage(R.drawable.error)
                            successFailedDialogForUpdate.setTitle(getString(R.string.buyurtma_izlash))
                            successFailedDialogForUpdate.setMessage(it.message?:getString(R.string.xatolik))
                            successFailedDialogForUpdate.setButton1Text(getString(R.string.yopish))
                            successFailedDialogForUpdate.showCloseButton(false)
                            successFailedDialogForUpdate.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }
                    }
                }
            }
        }
    }


    private fun updateOrder(data: SearchOrder) {
        val map = HashMap<String, RequestBody>()
        map["postal_id"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["receiver_id"] = data.barcode.toString().toRequestBody(MultipartBody.FORM)
        map["quarters_id"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["driver_id"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["passport"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["size"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["price"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["description"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["sender_address_name"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["sender_longitude"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["sender_latitude"] = data.id.toString().toRequestBody(MultipartBody.FORM)
        map["street"] = data.id.toString().toRequestBody(MultipartBody.FORM)
    }

    private fun deleteOrder() {
        viewModel.deleteOrderResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()

                    successFailedDialogForDelete.show()
                    successFailedDialogForDelete.setStatusImage(R.drawable.error)
                    successFailedDialogForDelete.setTitle(getString(R.string.buyurtmani_ochirish))
                    successFailedDialogForDelete.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialogForDelete.setButton1Text(getString(R.string.yopish))
                    successFailedDialogForDelete.showCloseButton(false)
                    successFailedDialogForDelete.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            Utils.toastIconSuccess(requireActivity(), getString(R.string.buyurtma_ochirildi))
                            findNavController().popBackStack()
                        }
                        else {
                            successFailedDialogForDelete.show()
                            successFailedDialogForDelete.setStatusImage(R.drawable.error)
                            successFailedDialogForDelete.setTitle(getString(R.string.buyurtmani_ochirish))
                            successFailedDialogForDelete.setMessage(it.message?:getString(R.string.xatolik))
                            successFailedDialogForDelete.setButton1Text(getString(R.string.yopish))
                            successFailedDialogForDelete.showCloseButton(false)
                            successFailedDialogForDelete.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }
                    }
                }
            }
        }
    }

    private fun drawLocationMark(lat: Double, lon: Double) {
        val view = View(requireContext()).apply {
            background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_location_red)
        }

        accepted_order_details_map.map.mapObjects.addPlacemark(
            Point(lat, lon),
            ViewProvider(view)
        )
    }


    private fun openMap() {
        val userLat = SharedPref.latitude
        val userLon = SharedPref.longitude

        var uri: Uri = Uri.EMPTY
        if (requireContext().isPackageInstalled("ru.yandex.yandexmaps")) {
            try {
                uri = Uri.parse("yandexmaps://maps.yandex.ru/?rtext=$userLat,$userLon~$latitude,$longitude&rtt=mt")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                requireActivity().startActivity(intent)
            }catch (e:Exception){
                e.printStackTrace()
            }

        } else {
            successFailedDialogForUpdate.show()
            successFailedDialogForUpdate.setStatusImage(R.drawable.error)
            successFailedDialogForUpdate.setTitle(getString(R.string.yandexmaps_topildmadi))
            successFailedDialogForUpdate.setMessage(getString(R.string.xarita_orqali_korish))
            successFailedDialogForUpdate.setButton1Text(getString(R.string.yopish))
            successFailedDialogForUpdate.showCloseButton(false)
            successFailedDialogForUpdate.setClickAction(SuccessFailedDialog.ACTION_FAILED)
            Log.d(TAG, "openMap: map is not installed ")
        }
    }


    fun Context.isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "isPackageInstalled: error e "+e)
            false
        }catch (e:Exception){
            Log.d(TAG, "isPackageInstalled: error "+e)
            false
        }
    }


    private fun initToolbar() {
        toolbar_btn_back.visibility = View.VISIBLE
        toolbar_title.text = getString(R.string.buyurtma_malumotlari)

        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        accepted_order_details_map.onStart()
        MapKitFactory.getInstance().onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        accepted_order_details_map.onStop()
        MapKitFactory.getInstance().onStop()
        Log.d(TAG, "onStop: ")
    }


    companion object{
        const val ACTION_DELETE = "ACTION_DELETE"
        const val ACTION_UPDATE = "ACTION_DELETE"
    }

}