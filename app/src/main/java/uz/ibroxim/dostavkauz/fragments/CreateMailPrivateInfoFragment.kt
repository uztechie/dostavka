package uz.ibroxim.dostavkauz.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_create_mail_location.*
import kotlinx.android.synthetic.main.fragment_create_mail_location.add_stepview
import kotlinx.android.synthetic.main.fragment_create_mail_private_info.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.PhoneBottomDialog
import uz.ibroxim.dostavkauz.models.User
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel


@AndroidEntryPoint
class CreateMailPrivateInfoFragment:Fragment(R.layout.fragment_create_mail_private_info) {

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var phoneBottomDialog:PhoneBottomDialog
    private lateinit var customProgressDialog:CustomProgressDialog

    private val TAG = "AddFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customProgressDialog = CustomProgressDialog(requireContext())
        initToolbar()
        initStepProgress()

//        findNavController().navigate(CreateMailPrivateInfoFragmentDirections.actionCreateMailPrivateInfoFragmentToCreateMailPassportFragment())

        if (SharedPref.receiver_id > -1){

            mail_et_info_name.isFocusable = false
            mail_et_info_lastname.isFocusable = false
            mail_et_info_middlename.isFocusable = false
            mail_et_info_phone.isFocusable = false
            mail_et_info_phone2.isFocusable = false

            mail_et_info_name.setText(SharedPref.receiver_name)
            mail_et_info_lastname.setText(SharedPref.receiver_lastname)
            mail_et_info_middlename.setText(SharedPref.receiver_middlename)
            mail_et_info_phone.setText(SharedPref.receiver_phone1)
            mail_et_info_phone2.setText(SharedPref.receiver_phone2)

            if (SharedPref.receiver_type == 1){
                mail_radio_physical.isChecked = true
            }
            else{
                mail_radio_legal.isChecked = true
            }

        }
        else{
            mail_et_info_phone.setText(SharedPref.receiver_phone1)
            mail_et_info_name.isFocusable = true
            mail_et_info_lastname.isFocusable = true
            mail_et_info_middlename.isFocusable = true
            mail_et_info_phone.isFocusable = false
            mail_et_info_phone2.isFocusable = true
        }


        mail_radio_group.setOnCheckedChangeListener { radioGroup, i ->
            Log.d(TAG, "onViewCreated: iiii "+i)
           if (i == R.id.mail_radio_legal){
                mail_tv_name.text = getString(R.string.korxona_nomi)
                mail_tv_lastname.text = getString(R.string.korxona_raxbari)
                mail_tv_middle_name.text = getString(R.string.masul_shaxs)

               mail_et_info_name.hint = getString(R.string.korxona_nomini_kiriting)
               mail_et_info_lastname.hint = getString(R.string.ism_va_familiyani_kiriting)
               mail_et_info_middlename.hint = getString(R.string.ism_va_familiyani_kiriting)
           }
            else{
               mail_tv_name.text = getString(R.string.qabul_qiluvchi_ismi)
               mail_tv_lastname.text = getString(R.string.qabul_qiluvchining_familiyasi)
               mail_tv_middle_name.text = getString(R.string.qabul_qiluvchining_otasining_ismi)

               mail_et_info_name.hint = getString(R.string.ismni_kiriting)
               mail_et_info_lastname.hint = getString(R.string.familiyani_kiriting)
               mail_et_info_middlename.hint = getString(R.string.otasining_ismini_kiriting)
           }

        }


        mail_info_btn_next.setOnClickListener {
            val name = mail_et_info_name.text.toString().trim()
            val lastName = mail_et_info_lastname.text.toString().trim()
            val middleName = mail_et_info_middlename.text.toString().trim()
            val phone = mail_et_info_phone.text.toString().trim()
            val phon2 = mail_et_info_phone2.text.toString().trim()
            val note = mail_et_info_note.text.toString().trim()


            if (name.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.ismni_kiriting))
                return@setOnClickListener
            }

            if (lastName.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.familiyani_kiriting))
                return@setOnClickListener
            }

            if (middleName.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.otasining_ismini_kiriting))
                return@setOnClickListener
            }

            if (phone.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.asosiy_telefon_raqamni_kiriting))
                return@setOnClickListener
            }

            SharedPref.receiver_name = name
            SharedPref.receiver_lastname = lastName
            SharedPref.receiver_middlename = middleName
            SharedPref.receiver_phone1 = phone
            SharedPref.receiver_phone2 = phon2
            SharedPref.receiver_note = note

            if (mail_radio_physical.isChecked){
                SharedPref.receiver_type = 1
            }
            else{
                SharedPref.receiver_type = 2
            }



            findNavController().navigate(CreateMailPrivateInfoFragmentDirections.actionCreateMailPrivateInfoFragmentToCreateMailPassportFragment())
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
        add_stepview.go(2, false)
    }


    private fun initToolbar(){
        toolbar_btn_back.visibility = View.INVISIBLE
        toolbar_title.text = getString(R.string.yuk_yuborish)
    }

}