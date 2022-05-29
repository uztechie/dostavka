package uz.ibroxim.dostavkauz.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_news_details.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.ibroxim.dostavkauz.MainActivity
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.NewsAdapter
import uz.ibroxim.dostavkauz.adapter.PostalHistoryAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.News
import uz.ibroxim.dostavkauz.models.PostalHistory
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class HomeFragment:Fragment(R.layout.fragment_home), TextWatcher {
    private val TAG = "HomeFragment"
    private lateinit var viewModel:AppViewModel
    private lateinit var postalHisAdapter: PostalHistoryAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var successFailedDialog:SuccessFailedDialog
    private var historyList = mutableListOf<PostalHistory>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as MainActivity).viewModel
        customProgressDialog = CustomProgressDialog(requireContext())

        home_et_search.addTextChangedListener(this)

        if (viewModel.getUser().isEmpty()){
            findNavController().navigate(HomeFragmentDirections.actionGlobalLoginFragment())
        }
        else{
            SharedPref.token = "Token "+viewModel.getUser().get(0).token
        }

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_FAILED){
                    viewModel.loadPostalHistory(SharedPref.token)
                }
            }

        })


        postalHisAdapter = PostalHistoryAdapter(object :
            PostalHistoryAdapter.PostalHistoryAdapterCallBack {
            override fun onItemClick(postalHistory: PostalHistory) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPostalHistoryDetailsFragment(postalHistory))
            }

        })

        home_recyclerview?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postalHisAdapter
        }

        viewModel.postalHistoryResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    successFailedDialog.show()
                    successFailedDialog.setStatusImage(R.drawable.error)
                    successFailedDialog.setTitle(getString(R.string.buyurtmalar_tarixi))
                    successFailedDialog.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialog.setButtonText(getString(R.string.qayta_urinish))
                    successFailedDialog.showCloseButton(true)
                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)


                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            val list = it.data
                            postalHisAdapter.differ.submitList(list)
                            historyList.clear()
                            historyList = list as MutableList<PostalHistory>
                        }
                        else{
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.error)
                            successFailedDialog.setTitle(getString(R.string.buyurtmalar_tarixi))
                            successFailedDialog.setMessage(it.message?:getString(R.string.xatolik))
                            successFailedDialog.setButtonText(getString(R.string.qayta_urinish))
                            successFailedDialog.showCloseButton(true)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }

                    }
                }
            }
        }

        viewModel.loadPostalHistory(SharedPref.token)


        SharedPref.resetCustomerData()


    }

    private fun newsDetailsBottomSheet(news: News) {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_news_details)
        dialog.show()
        dialog.news_details_tv_title.text = news.name
        dialog.news_details_tv_desc.text = news.description
        dialog.news_details_tv_date.text = Utils.reformatDateFromStringLocale(news.updated_at)

        Glide.with(dialog.news_details_image)
            .load(news.image)
            .into(dialog.news_details_image)

        dialog.news_details_btn_close.setOnClickListener {
            dialog.dismiss()
        }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    var job:Job? = null
    override fun afterTextChanged(p0: Editable?) {
        job?.cancel()
        job = lifecycleScope.launch {
            delay(500)
            val query = p0.toString().trim().lowercase()
            if (query.isNotEmpty()){
                val filteredList = historyList.filter { postalHistory ->
                    postalHistory.barcode.toString().contains(query)
                }
                postalHisAdapter.differ.submitList(filteredList)
            }
            else{
                postalHisAdapter.differ.submitList(historyList)
            }

        }
    }


}