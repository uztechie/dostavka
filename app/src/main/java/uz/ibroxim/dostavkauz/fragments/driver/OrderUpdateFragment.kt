package uz.ibroxim.dostavkauz.fragments.driver

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_order_update.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.OrderUpdateViewPager
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class OrderUpdateFragment:Fragment(R.layout.fragment_order_update) {
    var barcode = ""
    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var successFailedDialog: SuccessFailedDialog

    private val TAG = "OrderUpdateFragment"


    private lateinit var orderUpdatePagerAdapter:OrderUpdateViewPager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            barcode = OrderUpdateFragmentArgs.fromBundle(it).barcode
        }
        Log.d(TAG, "onViewCreated: barcode  "+barcode)

        viewModel.searchByBarcode(barcode, SharedPref.token)

        customProgressDialog = CustomProgressDialog(requireContext())
        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {

            }

        })

        searchByBarcode()

        orderUpdatePagerAdapter = OrderUpdateViewPager(childFragmentManager, viewLifecycleOwner.lifecycle)
        order_update_viewpager.adapter = orderUpdatePagerAdapter

        TabLayoutMediator(order_update_tablayout, order_update_viewpager){tab, position->
            when(position){
                0->tab.text = getString(R.string.qabul_qiluvchi_malumotlari)
                1->tab.text = getString(R.string.passport)
                2->tab.text = getString(R.string.audio_yuklash)
                3->tab.text = getString(R.string.tolov)
                else->tab.text = "No page found"
            }
        }.attach()


    }


    private fun searchByBarcode(){
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
}