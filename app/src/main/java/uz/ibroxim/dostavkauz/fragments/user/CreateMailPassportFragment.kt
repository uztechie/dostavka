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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_create_mail_location.add_stepview
import kotlinx.android.synthetic.main.fragment_create_mail_passport.*
import kotlinx.android.synthetic.main.fragment_upload_customer_passport.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class CreateMailPassportFragment:Fragment(R.layout.fragment_create_mail_passport){
    private val RESULT_LOAD_IMG = 333

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog:CustomProgressDialog
    private lateinit var successFailedDialog: SuccessFailedDialog
    private var fileUri:Uri? = null
    var passportSerial = ""
    var passportId = ""


    private val TAG = "AddFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customProgressDialog = CustomProgressDialog(requireContext())

        initToolbar()
        initStepProgress()

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){
                    findNavController().navigate(CreateMailPassportFragmentDirections.actionCreateMailPassportFragmentToCreateMailItemsFragment())

                }
            }

        })


        viewModel.createReceiverResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: postalResponse error "+response.message)
                    successFailedDialog.show()
                    successFailedDialog.setStatusImage(R.drawable.error)
                    successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                    successFailedDialog.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialog.setButtonText(getString(R.string.bekor_qilish))
                    successFailedDialog.showCloseButton(true)
                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    Log.d(TAG, "onViewCreated: postalResponse "+response.data)
                    customProgressDialog.dismiss()
                    response.data?.let { login->
                        if (login.status == 200){
                            login.data?.let { user ->
                                SharedPref.receiver_id = user.id
                                SharedPref.receiver_name = user.first_name ?: ""
                                SharedPref.receiver_lastname = user.last_name ?: ""
                                SharedPref.receiver_middlename = user.surname ?: "s"
                                SharedPref.receiver_phone1 = user.phone ?: ""
                                SharedPref.receiver_phone2 = user.phone2 ?: ""
                                SharedPref.receiver_passport_serial = user.passport_serial ?: ""
                                SharedPref.receiver_passport_id = user.passport_number ?: ""
                                SharedPref.receiver_passport_image = user.passport_image ?: ""
                            }

                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.success)
                            successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                            successFailedDialog.setMessage(getString(R.string.yangi_foydalanuvchi_yaratildi))
                            successFailedDialog.setButtonText(getString(R.string.keyingisi))
                            successFailedDialog.showCloseButton(false)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
                        }
                        else{
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.error)
                            successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                            successFailedDialog.setMessage(login.message?:getString(R.string.xatolik))
                            successFailedDialog.setButtonText(getString(R.string.bekor_qilish))
                            successFailedDialog.showCloseButton(true)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }
                    }
                }
            }
        }

        create_mail_passport_btn_select_image.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            photoPickerIntent.type = "image/*"
            launchSomeActivity.launch(photoPickerIntent)
        }

        create_mail_passport_btn_next.setOnClickListener {

            passportSerial = create_mail_passport_serial.text.toString().trim()
            passportId = create_mail_passport_id.text.toString().trim()
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

            if (SharedPref.receiver_id>-1 && SharedPref.receiver_passport_serial.isEmpty()){
                prepareUploadPassport()
            }
            else{
                SharedPref.receiver_passport_serial = passportSerial
                SharedPref.receiver_passport_id = passportId
                createReceiver()
            }

        }

        create_mail_passport_tv_help1.setOnClickListener {
            findNavController().navigate(CreateMailPassportFragmentDirections.actionCreateMailPassportFragmentToPassportInfoGuideFragment())
        }

        create_mail_passport_tv_help2.setOnClickListener {
            findNavController().navigate(CreateMailPassportFragmentDirections.actionCreateMailPassportFragmentToPassportInfoGuideFragment())
        }


        uploadPassportResponse()




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
                create_mail_passport_imageview.setImageURI(
                    selectedImageUri
                )
            }
        }
    }



    private fun createReceiver(){
        val map = HashMap<String, RequestBody>()

        map["first_name"] = SharedPref.receiver_name.toRequestBody(MultipartBody.FORM)
        map["last_name"] = SharedPref.receiver_lastname.toRequestBody(MultipartBody.FORM)
        map["surname"] = SharedPref.receiver_middlename.toRequestBody(MultipartBody.FORM)
        map["phone"] = SharedPref.receiver_phone1.toRequestBody(MultipartBody.FORM)
        map["phone2"] = SharedPref.receiver_phone2.toRequestBody(MultipartBody.FORM)

        map["notes"] = SharedPref.receiver_note.toRequestBody(MultipartBody.FORM)
        map["quarters_id"] = SharedPref.receiver_quarterId.toString().toRequestBody(MultipartBody.FORM)
        map["street"] = SharedPref.receiver_address.toRequestBody(MultipartBody.FORM)
        map["customer_type"] = SharedPref.receiver_type.toString().toRequestBody(MultipartBody.FORM)
        map["passport_serial"] = SharedPref.receiver_passport_serial.toRequestBody(MultipartBody.FORM)
        map["passport_number"] = SharedPref.receiver_passport_id.toRequestBody(MultipartBody.FORM)

        Log.d(TAG, "createReceiver: map "+map)
        Log.d(TAG, "createReceiver: fileUri "+fileUri)


        val file = File(getPath(fileUri))
        Log.d(TAG, "createReceiver: file "+file)

        val filePart = file.asRequestBody("image/*".toMediaType())
        val passport = MultipartBody.Part.createFormData("passport_image", file.name, filePart)


        viewModel.createReceiver(map, passport)

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
                    successFailedDialog.setButtonText(getString(R.string.bekor_qilish))
                    successFailedDialog.showCloseButton(true)
                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    Log.d(TAG, "onViewCreated: uploadPassportResponse "+response.data)
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 201){
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.success)
                            successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                            successFailedDialog.setMessage(getString(R.string.pasport_malumotlari_yuklandi))
                            successFailedDialog.setButtonText(getString(R.string.keyingisi))
                            successFailedDialog.showCloseButton(false)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
                        }
                        else{
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.error)
                            successFailedDialog.setTitle(getString(R.string.pasport_malumotlari))
                            successFailedDialog.setMessage(it.message?:getString(R.string.xatolik))
                            successFailedDialog.setButtonText(getString(R.string.bekor_qilish))
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


            viewModel.uploadCustomerPassport(map, passport, SharedPref.receiver_token)

        }
    }


    fun getPath(uri: Uri?): String? {
        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor = requireActivity().managedQuery(uri, projection, null, null, null)
            val column_index: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }catch (e:Exception){
            return ""
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
        add_stepview.go(3, false)

    }



    private fun initToolbar(){
        toolbar_btn_back.visibility = View.INVISIBLE
        toolbar_title.text = getString(R.string.yuk_yuborish)
    }
}