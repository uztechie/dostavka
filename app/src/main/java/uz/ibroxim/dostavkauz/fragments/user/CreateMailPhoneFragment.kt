package uz.ibroxim.dostavkauz.fragments.user

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_create_mail_location.add_stepview
import kotlinx.android.synthetic.main.fragment_create_mail_phone.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel


@AndroidEntryPoint
class CreateMailPhoneFragment:Fragment(R.layout.fragment_create_mail_phone) {

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog:CustomProgressDialog
    private var senderPhone = ""

    private val TAG = "AddFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProgressDialog = CustomProgressDialog(requireContext())
        initToolbar()
        initStepProgress()
        getSenderPhone()

//        findNavController().navigate(CreateMailPhoneFragmentDirections.actionCreateMailPhoneFragmentToCreateMailPrivateInfoFragment())



        create_mail_phone_btn_next.setOnClickListener {
            val phone = create_mail_phone_et_phone.text.toString().trim()
            if (phone.length<9){
                Utils.toastIconError(requireActivity(), getString(R.string.telefon_raqamni_toliq_kiriting))
                return@setOnClickListener
            }
            if (phone == senderPhone){
                Utils.toastIconError(requireActivity(), getString(R.string.siz_ozingizning_telefon_raqamingizga_pochta_yubora_olmaysiz))
                return@setOnClickListener
            }


            SharedPref.receiver_phone1 = phone
            viewModel.loginCustomer(phone)

        }

        viewModel.loginCustomerResponse.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    Log.d(TAG, "checkPhoneFromServer: error "+it.message)
                    customProgressDialog.dismiss()
                    it.message?.let {message->
                        Utils.toastIconError(requireActivity(), message)
                    }


                    viewModel.loginCustomerResponse = MutableLiveData()

                }
                is Resource.Success->{

                    viewModel.loginCustomerResponse = MutableLiveData()
                    Log.d(TAG, "checkPhoneFromServer: success ")
                    customProgressDialog.dismiss()

                    it.data?.let { login->
                        if (login.status == 200){
                            login.data?.let { user->
                                SharedPref.receiver_token = "Token "+user.token
                                SharedPref.receiver_id = user.id
                                SharedPref.receiver_name = user.first_name?:""
                                SharedPref.receiver_lastname = user.last_name?:""
                                SharedPref.receiver_middlename = user.surname?:""
                                SharedPref.receiver_phone1 = user.phone?:""
                                SharedPref.receiver_phone2 = user.phone2?:""
                                SharedPref.receiver_passport_serial = user.passport_serial?:""
                                SharedPref.receiver_passport_id = user.passport_number?:""
                                SharedPref.receiver_passport_image = user.passport_image?:""

                            }
                        }

                        findNavController().navigate(CreateMailPhoneFragmentDirections.actionCreateMailPhoneFragmentToCreateMailPrivateInfoFragment())

                    }

                }
            }
        }



    }

    private fun getSenderPhone(){
        viewModel.getUserLive().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                senderPhone = it[0].phone?:""
                println("getSenderPhone phone $senderPhone")
            }
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
                    add(getString(R.string.buyumlar))
                }
            }) // You should specify only steps number or steps array of strings.
            // In case you specify both steps array is chosen.
            .stepsNumber(5)
            .animationDuration(resources.getInteger(android.R.integer.config_shortAnimTime))
            // other state methods are equal to the corresponding xml attributes
            .commit()

        add_stepview.go(1, false)
    }


    private fun initToolbar(){
        toolbar_btn_back.visibility = View.INVISIBLE
        toolbar_title.text = getString(R.string.yuk_yuborish)
    }

}