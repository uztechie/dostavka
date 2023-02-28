package uz.ibroxim.dostavkauz.fragments.user

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_create_mail_items.*
import kotlinx.android.synthetic.main.fragment_create_mail_location.add_stepview
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.ItemAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel
import java.io.File


@AndroidEntryPoint
class CreateMailItemsFragment : Fragment(R.layout.fragment_create_mail_items) {

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var successFailedDialog: SuccessFailedDialog
    private lateinit var itemAdapter: ItemAdapter

    private var itemsList: MutableLiveData<MutableList<Item>> = MutableLiveData()

    private val TAG = "CreateMailItemsFragment"

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
        Log.d(TAG, "onViewCreated: ")
        customProgressDialog = CustomProgressDialog(requireContext())
        initToolbar()
        initStepProgress()

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){
                    findNavController().navigate(CreateMailItemsFragmentDirections.actionGlobalHomeFragment())
                }
            }

        })

        requestPostal()


        itemAdapter = ItemAdapter(object : ItemAdapter.ItemAdapterCallBack {
            override fun onItemRemove(item: Item) {
            }

        }, mutableListOf())

        create_mail_items_recyclerview?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }

        itemsList.postValue(mutableListOf(Item(-1,"", "")))


        itemsList.observe(viewLifecycleOwner) {
            Log.d(TAG, "onViewCreated: itemsList.observe " + it)
            itemAdapter.submitList(it)
        }

        create_mail_items_btn_add.setOnClickListener {
            val list = itemAdapter.getList()
            list.add(Item(-1,"", ""))

            Log.d(TAG, "onViewCreated: itemAdapter.getList() " + list)
            itemsList.postValue(list)


        }

        create_mail_items_btn_submit.setOnClickListener {
            val readyItems = itemAdapter.getList().filter {
                it.amount.trim().isNotEmpty() && it.name.trim().isNotEmpty()
            } as MutableList

            if (readyItems.isEmpty()) {
                Utils.toastIconError(
                    requireActivity(),
                    getString(R.string.iltimos_kamida_1ta_buyumni_kiriting)
                )
                return@setOnClickListener
            }


            preparePostal(readyItems)


            Log.d(TAG, "onViewCreated: readyItems " + readyItems)
        }

        create_mail_items_btn_cancel.setOnClickListener {
            findNavController().navigate(CreateMailItemsFragmentDirections.actionGlobalHomeFragment())
        }



    }

    private fun requestPostal() {
        viewModel.postalResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    customProgressDialog.show()
                }
                is Resource.Error -> {
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: postalResponse error " + response.message)
                    successFailedDialog.show()
                    successFailedDialog.setStatusImage(R.drawable.error)
                    successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                    successFailedDialog.setMessage(response.message ?: getString(R.string.xatolik))
                    successFailedDialog.setButtonText(getString(R.string.bekor_qilish))
                    successFailedDialog.showCloseButton(true)
                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success -> {
                    Log.d(TAG, "onViewCreated: postalResponse " + response.data)
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200) {
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.success)
                            successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                            successFailedDialog.setMessage(it.message ?: "")
                            successFailedDialog.setButtonText(getString(R.string.yopish))
                            successFailedDialog.showCloseButton(false)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
                        } else {
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.error)
                            successFailedDialog.setTitle(getString(R.string.pochta_yuborish))
                            successFailedDialog.setMessage(
                                it.message ?: getString(R.string.xatolik)
                            )
                            successFailedDialog.setButtonText(getString(R.string.bekor_qilish))
                            successFailedDialog.showCloseButton(true)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }
                    }
                }
            }
        }
    }



    private fun preparePostal(items:MutableList<Item>){

        val fileUri = Uri.parse(SharedPref.receiver_passport_image_uri)
        val map = HashMap<String, Any>()

        var addressName = SharedPref.customer_knownAreaName+" "+SharedPref.customer_city+"  "+SharedPref.customer_state+"   "+SharedPref.customer_country

        if (addressName.trim().isEmpty()){
            addressName = "Номаълум"
        }

        map["sender_address_name"] = addressName
        map["sender_longitude"] = SharedPref.customer_longitude
        map["sender_latitude"] = SharedPref.customer_latitude
        map["description"] = SharedPref.receiver_mailTitle
        map["note"] = SharedPref.receiver_note
        map["quarters_id"] = SharedPref.receiver_quarterId
        map["street"] = SharedPref.receiver_address
        map["receiver_id"] = SharedPref.receiver_id
        map["items"] = items

        Log.d(TAG, "preparePostal: map $map")
        Log.d(TAG, "preparePostal: map "+map["street"].toString())
        Log.d(TAG, "preparePostal: file uri "+fileUri?.path)

        viewModel.uploadPostal(map)


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
        add_stepview.go(4, false)

    }

    private fun initToolbar() {
        toolbar_btn_back.visibility = View.INVISIBLE
        toolbar_title.text = getString(R.string.yuk_yuborish)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
//        SharedPref.resetCustomerData()
    }

}