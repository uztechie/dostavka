
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
import kotlinx.android.synthetic.main.adapter_new_order_item.view.*
import kotlinx.android.synthetic.main.adapter_tariff.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.models.Tariff

class NewOrderItemAdapter:RecyclerView.Adapter<NewOrderItemAdapter.AdapterViewHolder>() {
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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_new_order_item, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            val item = differ.currentList[position]
            holder.itemView.adapter_new_order_item_title.setText(item.name)
            holder.itemView.adapter_new_order_item_amount.setText(item.amount.toString())
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}