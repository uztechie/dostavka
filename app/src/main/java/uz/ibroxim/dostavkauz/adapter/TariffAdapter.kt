package uz.ibroxim.dostavkauz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_region.view.*
import kotlinx.android.synthetic.main.adapter_tariff.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.databinding.AdapterRegionBinding
import uz.ibroxim.dostavkauz.databinding.AdapterTariffBinding
import uz.ibroxim.dostavkauz.models.Tariff
import uz.techie.mexmash.models.District

class TariffAdapter(val callBack: TariffAdapterCallBack):RecyclerView.Adapter<TariffAdapter.AdapterViewHolder>() {

   private val diffCallBack = object : DiffUtil.ItemCallback<Tariff>(){
        override fun areItemsTheSame(oldItem: Tariff, newItem: Tariff): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tariff, newItem: Tariff): Boolean {
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_tariff, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            holder.itemView.adapter_tariff_title.text = differ.currentList[position].name
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface TariffAdapterCallBack{
        fun onItemClick(tariff: Tariff)
    }


}