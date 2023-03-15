package uz.ibroxim.dostavkauz.fragments.user

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_upload_customer_passport.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class UploadCustomerPassportFragment:Fragment(R.layout.fragment_upload_customer_passport) {
    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var successFailedDialog: SuccessFailedDialog
    private var fileUri:Uri? = null
    private  val TAG = "UploadCustomerPassportF"

    var passportSerial = ""
    var passportId = ""
    var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(UploadCustomerPassportFragmentDirections.actionUploadCustomerPassportFragmentToCabinetFragment())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        customProgressDialog = CustomProgressDialog(requireContext())
        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){
                    findNavController().popBackStack()
                }
            }

            override fun onActionButton2Click(clickAction: String) {

            }

        })

        viewModel.getUserLive().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                val user = it[0]
                phone = user.phone?:""

                customer_passport_serial.setText(user.passport_serial)
                customer_passport_id.setText(user.passport_number)
                if (!user.passport_image.isNullOrEmpty()){
                    Glide.with(this)
                        .load(Constants.BASE_URL+user.passport_image)
                        .apply(Utils.options)
                        .into(customer_passport_imageview)
                }



                if (user.status == true){
                    customer_passport_serial.isFocusable = false
                    customer_passport_id.isFocusable = false

                    customer_passport_btn_select_image.isEnabled = false
                    customer_passport_btn_save.isEnabled = false

                }

            }
        }

        customer_passport_tv_help1.setOnClickListener {
            findNavController().navigate(UploadCustomerPassportFragmentDirections.actionUploadCustomerPassportFragmentToPassportInfoGuideFragment(""))
        }

        customer_passport_tv_help2.setOnClickListener {
            findNavController().navigate(UploadCustomerPassportFragmentDirections.actionUploadCustomerPassportFragmentToPassportInfoGuideFragment(""))
        }


        customer_passport_btn_select_image.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            photoPickerIntent.type = "image/*"
            launchSomeActivity.launch(photoPickerIntent)
        }


        customer_passport_btn_save.setOnClickListener {
            passportSerial = customer_passport_serial.text.toString().trim()
            passportId = customer_passport_id.text.toString().trim()
            if (passportSerial.length<9){
                Utils.toastIconError(requireActivity(), getString(R.string.pasport_seriya_raqamini_toliq_kiriting))
                return@setOnClickListener
            }

            if (passportId.length<14){
                Utils.toastIconError(requireActivity(), getString(R.string.pasport_shir_toliq_kiriting))
                return@setOnClickListener
            }

            if (fileUri == null){
                Utils.toastIconError(requireActivity(), getString(R.string.iltimos_pasport_rasmini_tanlang))
                return@setOnClickListener
            }

            prepareUploadPassport()

        }

        uploadPassportResponse()


    }

    private fun uploadPassportResponse() {
        viewModel.uploadCustomerPassportResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: uploadPassportResponse error "+response.message)
                    successFailedDialog.show()
                    successFailedDialog.setStatusImage(R.drawable.error)
                    successFailedDialog.setTitle(getString(R.string.pasport_malumotlari))
                    successFailedDialog.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialog.setButton1Text(getString(R.string.bekor_qilish))
                    successFailedDialog.showCloseButton(true)
                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    Log.d(TAG, "onViewCreated: uploadPassportResponse "+response.data)
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 201){

                            checkPhoneFromServer()
                        }
                        else{
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.error)
                            successFailedDialog.setTitle(getString(R.string.pasport_malumotlari))
                            successFailedDialog.setMessage(it.message?:getString(R.string.xatolik))
                            successFailedDialog.setButton1Text(getString(R.string.bekor_qilish))
                            successFailedDialog.showCloseButton(true)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }
                    }
                }
            }
        }
    }


    private fun prepareUploadPassport() {
        val map = HashMap<String, RequestBody>()
        map["passport_serial"] = passportSerial.toRequestBody(MultipartBody.FORM)
        map["passport_number"] = passportId.toRequestBody(MultipartBody.FORM)

        val file = File(getPath(fileUri))
        Log.d(TAG, "prepareUploadPassport: file "+file)
        Log.d(TAG, "prepareUploadPassport: map "+map)

        file.let {
            val filePart = file.asRequestBody("image/*".toMediaType())
            val passport = MultipartBody.Part.createFormData("passport_image", file.name, filePart)


            viewModel.uploadCustomerPassport(map, passport, SharedPref.token)

        }
    }

    var launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // do your operation from here....
            if (data != null && data.data != null) {
                val selectedImageUri: Uri? = data.data
                val selectedImageBitmap: Bitmap
                fileUri = selectedImageUri
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        selectedImageUri
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                customer_passport_imageview.setImageURI(
                    selectedImageUri
                )
            }
        }
    }


    private fun checkPhoneFromServer() {

        Log.d(TAG, "checkPhoneFromServer: phone "+phone)

        viewModel.loginCustomerResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    Log.d(TAG, "checkPhoneFromServer: error " + it.message)
                    customProgressDialog.dismiss()
                    it.message?.let { message ->
                        Utils.toastIconError(requireActivity(), message)
                    }

                }
                is Resource.Success -> {
                    Log.d(TAG, "checkPhoneFromServer: success ")
                    customProgressDialog.dismiss()

                    it.data?.let { login ->
                        if (login.status == 200) {
                            login.data?.let { user ->

                                lifecycleScope.launch {
                                    Log.d(TAG, "checkPhoneFromServer: user " + user)
                                    viewModel.insertUser(user)
                                }.invokeOnCompletion {
                                    SharedPref.token = "Token " + user.token

                                    successFailedDialog.show()
                                    successFailedDialog.setStatusImage(R.drawable.success)
                                    successFailedDialog.setTitle(getString(R.string.pasport_malumotlari))
                                    successFailedDialog.setMessage(getString(R.string.pasport_malumotlari_yuklandi))
                                    successFailedDialog.setButton1Text(getString(R.string.yopish))
                                    successFailedDialog.showCloseButton(false)
                                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)

                                }
                            }

                        } else {
                            Utils.toastIconError(requireActivity(), login.message)
                        }
                    }

                }
            }
        }
        Log.d(TAG, "checkPhoneFromServer: phoneee "+phone)
        viewModel.loginCustomer(phone)
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun initToolbar(){
        toolbar_btn_back.visibility = View.VISIBLE
        toolbar_title.text = getString(R.string.pasport_malumotlari)

        toolbar_btn_back.setOnClickListener {
            findNavController().navigate(UploadCustomerPassportFragmentDirections.actionUploadCustomerPassportFragmentToCabinetFragment())
        }
    }



}