package uz.ibroxim.dostavkauz.fragments.driver

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_play_audio.*
import kotlinx.android.synthetic.main.dialog_play_audio.update_audio_music_icon
import kotlinx.android.synthetic.main.dialog_update_user.*
import kotlinx.android.synthetic.main.fragment_create_mail_passport.*
import kotlinx.android.synthetic.main.fragment_driver_search_order.*
import kotlinx.android.synthetic.main.fragment_order_update_audio.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.AudioAdapter
import uz.ibroxim.dostavkauz.adapter.NewOrderItemAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.SearchOrder
import uz.ibroxim.dostavkauz.models.User
import uz.ibroxim.dostavkauz.models.UserRecord
import uz.ibroxim.dostavkauz.utils.*
import uz.techie.mexmash.data.AppViewModel
import java.io.IOException

@AndroidEntryPoint
class DriverSearchOrderFragment:Fragment(R.layout.fragment_driver_search_order) {
    private val TAG = "DriverSearchOrderFragme"

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var audioAdapter:AudioAdapter
    private lateinit var itemAdapter:NewOrderItemAdapter

    private var senderPassport = ""
    private var receiverPassport = ""


    val handler = Handler()
    lateinit var runnable: Runnable

    private lateinit var mediaPlayerStream: MediaPlayer



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayerStream = MediaPlayer()
        customProgressDialog = CustomProgressDialog(requireContext())
        itemAdapter = NewOrderItemAdapter()

        audioAdapter = AudioAdapter(object : AudioAdapter.AudioCallback {
            override fun onItemClick(userRecord: UserRecord) {
                showPlayAudioSheet(userRecord)
            }

        })

        search_recyclerview_items?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemAdapter
        }


        search_recyclerview_audios?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = audioAdapter
        }


        search_btn_search.setOnClickListener {
            val barcode = search_et_search.text.toString().trim()
            if (barcode.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.barcodeni_kiriting))
                return@setOnClickListener
            }

            searchByBarcode(barcode)
        }
        search_btn_scan.setOnClickListener {
            scanBarcode()
        }

        search_sender_passport.setOnClickListener {
            Log.d(TAG, "onViewCreated: search_sender_passport click "+senderPassport)
            findNavController().navigate(DriverSearchOrderFragmentDirections.actionDriverSearchOrderFragmentToPassportInfoGuideFragment2(senderPassport))
        }


        search_receiver_passport.setOnClickListener {
            Log.d(TAG, "onViewCreated: search_receiver_passport click "+receiverPassport)
            findNavController().navigate(DriverSearchOrderFragmentDirections.actionDriverSearchOrderFragmentToPassportInfoGuideFragment2(receiverPassport))
        }


        searchByBarcodeResponse()



    }

    private fun scanBarcode() {
        val scanOption = ScanOptions()


        scanOption.captureActivity = Capture::class.java
        scanOption.setOrientationLocked(false)
        scanOption.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        scanOption.setPrompt("Scanning Barcode")
        scanOption.setBeepEnabled(true)
        launchSomeActivity.launch(scanOption)
    }


    private fun searchByBarcode(barcode:String){
        Log.d(TAG, "searchByBarcode: barcode "+Constants.barcode)
        viewModel.searchByBarcode(barcode, SharedPref.token)
    }
    private fun searchByBarcodeResponse(){
        viewModel.searchByBarcodeResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    clearAllData()
                    customProgressDialog.dismiss()
                    Log.e(TAG, "searchByBarcode: Error "+response.message)
                    val title = getString(R.string.buyurtma_izlash)
                    val message = getString(R.string.xatolik)+" "+response.message
                    showSuccessFailedDialog(title, message, false)

                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            if (!it.data.isNullOrEmpty()){
                                val data = it.data[0]
                                Log.d(TAG, "searchByBarcode:  data  "+data)
                                audioAdapter.differ.submitList(data.recording)
                                itemAdapter.differ.submitList(data.items)

                                Log.d(TAG, "searchByBarcodeResponse: recordings "+data.recording)
                                Log.d(TAG, "searchByBarcodeResponse: items "+data.items)

                                fetchData(data)


                            }
                        }
                        else{
                            clearAllData()
                            Log.e(TAG, "searchByBarcode: Error "+it.message)
                            val title = getString(R.string.buyurtma_izlash)
                            val message = getString(R.string.xatolik)+" "+it.message
                            showSuccessFailedDialog(title, message, false)
                        }
                    }
                }
            }

        }
    }

    private fun clearAllData() {
        viewModel.searchByBarcodeResponse = MutableLiveData()
        search_tv_barcode.text = ""

        search_tv_sender_name.text = ""
        search_tv_sender_address.text = ""
        search_tv_sender_phone.text = ""

        search_tv_receiver_name.text = ""
        search_tv_receiver_phone.text = ""
        search_tv_receiver_address.text = ""

        itemAdapter.differ.submitList(emptyList())
        audioAdapter.differ.submitList(emptyList())

        Glide.with(this)
            .load(Constants.BASE_URL)
            .apply(Utils.options)
            .into(search_sender_passport)

        Glide.with(this)
            .load(Constants.BASE_URL)
            .apply(Utils.options)
            .into(search_receiver_passport)
    }

    private fun fetchData(data: SearchOrder) {
        search_tv_barcode.text = data.barcode

        search_tv_sender_name.text = data.sender_full_name
        search_tv_sender_phone.text = data.sender_phone
        var senderAddress = ""
        data.sender_address?.let {
            senderAddress = it.sender_address_name.toString()
        }
        search_tv_sender_address.text = senderAddress

        Glide.with(this)
            .load(Constants.BASE_URL+data.sender_passport?.image)
            .apply(Utils.options)
            .into(search_sender_passport)

        senderPassport = data.sender_passport?.image?:"d"
        receiverPassport = data.receiver_passport?.image?:"d"


        search_tv_receiver_name.text = data.receiver_full_name
        search_tv_receiver_phone.text = data.receiver_phone
        var receiverAddress = ""
        data.receiver_address?.let {
            receiverAddress = it.region_name+",  "+it.district_name+",  "+it.quarters_name+",  "+it.street
        }
        search_tv_receiver_address.text = receiverAddress

        Glide.with(this)
            .load(Constants.BASE_URL+data.receiver_passport?.image)
            .apply(Utils.options)
            .into(search_receiver_passport)



    }


    private fun showSuccessFailedDialog(title:String, message:String, isSuccess:Boolean){


        val successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {

            }

        })

        if (isSuccess){
            successFailedDialog.show()
            successFailedDialog.setStatusImage(R.drawable.success)
            successFailedDialog.setTitle(title)
            successFailedDialog.setMessage(message)
            successFailedDialog.setButtonText(getString(R.string.yopish))
            successFailedDialog.showCloseButton(false)
            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
        }
        else {
            successFailedDialog.show()
            successFailedDialog.setStatusImage(R.drawable.error)
            successFailedDialog.setTitle(title)
            successFailedDialog.setMessage(message)
            successFailedDialog.setButtonText(getString(R.string.yopish ))
            successFailedDialog.showCloseButton(true)
            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
        }
    }

    private fun showPlayAudioSheet(userRecord: UserRecord) {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_play_audio)
        dialog.show()
        dialog.play_audio_tv_title.text = Utils.reformatDateFromStringLocale(userRecord.created_at)
        dialog.play_audio_btn_close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            Log.d(TAG, "showPlayAudioSheet: dismissed ")
            try {
                handler.removeCallbacks(runnable)
                mediaPlayerStream.stop()
                mediaPlayerStream.reset()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }


        val url = Constants.BASE_URL+userRecord.audio

        try {
            handler.removeCallbacks(runnable)
        }catch (e:Exception){
            e.printStackTrace()
        }


        mediaPlayerStream.setAudioStreamType(AudioManager.STREAM_MUSIC)

        runnable = object : Runnable {
            override fun run() {
                handler.postDelayed(this, 500)
                dialog.play_audio_seekbar.setProgress(mediaPlayerStream.currentPosition)
            }

        }

        if (mediaPlayerStream.isPlaying){
            mediaPlayerStream.stop()
            mediaPlayerStream.reset()
        }

        mediaPlayerStream.setDataSource(url)
        mediaPlayerStream.prepareAsync()



        dialog.play_audio_progress.visibility = View.VISIBLE
        dialog.play_audio_btn_play.visibility = View.INVISIBLE
        dialog.play_audio_btn_pause.visibility = View.INVISIBLE


        mediaPlayerStream.setOnPreparedListener {

            dialog.play_audio_progress.visibility = View.INVISIBLE
            dialog.play_audio_btn_play.visibility = View.INVISIBLE
            dialog.play_audio_btn_pause.visibility = View.VISIBLE

            mediaPlayerStream.start()
            dialog.play_audio_seekbar.max = mediaPlayerStream.duration
            handler.postDelayed(runnable, 0)
        }


        dialog.play_audio_btn_play.setOnClickListener {
            dialog.play_audio_progress.visibility = View.INVISIBLE
            dialog.play_audio_btn_play.visibility = View.INVISIBLE
            dialog.play_audio_btn_pause.visibility = View.VISIBLE
            handler.postDelayed(runnable, 0)
            mediaPlayerStream.start()

        }

        dialog.play_audio_btn_pause.setOnClickListener {
            dialog.play_audio_progress.visibility = View.INVISIBLE
            dialog.play_audio_btn_play.visibility = View.VISIBLE
            dialog.play_audio_btn_pause.visibility = View.INVISIBLE
            mediaPlayerStream.pause()

            handler.removeCallbacks(runnable)

        }

        dialog.play_audio_seekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    mediaPlayerStream.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        mediaPlayerStream.setOnCompletionListener {
            dialog.play_audio_progress.visibility = View.INVISIBLE
            dialog.play_audio_btn_play.visibility = View.VISIBLE
            dialog.play_audio_btn_pause.visibility = View.INVISIBLE
            mediaPlayerStream.seekTo(0)
        }











    }


    var launchSomeActivity:ActivityResultLauncher<ScanOptions> = registerForActivityResult(ScanContract()) { intentResult->
        Log.d(TAG, "launchSomeActivity: "+intentResult.contents)
        if (intentResult.contents != null){
            Log.d(TAG, "onActivityResult: barcode "+intentResult.contents)
                searchByBarcode(intentResult.contents)
        }
        else{
            val title = getString(R.string.skaner_qilish)
            val message = getString(R.string.barcode_skaner_qilinmadi)
            showSuccessFailedDialog(title, message, false)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayerStream.release()
    }

}