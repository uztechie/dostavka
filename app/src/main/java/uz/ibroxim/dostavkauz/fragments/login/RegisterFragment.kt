package uz.ibroxim.dostavkauz.fragments.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.ibroxim.dostavkauz.DriverActivity
import uz.ibroxim.dostavkauz.UserActivity
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class RegisterFragment:Fragment(R.layout.fragment_register) {
    private val viewModel by viewModels<AppViewModel>()
    private var phone = ""
    private lateinit var customProgressDialog: CustomProgressDialog
    private val TAG = "RegisterFragment"



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customProgressDialog = CustomProgressDialog(requireContext())

        arguments?.let {
            phone = RegisterFragmentArgs.fromBundle(it).phone
            register_et_phone.setText(phone)
            Log.d(TAG, "onViewCreated: phoneeeee "+phone)
        }


        viewModel.registerCustomerResponse.observe(viewLifecycleOwner){
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

                }
                is Resource.Success->{
                    Log.d(TAG, "checkPhoneFromServer: success ")
                    customProgressDialog.dismiss()

                    it.data?.let { login->
                        if (login.status == 200){
                            login.data?.let { user->

                                lifecycleScope.launch {
                                    viewModel.insertUser(user)
                                    delay(500)
                                }.invokeOnCompletion {
                                    Utils.toastIconSuccess(requireActivity(), getString(R.string.royxatdan_otish_yakunlandi))
                                    SharedPref.token = "Token " + user.token

                                    if (user.type == Constants.USER_TYPE_DRIVER){
                                        startActivity(Intent(requireActivity(), DriverActivity::class.java))
                                        requireActivity().finish()
                                    }
                                    else{
                                        startActivity(Intent(requireActivity(), UserActivity::class.java))
                                        requireActivity().finish()
                                    }
                                }
                            }



                        }
                        else{
                            Utils.toastIconError(requireActivity(), login.message)
                        }
                    }

                }
            }
        }


        register_btn_save.setOnClickListener {
            val name = register_et_name.text.toString().trim()
            val lastName = register_et_surname.text.toString().trim()
            val middleName = register_et_middle_name.text.toString().trim()
            val phone2 = register_et_phone2.text.toString().trim()
            val note = register_et_note.text.toString().trim()

            if (name.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.iltimos_ismingizni_kiriting))
                return@setOnClickListener
            }
            if (lastName.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.iltimos_familiyangizni_kiriting))
                return@setOnClickListener
            }
            if (lastName.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.iltimos_otangizni_ismini_kiriting))
                return@setOnClickListener
            }


            val map = HashMap<String, Any>()
            map["phone"] = phone
            map["phone2"] = phone2
            map["first_name"] = name
            map["last_name"] = lastName
            map["surname"] = middleName
            map["notes"] = note
            viewModel.registerCustomer(map)
        }








    }
}