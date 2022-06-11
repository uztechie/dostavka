package uz.ibroxim.dostavkauz.fragments.driver

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_new_orders.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.NewOrderAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.Order
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class NewOrdersFragment:Fragment(R.layout.fragment_new_orders) {
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var successFailedDialog: SuccessFailedDialog
    private lateinit var newOrderAdapter: NewOrderAdapter

    private val viewModel by viewModels<AppViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        customProgressDialog = CustomProgressDialog(requireContext())

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {

            }

        })

        newOrderAdapter = NewOrderAdapter(0, requireContext(),object : NewOrderAdapter.NewOrderAdapterCallBack {
            override fun onItemClick(order: Order) {
                findNavController().navigate(NewOrdersFragmentDirections.actionNewOrdersFragmentToNewOrderDetailsFragment(order))
            }

        })


        new_order_recyclerview?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newOrderAdapter
        }

        viewModel.loadNewOrders(SharedPref.token)

        viewModel.newOrderResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Utils.toastIconError(requireActivity(), response.message)
                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            newOrderAdapter.differ.submitList(it.data)
                        }
                        else{
                            Utils.toastIconError(requireActivity(), it.message)
                        }
                    }
                }
            }
        }



    }

    private fun initToolbar(){
        toolbar_btn_back.visibility = View.INVISIBLE
        toolbar_title.text = getString(R.string.yangi_buyurtmalar)
    }

}