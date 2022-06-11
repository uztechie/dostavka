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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
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
                    findNavController().navigate(CreateMailPhoneFragmentDirections.actionGlobalHomeFragment())
                }
            }

        })


        viewModel.postalResponse.observe(viewLifecycleOwner){ response->
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
                    response.data?.let {
                        if (it.status == 200){
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.success)
                            successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                            successFailedDialog.setMessage(it.message?:"")
                            successFailedDialog.setButtonText(getString(R.string.yopish))
                            successFailedDialog.showCloseButton(false)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
                        }
                        else{
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.error)
                            successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                            successFailedDialog.setMessage(it.message?:getString(R.string.xatolik))
                            successFailedDialog.setButtonText(getString(R.string.bekor_qilish))
                            successFailedDialog.showCloseButton(true)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }
                    }
                }
            }
        }

        create_mail_passport_btn_choose_image.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            photoPickerIntent.type = "image/*"
            launchSomeActivity.launch(photoPickerIntent)
        }

        create_mail_passport_btn_submit.setOnClickListener {
            if (fileUri == null){
                Utils.toastIconError(requireActivity(), getString(R.string.iltimos_pasport_rasmini_tanlang))
                return@setOnClickListener
            }

            if (SharedPref.receiver_id>-1){
                preparePostal()
            }
            else{
                preparePostalWithReceiver()
            }
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
                create_mail_passport_img.setImageURI(
                    selectedImageUri
                )
            }
        }
    }


    private fun preparePostalWithReceiver(){
        val map = HashMap<String, RequestBody>()

        val receiverAddress = SharedPref.receiver_address
//        val receiverAddressBody = receiverAddress.toRequestBody("text/plain".toMediaTypeOrNull())

        val receiverAddressBody = RequestBody.create("text/plain".toMediaTypeOrNull(), receiverAddress)
        Log.d(TAG, "preparePostalWithReceiver: receiverAddress "+receiverAddress)

        map["sender_address_name"] = (SharedPref.customer_knownAreaName+", "+SharedPref.customer_city+", "+SharedPref.customer_state+", "+SharedPref.customer_country).toRequestBody(MultipartBody.FORM)
        map["sender_longitude"] = SharedPref.customer_longitude.toRequestBody(MultipartBody.FORM)
        map["sender_latitude"] = SharedPref.customer_latitude.toRequestBody(MultipartBody.FORM)
        map["description"] = SharedPref.receiver_mailTitle.toRequestBody(MultipartBody.FORM)
        map["first_name"] = SharedPref.receiver_name.toRequestBody(MultipartBody.FORM)
        map["last_name"] = SharedPref.receiver_lastname.toRequestBody(MultipartBody.FORM)
        map["surname"] = SharedPref.receiver_middlename.toRequestBody(MultipartBody.FORM)
        map["phone"] = SharedPref.receiver_phone1.toRequestBody(MultipartBody.FORM)
        map["phone2"] = SharedPref.receiver_phone2.toRequestBody(MultipartBody.FORM)
        if (SharedPref.receiver_phone2.isEmpty()){
            Log.d(TAG, "preparePostalWithReceiver: SharedPref.receiver_phone2 empty ")
        }
        if (SharedPref.receiver_phone1.isDigitsOnly()){
        }
        if (SharedPref.receiver_phone2.isDigitsOnly()){
        }
        map["note"] = SharedPref.receiver_note.toRequestBody(MultipartBody.FORM)
        map["quarters_id"] = SharedPref.receiver_quarterId.toString().toRequestBody(MultipartBody.FORM)
        map["street"] = SharedPref.receiver_address.toRequestBody(MultipartBody.FORM)
        map["customer_type"] = SharedPref.receiver_type.toString().toRequestBody(MultipartBody.FORM)

        Log.d(TAG, "preparePostalWithReceiver: map "+map)
        Log.d(TAG, "preparePostalWithReceiver: fileUri "+fileUri)

        val file = File(getPath(fileUri))
        Log.d(TAG, "preparePostalWithReceiver: file "+file)

        val filePart = file.asRequestBody("image/*".toMediaType())
        val passport = MultipartBody.Part.createFormData("passport", file.name, filePart)


        viewModel.uploadPostalWithCustomer(map, passport, SharedPref.token)

    }

    private fun preparePostal(){
        val map = HashMap<String, RequestBody>()
        val receiverAddress = SharedPref.receiver_address
        val receiverAddressBody = receiverAddress.toRequestBody(MultipartBody.FORM)
        Log.d(TAG, "preparePostal: receiverAddress "+receiverAddress)


        map["sender_address_name"] = (SharedPref.customer_knownAreaName+", "+SharedPref.customer_city+", "+SharedPref.customer_state+", "+SharedPref.customer_country).toRequestBody(MultipartBody.FORM)
        map["sender_longitude"] = SharedPref.customer_longitude.toRequestBody(MultipartBody.FORM)
        map["sender_latitude"] = SharedPref.customer_latitude.toRequestBody(MultipartBody.FORM)
        map["description"] = SharedPref.receiver_mailTitle.toRequestBody(MultipartBody.FORM)
        map["note"] = SharedPref.receiver_note.toRequestBody(MultipartBody.FORM)
        map["quarters_id"] = SharedPref.receiver_quarterId.toString().toRequestBody(MultipartBody.FORM)
        map["street"] = SharedPref.receiver_address.toRequestBody(MultipartBody.FORM)
        map["receiver_id"] = SharedPref.receiver_id.toString().toRequestBody(MultipartBody.FORM)

        Log.d(TAG, "preparePostal: map "+map)
        Log.d(TAG, "preparePostal: map "+map["street"].toString())
        Log.d(TAG, "preparePostal: file uri "+fileUri?.path)

        val file = File(getPath(fileUri))
        Log.d(TAG, "preparePostalWithReceiver: file "+file)

        file.let {
            val filePart = file.asRequestBody("image/*".toMediaType())
            val passport = MultipartBody.Part.createFormData("passport", file.name, filePart)


            viewModel.uploadPostal(map, passport, SharedPref.token)

        }


    }


    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
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
        add_stepview.go(3, false)

    }



    private fun initToolbar(){
        toolbar_btn_back.visibility = View.INVISIBLE
        toolbar_title.text = getString(R.string.yuk_yuborish)
    }
}