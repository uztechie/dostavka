package uz.ibroxim.dostavkauz.fragments.driver

import android.os.Bundle
import android.util.Log
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
import uz.ibroxim.dostavkauz.utils.*
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class AcceptedOrdersFragment:Fragment(R.layout.fragment_new_orders) {
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var successFailedDialog: SuccessFailedDialog
    private lateinit var newOrderAdapter: NewOrderAdapter

    private val TAG = "AcceptedOrdersFragment"

    private val viewModel by viewModels<AppViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        customProgressDialog = CustomProgressDialog(requireContext())

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {

            }

            override fun onActionButton2Click(clickAction: String) {

            }

        })

        newOrderAdapter = NewOrderAdapter(StatusList.Accepted.id, requireContext(), object : NewOrderAdapter.NewOrderAdapterCallBack {
            override fun onItemClick(order: Order) {

                Log.d(TAG, "onItemClick: orderrrrr "+order)

                order.items?.let {
                    Log.d(TAG, "onViewCreated: itemssssss "+it)
                    Constants.orderItems = it.toMutableList()
                }
                order.payment?.let {
                    Log.d(TAG, "onItemClick: paymentssssss "+it)
                    Constants.paymentList = it.toMutableList()
                }


                Constants.orderId = order.id?:-1
                Constants.barcode = order.barcode.toString()

                findNavController().navigate(AcceptedOrdersFragmentDirections.actionAcceptedOrdersFragmentToAcceptedOrderDetailsFragment(order))
            }

        })


        new_order_recyclerview?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newOrderAdapter
        }

        viewModel.loadOrderHistory()

        viewModel.orderHistoryResponse.observe(viewLifecycleOwner){ response->
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
                            val list = it.data?.filter { order->
                                order.status_id == 1
                            }
                            newOrderAdapter.differ.submitList(list)
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
        toolbar_title.text = getString(R.string.qabul_qilingan_buyurtmalar)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}