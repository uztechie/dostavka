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

@AndroidEntryPoint
class PostalHistoryDetailsFragment:Fragment(R.layout.fragment_order_hsitory_details) {

    private lateinit var statusAdapter:StatusAdapter
    private lateinit var itemAdapter:NewOrderItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        statusAdapter = StatusAdapter()
        itemAdapter = NewOrderItemAdapter()

        arguments?.let {
            val postalHistory = PostalHistoryDetailsFragmentArgs.fromBundle(it).postalHistory

            postal_details_tv_postal_id.text = postalHistory.barcode.toString()
            postal_details_tv_postal_destination.text = postalHistory.receiver_address?.street+", "+
                    postalHistory.receiver_address?.quarters_name+", "+
                    postalHistory.receiver_address?.district_name+", "+
                    postalHistory.receiver_address?.region_name

            var list = postalHistory.status
            list = list?.sortedByDescending { postalStatus ->
                postalStatus.status
            }
            statusAdapter.differ.submitList(list)

            postalHistory.items?.let {
                itemAdapter.differ.submitList(it)
            }

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

    private fun initToolbar(){
        toolbar_title.text = getString(R.string.pochta_haqida)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}