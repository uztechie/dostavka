package uz.ibroxim.dostavkauz.adapter

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_audio.view.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.models.UserRecord
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.Utils

class AudioAdapter(val callback:AudioCallback):RecyclerView.Adapter<AudioAdapter.AdapterViewHolder>() {
    val mediaPlayer = MediaPlayer()
    var currentPlayingPosition = -1

    private val TAG = "AudioAdapter"

    private val diffCallBack = object : DiffUtil.ItemCallback<UserRecord>(){
        override fun areItemsTheSame(oldItem: UserRecord, newItem: UserRecord): Boolean {
            return oldItem.created_at == newItem.created_at
        }

        override fun areContentsTheSame(oldItem: UserRecord, newItem: UserRecord): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class AdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                callback.onItemClick(differ.currentList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_audio, parent, false)
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        if (position>=0){
            val audio = differ.currentList[position]
            holder.itemView.adapter_audio_duration.text = Utils.reformatDateFromStringLocale(audio.created_at?:"")


        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface AudioCallback{
        fun onItemClick(userRecord:UserRecord)
    }


}