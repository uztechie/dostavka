package uz.ibroxim.dostavkauz.fragments.driver

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_add_update_item.*
import kotlinx.android.synthetic.main.fragment_order_update_items.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.DriverItemAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class OrderUpdateItemsFragment:Fragment(R.layout.fragment_order_update_items) {

    private val TAG = "OrderUpdateItemsFragmen"

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var driverItemAdapter:DriverItemAdapter

    var mainItem:Item? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: items "+Constants.orderItems)

        customProgressDialog = CustomProgressDialog(requireContext())


        driverItemAdapter = DriverItemAdapter(object : DriverItemAdapter.DriverItemAdapterCallBack {
            override fun onOptionsClick(item: Item, view: View) {
                val menu = PopupMenu(requireContext(), view)
                menu.inflate(R.menu.item_options_menu)
                menu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(p0: MenuItem?): Boolean {
                        when(p0?.itemId){
                            R.id.item_menu_update->{
                                showUpdateItemDialog(item)
                            }
                            R.id.item_menu_delete->{
                                showDeleteDialog(item)
                            }
                        }

                        return true
                    }

                })
                menu.show()
            }

        })


        order_update_items_recyclerview?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = driverItemAdapter
        }
        driverItemAdapter.differ.submitList(Constants.orderItems)

        order_update_btn_add.setOnClickListener {
            showAddItemDialog()
        }

        addItemResponse()
        updateItemResponse()
        deleteItemResponse()
    }

    private fun showUpdateItemDialog(item: Item) {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_add_update_item)
        dialog.show()
        dialog.dialog_add_item_title.setText(getString(R.string.taxrirlash))
        dialog.dialog_add_item_tv_name.setText(item.name)
        dialog.dialog_add_item_tv_amount.setText(item.amount)

        dialog.dialog_add_item_btn_close.setOnClickListener {
            dialog.dismiss()
        }
        dialog.dialog_add_item_btn_save.setOnClickListener {
            val name = dialog.dialog_add_item_tv_name.text.toString().trim()
            val amount = dialog.dialog_add_item_tv_amount.text.toString().trim()

            if (name.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.buyum_nomini_kiriting))
                return@setOnClickListener
            }

            if (amount.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.buyum_sonini_kiriting))
                return@setOnClickListener
            }

            item.id?.let {
                dialog.dismiss()
                val map = HashMap<String, Any>()
                map["item_id"] = it
                map["name"] = name
                map["amount"] = amount

                mainItem = Item(it, name, amount)

                viewModel.requestUpdateItem(map)
            }


        }

    }

    private fun showAddItemDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_add_update_item)
        dialog.show()
        dialog.dialog_add_item_title.setText(getString(R.string.buyum_qo_shish))

        dialog.dialog_add_item_btn_close.setOnClickListener {
            dialog.dismiss()
        }
        dialog.dialog_add_item_btn_save.setOnClickListener {
            val name = dialog.dialog_add_item_tv_name.text.toString().trim()
            val amount = dialog.dialog_add_item_tv_amount.text.toString().trim()

            if (name.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.buyum_nomini_kiriting))
                return@setOnClickListener
            }

            if (amount.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.buyum_sonini_kiriting))
                return@setOnClickListener
            }

            dialog.dismiss()
            val map = HashMap<String, Any>()
            map["postal_id"] = Constants.orderId
            map["name"] = name
            map["amount"] = amount
            viewModel.requestAddItem(map)


        }

    }


    private fun showDeleteDialog(item: Item) {
        Log.d(TAG, "showDeleteDialog: item "+item)
        val successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                item.id?.let {
                    viewModel.requestDeleteItem(it)
                }
                mainItem = item
            }
        })

        successFailedDialog.show()
        successFailedDialog.setStatusImage(R.drawable.question)
        successFailedDialog.setTitle(getString(R.string.buyumni_ochirish))
        successFailedDialog.setMessage(getString(R.string.siz_buyumni_ochirmoqchimisiz))
        successFailedDialog.setButtonText(getString(R.string.o_chirish))
        successFailedDialog.showCloseButton(false)
        successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
    }

    private fun deleteItemResponse(){
        viewModel.deleteItemResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: postalResponse error "+response.message)
                    val title = getString(R.string.buyumni_ochirish)
                    showSuccessFailedDialog(title, response.message?:"", false, ACTION_DELETE)
                }
                is Resource.Success->{
                    Log.d(TAG, "onViewCreated: postalResponse "+response.data)
                    customProgressDialog.dismiss()
                    response.data?.let { itemResponse->
                        if (itemResponse.status == 200) {
                            val title = getString(R.string.buyumni_ochirish)
                            showSuccessFailedDialog(title, getString(R.string.buyum_ochirildi), true, ACTION_DELETE)
                        }
                        else{
                            val title = getString(R.string.buyumni_ochirish)
                            showSuccessFailedDialog(title, itemResponse.message?:"", false, ACTION_DELETE)
                        }
                    }
                }
            }
        }
    }

    private fun updateItemResponse(){
        viewModel.updateItemResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: postalResponse error "+response.message)
                    val title = getString(R.string.buyumni_taxrirlash)
                    showSuccessFailedDialog(title, response.message?:"", false, ACTION_UPDATE)
                }
                is Resource.Success->{
                    Log.d(TAG, "onViewCreated: postalResponse "+response.data)
                    customProgressDialog.dismiss()
                    response.data?.let { itemResponse->
                        if (itemResponse.status == 200) {
                            val title = getString(R.string.buyumni_taxrirlash)
                            showSuccessFailedDialog(title, getString(R.string.buyumni_taxrirlandi), true, ACTION_UPDATE)
                        }
                        else{
                            val title = getString(R.string.buyumni_taxrirlash)
                            showSuccessFailedDialog(title, itemResponse.message?:"", false, ACTION_UPDATE)
                        }
                    }
                }
            }
        }
    }


    private fun addItemResponse(){
        viewModel.addItemResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: postalResponse error "+response.message)
                    val title = getString(R.string.buyum_qo_shish)
                    showSuccessFailedDialog(title, response.message?:"", false, ACTION_ADD)
                }
                is Resource.Success->{
                    Log.d(TAG, "onViewCreated: postalResponse "+response.data)
                    customProgressDialog.dismiss()
                    response.data?.let { itemResponse->
                        if (itemResponse.status == 200) {

                            itemResponse.data?.let {
                                mainItem = it
                                val title = getString(R.string.buyum_qo_shish)
                                showSuccessFailedDialog(title, getString(R.string.buyum_qoshildi), true, ACTION_ADD)
                            }

                        }
                        else{
                            val title = getString(R.string.buyum_qo_shish)
                            showSuccessFailedDialog(title, itemResponse.message?:"", false, ACTION_ADD)
                        }
                    }
                }
            }
        }
    }


    private fun showSuccessFailedDialog(title:String, message:String, isSuccess:Boolean, action:Int){


        val successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (isSuccess && action == ACTION_DELETE){
                    Constants.orderItems = Constants.orderItems.filterNot {
                        it.id == mainItem?.id
                    }.toMutableList()
                    driverItemAdapter.differ.submitList(Constants.orderItems)
                }
                else if (isSuccess && action == ACTION_UPDATE){
                    Constants.orderItems = Constants.orderItems.filterNot {
                        it.id == mainItem?.id
                    }.toMutableList()
                    mainItem?.let { Constants.orderItems.add(it) }
                    driverItemAdapter.differ.submitList(Constants.orderItems)
                }
                else if (isSuccess && action == ACTION_ADD){
                    Log.d(TAG, "onActionButtonClick: mainItem "+mainItem)
                    mainItem?.let { Constants.orderItems.add(it) }
                    Log.d(TAG, "onActionButtonClick: mainItems "+Constants.orderItems)
                    driverItemAdapter.differ.submitList(null)
                    driverItemAdapter.differ.submitList(Constants.orderItems)
                }
            }

        })

        if (isSuccess){
            successFailedDialog.show()
            successFailedDialog.setStatusImage(R.drawable.success)
            successFailedDialog.setTitle(title)
            successFailedDialog.setMessage(message)
            successFailedDialog.setButtonText(getString(R.string.yopish))
            successFailedDialog.showCloseButton(false)
            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
        }
        else {
            successFailedDialog.show()
            successFailedDialog.setStatusImage(R.drawable.error)
            successFailedDialog.setTitle(title)
            successFailedDialog.setMessage(message)
            successFailedDialog.setButtonText(getString(R.string.yopish ))
            successFailedDialog.showCloseButton(true)
            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
        }
    }


    companion object{
        val ACTION_DELETE = 1
        val ACTION_UPDATE = 2
        val ACTION_ADD = 3
    }

}