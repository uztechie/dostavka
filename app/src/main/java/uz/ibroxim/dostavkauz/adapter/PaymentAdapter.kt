
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
import kotlinx.android.synthetic.main.adapter_payment.view.*
import kotlinx.android.synthetic.main.adapter_tariff.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.Item
import uz.ibroxim.dostavkauz.models.Payment
import uz.ibroxim.dostavkauz.models.Tariff
import uz.ibroxim.dostavkauz.utils.Utils

class PaymentAdapter:RecyclerView.Adapter<PaymentAdapter.AdapterViewHolder>() {
    private  val TAG = "ItemAdapter"

   private val diffCallBack = object : DiffUtil.ItemCallback<Payment>(){
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class AdapterViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_payment, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            val payment = differ.currentList[position]
            holder.itemView.adapter_payment_amount.text = payment.amount
            holder.itemView.adapter_payment_type.text = payment.type_name
            holder.itemView.adapter_payment_date.text = Utils.reformatDateFromStringLocale(payment.created_at)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }




}