package uz.ibroxim.dostavkauz.fragments.driver

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_order_update_audio.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.adapter.AudioAdapter
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.models.UserRecord
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel
import java.io.File

@AndroidEntryPoint
class OrderUpdateAudioFragment : Fragment(R.layout.fragment_order_update_audio) {
    private val TAG = "OrderUpdateAudioFragmen"

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var successFailedDialog: SuccessFailedDialog
    private var fileUri: Uri? = null
    private lateinit var audioAdapter: AudioAdapter

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerStream: MediaPlayer

    val handler = Handler()
    lateinit var runnable: Runnable



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customProgressDialog = CustomProgressDialog(requireContext())
        mediaPlayer = MediaPlayer()
        mediaPlayerStream = MediaPlayer()

        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){

                }
            }

            override fun onActionButton2Click(clickAction: String) {

            }
        })


        audioAdapter = AudioAdapter(object : AudioAdapter.AudioCallback {
            override fun onItemClick(userRecord: UserRecord) {
                streamAudio(Constants.BASE_URL+userRecord.audio)
                update_audio_tv_title.text = Utils.reformatDateFromStringLocale(userRecord.created_at?:"")
            }

        })

        update_audio_recyclerview?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = audioAdapter
        }

        searchByBarcode()
        searchByBarcodeResponse()




        update_audio_btn_select.setOnClickListener {
            try {
                val photoPickerIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
//                val photoPickerIntent = Intent()
//                photoPickerIntent.action = Intent.ACTION_GET_CONTENT
                photoPickerIntent.type = "audio/*"
                launchSomeActivity.launch(photoPickerIntent)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }

        playAudio()

        update_audio_btn_save.setOnClickListener {

            if (fileUri == null){
                Utils.toastIconError(requireActivity(), getString(R.string.audio_tanlang))
                return@setOnClickListener
            }
            uploadAudio()
        }

        uploadAudioResponse()

    }

    private fun searchByBarcode(){
        Log.d(TAG, "searchByBarcode: barcode "+Constants.barcode)
        viewModel.searchByBarcode(Constants.barcode, SharedPref.token)
    }
    private fun searchByBarcodeResponse(){
        viewModel.searchByBarcodeResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Utils.toastIconError(requireActivity(), response.message)
                    Log.e(TAG, "searchByBarcode: Error "+response.message)
                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            if (!it.data.isNullOrEmpty()){
                                val data = it.data[0]
                                Log.d(TAG, "searchByBarcode:  data  "+data)
                                audioAdapter.differ.submitList(data.recording)
                            }
                        }
                        else{
                            Log.e(TAG, "searchByBarcode: Error "+it.message)
                            Utils.toastIconError(requireActivity(), it.message)
                        }
                    }
                }
            }

        }
    }

    private fun uploadAudio() {


        val file = File(getPath(fileUri))
        Log.d(TAG, "uploadAudio: file "+file)

        val filePart = file.asRequestBody("audio/*".toMediaType())
        val audio = MultipartBody.Part.createFormData("audio", file.name, filePart)

        Log.d(TAG, "uploadAudio: postalId "+Constants.orderId)

        viewModel.uploadAudio(Constants.orderId, audio)

    }

    private fun uploadAudioResponse(){
        viewModel.uploadAudioResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    Log.e(TAG, "uploadAudioResponse: Error "+response.message)

                    successFailedDialog.show()
                    successFailedDialog.setStatusImage(R.drawable.error)
                    successFailedDialog.setTitle(getString(R.string.audio_yuklash))
                    successFailedDialog.setMessage(response.message?:getString(R.string.xatolik))
                    successFailedDialog.setButton1Text(getString(R.string.yopish))
                    successFailedDialog.showCloseButton(true)
                    successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)

                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.success)
                            successFailedDialog.setTitle(getString(R.string.audio_yuklash))
                            successFailedDialog.setMessage(getString(R.string.audio_yuklandi))
                            successFailedDialog.setButton1Text(getString(R.string.yopish))
                            successFailedDialog.showCloseButton(false)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
                            searchByBarcode()

                        }
                        else{
                            Log.e(TAG, "searchByBarcode: Error "+it.message)
                            successFailedDialog.show()
                            successFailedDialog.setStatusImage(R.drawable.error)
                            successFailedDialog.setTitle(getString(R.string.audio_yuklash))
                            successFailedDialog.setMessage(it.message?:getString(R.string.xatolik))
                            successFailedDialog.setButton1Text(getString(R.string.yopish))
                            successFailedDialog.showCloseButton(true)
                            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_FAILED)
                        }
                    }
                }
            }

        }
    }

    var launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // do your operation from here....

            result.data?.data?.let {
                fileUri = it
                update_audio_layout.visibility = View.VISIBLE
                playAudio()
                requireContext().contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    Log.d(TAG, "curson: " + cursor.columnNames)
                    Log.d(TAG, "curson: " + cursor.columnCount)

                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()

                    val fileName = cursor.getString(nameIndex)
//                    var fileSize = cursor.getLong(sizeIndex)
//                    fileSize = fileSize/1024/1024

                    update_audio_tv_title.text = fileName
                    update_audio_tv_title.visibility = View.VISIBLE

                }

            }

            if (data?.data == null){
                fileUri = null
                update_audio_layout.visibility = View.GONE
                playAudio()
            }

        }
    }

    fun getPath(uri: Uri?): String? {
        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor = requireActivity().managedQuery(uri, projection, null, null, null)
            val column_index: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }catch (e:Exception){
            return ""
        }

    }

    private fun playAudio() {

        try {
            handler.removeCallbacks(runnable)
        }catch (e:Exception){
            e.printStackTrace()
        }

        if (mediaPlayerStream.isPlaying){
            mediaPlayerStream.stop()
            mediaPlayerStream.reset()
        }


        fileUri?.let {
            Log.d(TAG, "playAudio: file in not null ")
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(requireContext(), it)
            mediaPlayer.prepare()
            update_audio_seekbar.progress = 0
            update_audio_btn_play.visibility = View.VISIBLE
            update_audio_btn_pause.visibility = View.GONE

            update_audio_seekbar.max = mediaPlayer.duration



            runnable = object : Runnable {
                override fun run() {
                    update_audio_seekbar.setProgress(mediaPlayer.currentPosition)
                    handler.postDelayed(this, 500)
                }

            }



            update_audio_btn_play.setOnClickListener {
                update_audio_btn_play.visibility = View.GONE
                update_audio_btn_pause.visibility = View.VISIBLE
                mediaPlayer.start()
                update_audio_seekbar.max = mediaPlayer.duration

                handler.postDelayed(runnable, 0)

            }

            update_audio_btn_pause.setOnClickListener {
                update_audio_btn_play.visibility = View.VISIBLE
                update_audio_btn_pause.visibility = View.GONE
                mediaPlayer.pause()

                handler.removeCallbacks(runnable)

            }

            update_audio_seekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        mediaPlayer.seekTo(p1)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })

            mediaPlayer.setOnCompletionListener {
                update_audio_btn_play.visibility = View.VISIBLE
                update_audio_btn_pause.visibility = View.GONE
                mediaPlayer.seekTo(0)
            }

        }






    }

    private fun streamAudio(url:String) {

        fileUri = null

        try {
            handler.removeCallbacks(runnable)
        }catch (e:Exception){
            e.printStackTrace()
        }

        if (mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.reset()
        }

        mediaPlayerStream.setAudioStreamType(AudioManager.STREAM_MUSIC)

        runnable = object : Runnable {
            override fun run() {
                handler.postDelayed(this, 500)
                update_audio_seekbar.setProgress(mediaPlayerStream.currentPosition)
            }

        }

        Log.d(TAG, "playAudio: file in not null ")
        mediaPlayerStream.stop()
        mediaPlayerStream.reset()
        mediaPlayerStream.setDataSource(url)
        mediaPlayerStream.prepareAsync()

        update_audio_progress.visibility = View.VISIBLE
        update_audio_music_icon.visibility = View.GONE


        mediaPlayerStream.setOnPreparedListener {
            update_audio_progress.visibility = View.GONE
            update_audio_music_icon.visibility = View.VISIBLE

            update_audio_btn_play.visibility = View.GONE
            update_audio_btn_pause.visibility = View.VISIBLE

            mediaPlayerStream.start()
            update_audio_seekbar.max = mediaPlayerStream.duration
            handler.postDelayed(runnable, 0)
        }

        update_audio_seekbar.max = mediaPlayerStream.duration



        update_audio_btn_play.setOnClickListener {
            update_audio_btn_play.visibility = View.GONE
            update_audio_btn_pause.visibility = View.VISIBLE
            mediaPlayerStream.start()
            update_audio_seekbar.max = mediaPlayerStream.duration

            handler.postDelayed(runnable, 0)

        }

        update_audio_btn_pause.setOnClickListener {
            update_audio_btn_play.visibility = View.VISIBLE
            update_audio_btn_pause.visibility = View.GONE
            mediaPlayerStream.pause()

            handler.removeCallbacks(runnable)

        }

        update_audio_seekbar.setOnSeekBarChangeListener(object :
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
            update_audio_btn_play.visibility = View.VISIBLE
            update_audio_btn_pause.visibility = View.GONE
            mediaPlayerStream.seekTo(0)
        }







    }


    override fun onDestroyView() {

        try {
            mediaPlayer.release()
            mediaPlayerStream.release()
            handler.removeCallbacks(runnable)
        }catch (e:Exception){
            e.printStackTrace()
        }


        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ddddsds")
    }



}