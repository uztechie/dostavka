package uz.ibroxim.dostavkauz.fragments.driver

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_order_update_payment.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.DriverItemAdapter
import uz.ibroxim.dostavkauz.adapter.PaymentAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class OrderUpdatePaymentFragment:Fragment(R.layout.fragment_order_update_payment) {
    private val TAG = "OrderUpdatePaymentFra"

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var paymentAdapter: PaymentAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProgressDialog = CustomProgressDialog(requireContext())

        paymentAdapter = PaymentAdapter()

        order_payment_recyclerview?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentAdapter
        }

        paymentAdapter.differ.submitList(Constants.paymentList)

        order_payment_btn_save.setOnClickListener {
            var type = 1
            val amount = order_payment_et_amount.text.toString().trim()
            val desc = order_payment_et_desc.text.toString().trim()

            if (amount.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.to_lov_miqdorini_kiriting))
                return@setOnClickListener
            }

            if (order_payment_radio_cash.isChecked){
                type = 1
            }
            else if (order_payment_radio_card.isChecked){
                type = 2
            }

            val map = HashMap<String, Any>()
            map["postal"] = Constants.orderId
            map["type"] = type
            map["amount"] = amount
            map["description"] = desc

            Log.d(TAG, "onViewCreated: mappppp "+map)
            viewModel.createPayment(map)
        }

        createPaymentResponse()
        searchByBarcode()
        searchByBarcodeResponse()
    }



    private fun searchByBarcode(){
        Log.d(TAG, "searchByBarcode: barcode "+Constants.barcode)
        viewModel.searchByBarcode(Constants.barcode, SharedPref.token)
    }
    private fun searchByBarcodeResponse(){
        viewModel.searchByBarcodeResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Utils.toastIconError(requireActivity(), response.message)
                    Log.e(TAG, "searchByBarcode: Error "+response.message)
                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            if (!it.data.isNullOrEmpty()){
                                val data = it.data[0]
                                Log.d(TAG, "searchByBarcode:  data  "+data)
                                data.payment?.let {
                                    paymentAdapter.differ.submitList(it)
                                }
                            }
                        }
                        else{
                            Log.e(TAG, "searchByBarcode: Error "+it.message)
                            Utils.toastIconError(requireActivity(), it.message)
                        }
                    }
                }
            }

        }
    }


    private fun createPaymentResponse(){
        viewModel.createPaymentResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: postalResponse error "+response.message)
                    val title = getString(R.string.tolov_qilish)
                    showSuccessFailedDialog(title, response.message?:"", false)
                }
                is Resource.Success->{
                    Log.d(TAG, "onViewCreated: postalResponse "+response.data)
                    customProgressDialog.dismiss()
                    response.data?.let { itemResponse->
                        if (itemResponse.status == 200) {

                            itemResponse.data?.let {
                                paymentAdapter.differ.submitList(it)
                                val title = getString(R.string.tolov_qilish)
                                order_payment_et_amount.setText("")
                                order_payment_et_desc.setText("")

                                showSuccessFailedDialog(title, getString(R.string.tolov_qabul_qilindi), true)
                            }

                        }
                        else{
                            val title = getString(R.string.tolov_qilish)
                            showSuccessFailedDialog(title, itemResponse.message?:"", false)
                        }
                    }
                }
            }
        }
    }


    private fun showSuccessFailedDialog(title:String, message:String, isSuccess:Boolean){


        val successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {


            }

        })

        if (isSuccess){
            successFailedDialog.show()
            successFailedDialog.setStatusImage(R.drawable.success)
            successFailedDialog.setTitle(title)
            successFailedDialog.setMessage(message)
            successFailedDialog.setButtonText(getString(R.string.yopish))
            successFailedDialog.showCloseButton(false)
            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
        }
        else {
            successFailedDialog.show()
            successFailedDialog.setStatusImage(R.drawable.error)
            successFailedDialog.setTitle(title)
            successFailedDialog.setMessage(message)
            successFailedDialog.setButtonText(getString(R.string.yopish ))
            successFailedDialog.showCloseButton(true)
            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
        }
    }

}