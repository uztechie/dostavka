
package uz.ibroxim.dostavkauz.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter__driver_item.view.*
import kotlinx.android.synthetic.main.adapter_item.view.*
import kotlinx.android.synthetic.main.adapter_tariff.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.models.Tariff

class DriverItemAdapter(val callback:DriverItemAdapterCallBack):RecyclerView.Adapter<DriverItemAdapter.AdapterViewHolder>() {
    private  val TAG = "ItemAdapter"

   private val diffCallBack = object : DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class AdapterViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        init {
            itemView.adapter_driver_item_options.setOnClickListener {
                callback.onOptionsClick(differ.currentList[adapterPosition], it)
            }

            itemView.setOnClickListener {
                callback.onOptionsClick(differ.currentList[adapterPosition], it)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter__driver_item, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            val item = differ.currentList[position]
            holder.itemView.adapter_driver_item_name.setText(item.name)
            holder.itemView.adapter_driver_item_amount.setText(item.amount.toString())
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface DriverItemAdapterCallBack{
        fun onOptionsClick(item: Item, view:View)
    }


}