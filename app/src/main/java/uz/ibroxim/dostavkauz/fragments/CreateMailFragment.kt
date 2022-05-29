package uz.ibroxim.dostavkauz.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_region_list.*
import kotlinx.android.synthetic.main.fragment_create_mail_location.*
import kotlinx.android.synthetic.main.fragment_create_mail_location.add_stepview
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.DistrictAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.adapters.QuarterAdapter
import uz.techie.mexmash.adapters.RegionAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.models.District
import uz.techie.mexmash.models.Quarter
import uz.techie.mexmash.models.Region


@AndroidEntryPoint
class CreateMailFragment:Fragment(R.layout.fragment_create_mail_location) {

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog:CustomProgressDialog


    private var note = ""


    private val TAG = "AddFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        customProgressDialog = CustomProgressDialog(requireContext())
        initToolbar()
        initStepProgress()
        Utils.getLastKnownLocation(requireContext())

//        findNavController().navigate(CreateMailFragmentDirections.actionCreateMailFragmentToCreateMailPhoneFragment())

        mail_et_location_from.setOnClickListener {
            findNavController().navigate(CreateMailFragmentDirections.actionCreateMailFragmentToChooseLocationFragment())
        }

        mail_et_location_province.setText(null)
        mail_et_location_province.setOnClickListener {
            regionBottomSheet()
        }
        mail_et_location_region.setOnClickListener {
            districtBottomSheet()
        }
        mail_et_location_quarter.setOnClickListener {
            quarterBottomSheet()
        }

        if (SharedPref.customer_latitude != "0.0" && SharedPref.customer_longitude != "0.0"){
            var location = SharedPref.customer_knownAreaName+"  "+SharedPref.customer_city+"  "+SharedPref.customer_state+"  "+SharedPref.customer_country

            if (location.trim().isEmpty()){
                location = SharedPref.customer_latitude+"   "+SharedPref.customer_longitude
            }

            mail_et_location_from.setText(location)
        }
        else{
            mail_et_location_from.setText(null)
        }

        if (SharedPref.receiver_regionId != -1){
            mail_et_location_province.setText(SharedPref.receiver_region)
            mail_et_location_region.visibility = View.VISIBLE
        }


        if (SharedPref.receiver_districtId != -1){
            mail_et_location_region.setText(SharedPref.receiver_district)
            mail_et_location_quarter.visibility = View.VISIBLE
        }

        if (SharedPref.receiver_quarterId != -1){
            mail_et_location_quarter.setText(SharedPref.receiver_quarter)
            mail_et_location_address.visibility = View.VISIBLE
        }

        if (SharedPref.receiver_address.isNotEmpty()){
            mail_et_location_address.setText(SharedPref.receiver_address)
        }

        if (SharedPref.receiver_mailTitle.isNotEmpty()){
            mail_et_location_mail_title.setText(SharedPref.receiver_mailTitle)
        }




        create_mail_location_btn_next.setOnClickListener {
            val mailTitles = mail_et_location_mail_title.text.toString().trim()
            val address = mail_et_location_address.text.toString().trim()

            if (SharedPref.customer_latitude == "0.0" || SharedPref.customer_longitude == "0.0"){
                Utils.toastIconError(requireActivity(), getString(R.string.joylashuvni_tanlang))
                return@setOnClickListener
            }
            if (SharedPref.receiver_regionId == -1){
                Utils.toastIconError(requireActivity(), getString(R.string.viloyatni_tanlang))
                return@setOnClickListener
            }

            if (SharedPref.receiver_districtId == -1){
                Utils.toastIconError(requireActivity(), getString(R.string.tumanni_tanlang))
                return@setOnClickListener
            }

            if (SharedPref.receiver_quarterId == -1){
                Utils.toastIconError(requireActivity(), getString(R.string.mahallani_tanlang))
                return@setOnClickListener
            }
            if (address.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.manzilni_kiriting))
                return@setOnClickListener
            }

            if (mailTitles.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.yuk_nomlarini_kiriting))
                return@setOnClickListener
            }


            SharedPref.receiver_region = mail_et_location_province.text.toString()
            SharedPref.receiver_district = mail_et_location_region.text.toString()
            SharedPref.receiver_quarter = mail_et_location_quarter.text.toString()
            SharedPref.receiver_address = mail_et_location_address.text.toString()
            SharedPref.receiver_mailTitle = mailTitles


            findNavController().navigate(CreateMailFragmentDirections.actionCreateMailFragmentToCreateMailPhoneFragment())
        }



    }

    private fun initStepProgress() {
        add_stepview.state
            .steps(object : ArrayList<String?>() {
                init {
                    add(getString(R.string.manzillar))
                    add(getString(R.string.telefon_raqami))
                    add(getString(R.string.qabul_qiluvchi_malumotlari))
                    add(getString(R.string.passport))
                }
            }) // You should specify only steps number or steps array of strings.
            // In case you specify both steps array is chosen.
            .stepsNumber(4)
            .animationDuration(resources.getInteger(android.R.integer.config_shortAnimTime))
            // other state methods are equal to the corresponding xml attributes
            .commit()


    }


    private fun regionBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_region_list)

        val regionAdapter = RegionAdapter(object : RegionAdapter.RegionAdapterCallBack {
            override fun onItemClick(region: Region) {
                mail_et_location_province.setText(region.name)
                SharedPref.receiver_regionId = region.id

                SharedPref.receiver_districtId = -1
                SharedPref.receiver_quarterId = -1
                mail_et_location_region.setText(getString(R.string.tumanni_tanlang))
                mail_et_location_quarter.setText(getString(R.string.mahallani_tanlang))
                dialog.dismiss()

                mail_et_location_region.visibility = View.VISIBLE
                mail_et_location_quarter.visibility = View.GONE

            }

        })

        viewModel.getRegions().observe(viewLifecycleOwner) {
            regionAdapter.differ.submitList(it)
            dialog.show()
            dialog.dialog_region_recyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = regionAdapter
            }


        }


    }

    private fun districtBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_region_list)

        val districtAdapter = DistrictAdapter(object : DistrictAdapter.DistrictAdapterCallBack {
            override fun onItemClick(district: District) {
                mail_et_location_region.setText(district.name)
                SharedPref.receiver_districtId = district.id
                dialog.dismiss()

                SharedPref.receiver_quarterId = -1
                mail_et_location_quarter.setText(getString(R.string.mahallani_tanlang))
                mail_et_location_quarter.visibility = View.VISIBLE
            }

        })

        viewModel.getDistricts(SharedPref.receiver_regionId).observe(viewLifecycleOwner) {
            districtAdapter.differ.submitList(it)
            dialog.show()
            dialog.dialog_region_recyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = districtAdapter
            }


        }


    }

    private fun quarterBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_region_list)

        val quarterAdapter = QuarterAdapter(object : QuarterAdapter.QuarterAdapterCallBack {
            override fun onItemClick(quarter: Quarter) {
                mail_et_location_quarter.setText(quarter.name)
                SharedPref.receiver_quarterId    = quarter.id
                dialog.dismiss()

                mail_et_location_address.visibility = View.VISIBLE
            }

        })

        viewModel.getQuarters(SharedPref.receiver_districtId).observe(viewLifecycleOwner) {
            quarterAdapter.differ.submitList(it)
            dialog.show()
            dialog.dialog_region_recyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = quarterAdapter
            }


        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        mail_et_location_province.setText(null)
        Log.d(TAG, "onDestroyView: ")
    }



    private fun initToolbar(){
        toolbar_btn_back.visibility = View.INVISIBLE
        toolbar_title.text = getString(R.string.yuk_yuborish)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
//        SharedPref.resetCustomerData()
    }

}