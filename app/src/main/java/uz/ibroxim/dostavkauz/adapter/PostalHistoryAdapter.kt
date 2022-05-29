package uz.ibroxim.dostavkauz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.adapter_news.view.*
import kotlinx.android.synthetic.main.adapter_postal_history.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.News
import uz.ibroxim.dostavkauz.models.PostalHistory
import uz.ibroxim.dostavkauz.utils.Utils

class PostalHistoryAdapter(val callBack: PostalHistoryAdapterCallBack):RecyclerView.Adapter<PostalHistoryAdapter.AdapterViewHolder>() {

   private val diffCallBack = object : DiffUtil.ItemCallback<PostalHistory>(){
        override fun areItemsTheSame(oldItem: PostalHistory, newItem: PostalHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostalHistory, newItem: PostalHistory): Boolean {
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_postal_history, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            val history = differ.currentList[position]
            holder.itemView.adapter_postal_history_id.text = history.barcode.toString()

            history.status?.let {
                if (history.status.isNotEmpty()){
                    holder.itemView.adapter_postal_history_status.text = history.status.last().name
                    holder.itemView.adapter_postal_history_status.visibility = View.VISIBLE
                }
                else{
                    holder.itemView.adapter_postal_history_status.visibility = View.INVISIBLE
                }
            }

            holder.itemView.adapter_postal_history_date.text = Utils.reformatDateFromStringLocale(history.created_at)
            holder.itemView.adapter_postal_history_destination.text = history.receiver_address?.region_name

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface PostalHistoryAdapterCallBack{
        fun onItemClick(postalHistory: PostalHistory)
    }


}