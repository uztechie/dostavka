
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
import kotlinx.android.synthetic.main.adapter_item.view.*
import kotlinx.android.synthetic.main.adapter_tariff.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.models.Tariff

class ItemAdapter(val callback:ItemAdapterCallBack, var itemsList:MutableList<Item>):RecyclerView.Adapter<ItemAdapter.AdapterViewHolder>() {
    private  val TAG = "ItemAdapter"

   private val diffCallBack = object : DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.name == newItem.name && oldItem.amount == newItem.amount
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class AdapterViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        init {
            itemView.adapter_item_delete.setOnClickListener {
//                callback.onItemRemove(differ.currentList[adapterPosition])
                itemsList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }

            itemView.adapter_item_name.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    var item = itemsList[adapterPosition]
                    item.name = p0.toString().trim()
                    itemsList.set(adapterPosition, item)

                    Log.d(TAG, "onTextChanged: name "+item)
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })

            itemView.adapter_item_amount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    var item = itemsList[adapterPosition]
                    item.amount = p0.toString().trim()
                    itemsList.set(adapterPosition, item)

                    Log.d(TAG, "onTextChanged: amount "+item)
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            val item = itemsList[position]
            holder.itemView.adapter_item_name.setText(item.name)
            holder.itemView.adapter_item_amount.setText(item.amount.toString())
        }





    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun getList():MutableList<Item>{
        return itemsList
    }
    fun submitList(newItemsList:MutableList<Item>){
        itemsList =newItemsList
        notifyDataSetChanged()
    }

    interface ItemAdapterCallBack{
        fun onItemRemove(item: Item)
    }


}