package uz.ibroxim.dostavkauz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_region.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.databinding.AdapterRegionBinding
import uz.techie.mexmash.models.District
import uz.techie.mexmash.models.Region

class DistrictAdapter(val callBack: DistrictAdapterCallBack):RecyclerView.Adapter<DistrictAdapter.RegionViewHolder>() {

   private val diffCallBack = object : DiffUtil.ItemCallback<District>(){
        override fun areItemsTheSame(oldItem: District, newItem: District): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: District, newItem: District): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class RegionViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                callBack.onItemClick(differ.currentList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_region, parent, false)
        return RegionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        if (position>=0){
            holder.itemView.adapter_region_title.text = differ.currentList[position].name
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface DistrictAdapterCallBack{
        fun onItemClick(district:District)
    }


}