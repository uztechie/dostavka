package uz.ibroxim.dostavkauz.fragments.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_news_details.*
import kotlinx.android.synthetic.main.fragment_news.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.NewsAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.News
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class NewsFragment:Fragment(R.layout.fragment_news) {

    lateinit var customProgressDialog: CustomProgressDialog
    private val viewModel by viewModels<AppViewModel>()
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var successFailedDialog: SuccessFailedDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        customProgressDialog = CustomProgressDialog(requireContext())

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){

                }
            }

        })

        newsAdapter = NewsAdapter(object : NewsAdapter.NewsAdapterCallBack {
            override fun onItemClick(news: News) {
                newsDetailsBottomSheet(news)
            }

        })

        news_recyclerview?.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }


        viewModel.newsResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    successFailedDialog.show()
                    successFailedDialog.setStatusImage(R.drawable.error)
                    successFailedDialog.setTitle(getString(R.string.tariflar))
                    successFailedDialog.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialog.setButtonText(getString(R.string.yopish))
                    successFailedDialog.showCloseButton(false)
                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        newsAdapter.differ.submitList(it)
                    }
                }
            }
        }

        viewModel.loadNews(SharedPref.token)

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

    private fun initToolbar(){
        toolbar_title.text = getString(R.string.yangiliklar)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
        toolbar_btn_back.visibility = View.INVISIBLE

    }



}