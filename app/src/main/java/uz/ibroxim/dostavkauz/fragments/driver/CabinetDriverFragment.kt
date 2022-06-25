package uz.ibroxim.dostavkauz.fragments.driver

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_update_user.*
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kotlinx.android.synthetic.main.fragment_cabinet.cabinet_user_card
import kotlinx.android.synthetic.main.fragment_cabinet_driver.*
import uz.ibroxim.dostavkauz.DriverActivity
import uz.ibroxim.dostavkauz.LoginActivity
import uz.ibroxim.dostavkauz.UserActivity
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.User
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class CabinetDriverFragment:Fragment(R.layout.fragment_cabinet_driver) {

    private lateinit var  viewModel:AppViewModel
    private lateinit var successFailedDialog: SuccessFailedDialog
    private lateinit var customProgressDialog: CustomProgressDialog



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        viewModel = (requireActivity() as DriverActivity).viewModel
        customProgressDialog = CustomProgressDialog(requireContext())

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){
                    SharedPref.token = ""
                    Firebase.messaging.unsubscribeFromTopic(Constants.TOPIC_DRIVER)
                    Firebase.messaging.unsubscribeFromTopic(Constants.TOPIC_CUSTOMER)

                    viewModel.deleteUser().invokeOnCompletion {
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }

        })



        viewModel.getUserLive().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                val user = it[0]
                cabinet_driver_fullname.text = user.first_name+" "+user.last_name+" "+user.surname
                cabinet_driver_phone.text = user.phone
            }
        }

        cabinet_driver_card.setOnClickListener {
            if (viewModel.getUser().isNotEmpty()){
                updateBottomSheet(viewModel.getUser()[0])
                viewModel.updateCustomerResponse = MutableLiveData()
            }
        }

        cabinet_driver_btn_logout.setOnClickListener {
            successFailedDialog.show()
            successFailedDialog.setStatusImage(R.drawable.question)
            successFailedDialog.setTitle(getString(R.string.tizimdan_chiqish))
            successFailedDialog.setMessage(getString(R.string.siz_rostdan_tizimdan_chiqmoqchimisiz))
            successFailedDialog.setButtonText(getString(R.string.ha))
            successFailedDialog.showCloseButton(true)
            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
        }

        cabinet_driver_accepted_layout.setOnClickListener {
            findNavController().navigate(CabinetDriverFragmentDirections.actionCabinetDriverFragmentToAcceptedOrdersFragment())
        }

        cabinet_driver_history_layout.setOnClickListener {
            findNavController().navigate(CabinetDriverFragmentDirections.actionCabinetDriverFragmentToOrderHistoryFragment())
        }


    }

    private fun updateBottomSheet(user: User) {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_update_user)
        dialog.show()
        dialog.update_et_name.setText(user.first_name)
        dialog.update_et_surname.setText(user.last_name)
        dialog.update_et_middle_name.setText(user.surname)

        dialog.update_btn_close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.update_btn_save.setOnClickListener {
            val name = dialog.update_et_name.text.toString().trim()
            val lastName = dialog.update_et_surname.text.toString().trim()
            val middleName = dialog.update_et_middle_name.text.toString().trim()

            if (name.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.iltimos_ismingizni_kiriting))
                return@setOnClickListener
            }
            if (lastName.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.iltimos_familiyangizni_kiriting))
                return@setOnClickListener
            }
            if (middleName.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.iltimos_otangizni_ismini_kiriting))
                return@setOnClickListener
            }

            val map = HashMap<String, Any>()
            map["first_name"] = name
            map["last_name"] = lastName
            map["surname"] = middleName
            viewModel.updateCustomer(map, SharedPref.token)


            viewModel.updateCustomerResponse.observe(viewLifecycleOwner){ response->
                when(response){
                    is Resource.Loading->{
                        customProgressDialog.show()
                    }
                    is Resource.Error->{
                        customProgressDialog.dismiss()
                        successFailedDialog.show()
                        successFailedDialog.setStatusImage(R.drawable.error)
                        successFailedDialog.setTitle(getString(R.string.ma_lumotlarni_yangilash))
                        successFailedDialog.setMessage(response.message?:getString(R.string.xatolik))
                        successFailedDialog.setButtonText(getString(R.string.yopish))
                        successFailedDialog.showCloseButton(false)
                        successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                    }
                    is Resource.Success->{
                        customProgressDialog.dismiss()
                        response.data?.let {
                            if (it.status == 200){

                                user.first_name = name
                                user.last_name = lastName
                                user.surname = middleName

                                viewModel.deleteUser()
                                viewModel.insertUser(user).invokeOnCompletion {
                                    Utils.toastIconSuccess(requireActivity(), getString(R.string.malumotlar_yangilandi))
                                    dialog.dismiss()
                                }

                            }
                            else{
                                customProgressDialog.dismiss()
                                successFailedDialog.show()
                                successFailedDialog.setStatusImage(R.drawable.error)
                                successFailedDialog.setTitle(getString(R.string.ma_lumotlarni_yangilash))
                                successFailedDialog.setMessage(it.message?:getString(R.string.xatolik))
                                successFailedDialog.setButtonText(getString(R.string.yopish))
                                successFailedDialog.showCloseButton(false)
                                successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                            }
                        }
                    }
                }
            }

        }

    }

    private fun initToolbar(){
        toolbar_title.text = getString(R.string.kabinet)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
        toolbar_btn_back.visibility = View.INVISIBLE

    }

}