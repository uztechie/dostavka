package uz.ibroxim.dostavkauz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_new_order.view.*
import kotlinx.android.synthetic.main.adapter_postal_history.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.Order
import uz.ibroxim.dostavkauz.models.PostalHistory
import uz.ibroxim.dostavkauz.utils.StatusList
import uz.ibroxim.dostavkauz.utils.Utils

class NewOrderAdapter(val status:Int, val context:Context, val callBack: NewOrderAdapterCallBack):RecyclerView.Adapter<NewOrderAdapter.AdapterViewHolder>() {

   private val diffCallBack = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_new_order, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            val order = differ.currentList[position]
            holder.itemView.adapter_new_order_full_name.text = order.sender_full_name
            holder.itemView.adapter_new_order_address.text = order.sender_address?.sender_address_name
            holder.itemView.adapter_new_order_phone.text = order.sender_phone
            holder.itemView.adapter_new_order_date.text = Utils.reformatDateFromStringLocale(order.created_at)
            if (status == StatusList.Accepted.id){
                holder.itemView.adapter_new_order_status.text = StatusList.Accepted.title
            }
            else{
                holder.itemView.adapter_new_order_status.text = context.getString(R.string.yangi)
            }

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface NewOrderAdapterCallBack{
        fun onItemClick(order: Order)
    }


}