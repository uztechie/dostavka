package uz.ibroxim.dostavkauz.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.adapter_news.view.*
import kotlinx.android.synthetic.main.adapter_tariff.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.News
import uz.ibroxim.dostavkauz.models.Tariff
import uz.ibroxim.dostavkauz.utils.Utils

class NewsAdapter(val callBack: NewsAdapterCallBack):RecyclerView.Adapter<NewsAdapter.AdapterViewHolder>() {

   private val diffCallBack = object : DiffUtil.ItemCallback<News>(){
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class AdapterViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                callBack.onItemClick(differ.currentList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_news, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            holder.itemView.adapter_news_tv_title.text = Html.fromHtml(differ.currentList[position].name)
            holder.itemView.adapter_news_tv_date.text = Utils.reformatDateFromStringLocale(differ.currentList[position].updated_at)

            Glide.with(holder.itemView.adapter_news_image)
                .load(differ.currentList[position].image)
                .apply(Utils.options)
                .into(holder.itemView.adapter_news_image)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface NewsAdapterCallBack{
        fun onItemClick(news: News)
    }


}