package uz.ibroxim.dostavkauz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.adapter_news.view.*
import kotlinx.android.synthetic.main.adapter_status.view.*
import kotlinx.android.synthetic.main.adapter_tariff.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.News
import uz.ibroxim.dostavkauz.models.PostalStatus
import uz.ibroxim.dostavkauz.models.Tariff
import uz.ibroxim.dostavkauz.utils.Utils

class StatusAdapter :RecyclerView.Adapter<StatusAdapter.AdapterViewHolder>() {

   private val diffCallBack = object : DiffUtil.ItemCallback<PostalStatus>(){
        override fun areItemsTheSame(oldItem: PostalStatus, newItem: PostalStatus): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostalStatus, newItem: PostalStatus): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class AdapterViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_status, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            holder.itemView.adapter_status_title.text = differ.currentList[position].name
            holder.itemView.adapter_status_date.text = Utils.reformatDateOnlyFromStringLocale(differ.currentList[position].created_at)

            if (position == 0){
                holder.itemView.line_top.visibility = View.INVISIBLE
            }
            else{
                holder.itemView.line_top.visibility = View.VISIBLE
            }

            if (position == differ.currentList.size-1){
                holder.itemView.line_bottom.visibility = View.INVISIBLE
            }
            else{
                holder.itemView.line_bottom.visibility = View.VISIBLE
            }


        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }




}