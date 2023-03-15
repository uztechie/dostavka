package uz.ibroxim.dostavkauz.fragments.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_order_hsitory_details.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.NewOrderItemAdapter
import uz.ibroxim.dostavkauz.adapter.StatusAdapter
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.PostalHistory
import uz.ibroxim.dostavkauz.utils.SharedPref

@AndroidEntryPoint
class PostalHistoryDetailsFragment:Fragment(R.layout.fragment_order_hsitory_details) {

    private lateinit var statusAdapter:StatusAdapter
    private lateinit var itemAdapter:NewOrderItemAdapter
    private var postalHistory : PostalHistory? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        statusAdapter = StatusAdapter()
        itemAdapter = NewOrderItemAdapter()

        arguments?.let {
            postalHistory = PostalHistoryDetailsFragmentArgs.fromBundle(it).postalHistory

            postalHistory?.let {
                postal_details_tv_postal_id.text = it.barcode.toString()

                postal_details_tv_receiver_name.text = it.receiver_full_name

                postal_details_tv_postal_destination.text = it.receiver_address?.street+", "+
                        it.receiver_address?.quarters_name+", "+
                        it.receiver_address?.district_name+", "+
                        it.receiver_address?.region_name

                var list = it.status
                list = list?.sortedByDescending { postalStatus ->
                    postalStatus.status
                }
                statusAdapter.differ.submitList(list)

                it.items?.let {
                    itemAdapter.differ.submitList(it)
                }
            }

            initButtons()



        }

        postal_details_recyclerview_status?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = statusAdapter
        }


        postal_details_recyclerview_items?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }

    }

    private fun initButtons(){
        postal_details_btn_send_mail.setOnClickListener {
            SharedPref.receiver_quarterId = postalHistory?.receiver_address?.quarters_id?:0
            SharedPref.receiver_address = postalHistory?.receiver_address?.street?:""
            SharedPref.receiver_id = postalHistory?.receiver?:0

            println("initButtons receiver_quarterId "+ SharedPref.receiver_quarterId)
            println("initButtons receiver_address "+ SharedPref.receiver_address)
            println("initButtons receiver_id "+ SharedPref.receiver_id)
            println("initButtons customer_latitude "+ SharedPref.customer_latitude)
            println("initButtons customer_longitude "+ SharedPref.customer_longitude)

            if (SharedPref.customer_latitude == "0.0" || SharedPref.customer_longitude == "0.0"){
                showSetLocationDialog()
            }
            else{
                findNavController().navigate(PostalHistoryDetailsFragmentDirections.actionPostalHistoryDetailsFragmentToCreateMailItemsFragment())
            }



        }
    }

    private fun showSetLocationDialog(){
        val dialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {
                findNavController().navigate(PostalHistoryDetailsFragmentDirections.actionPostalHistoryDetailsFragmentToChooseLocationFragment(false))
            }

            override fun onActionButton2Click(clickAction: String) {

            }
        })
        dialog.show()
        dialog.setTitle(getString(R.string.joylashuvni_tanlang))
        dialog.setMessage(getString(R.string.iltimos_hozirgi_manzilingizni_xarita_orqali_korsating))
        dialog.setButton1Text(getString(R.string.davom_etish))
        dialog.showCloseButton(true)
    }

    private fun initToolbar(){
        toolbar_title.text = getString(R.string.pochta_haqida)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}