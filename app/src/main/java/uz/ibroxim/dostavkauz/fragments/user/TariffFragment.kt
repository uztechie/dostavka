package uz.ibroxim.dostavkauz.fragments.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_tariff_details.*
import kotlinx.android.synthetic.main.fragment_tariff.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.TariffAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.Tariff
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class TariffFragment:Fragment(R.layout.fragment_tariff) {
    lateinit var customProgressDialog: CustomProgressDialog
    private val viewModel by viewModels<AppViewModel>()
    private lateinit var tariffAdapter: TariffAdapter
    private lateinit var successFailedDialog: SuccessFailedDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        customProgressDialog = CustomProgressDialog(requireContext())

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){

                }
            }

        })


        tariffAdapter = TariffAdapter(object : TariffAdapter.TariffAdapterCallBack {
            override fun onItemClick(tariff: Tariff) {
                tariffDetailsBottomSheet(tariff)
            }

        })

        tariff_recyclerview?.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tariffAdapter
        }


        viewModel.tariffResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    successFailedDialog.show()
                    successFailedDialog.setStatusImage(R.drawable.error)
                    successFailedDialog.setTitle(getString(R.string.tariflar))
                    successFailedDialog.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialog.setButtonText(getString(R.string.yopish))
                    successFailedDialog.showCloseButton(false)
                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        tariffAdapter.differ.submitList(it)
                    }
                }
            }
        }

        viewModel.loadTariff(SharedPref.token)

    }




    private fun tariffDetailsBottomSheet(tariff: Tariff) {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_tariff_details)
        dialog.show()
        dialog.tariff_details_tv_title.text = tariff.name
        dialog.tariff_details_tv_desc.text = tariff.description
        dialog.tariff_details_tv_date.text = Utils.reformatDateFromStringLocale(tariff.updated_at)

        dialog.tariff_details_btn_close.setOnClickListener {
            dialog.dismiss()
        }

    }


    private fun initToolbar(){
        toolbar_title.text = getString(R.string.tariflar)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
        toolbar_btn_back.visibility = View.INVISIBLE
    }

}