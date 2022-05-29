package uz.ibroxim.dostavkauz.dialog

import android.app.Activity
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_dialog_phone.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.utils.Utils

class PhoneBottomDialog(val activity: Activity, private val callback:PhoneBottomCallback):BottomSheetDialog(activity, R.style.bottomSheetStyle) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setContentView(R.layout.bottom_dialog_phone)

        bottom_dialog_phone_btn_close.setOnClickListener {
            dismiss()
            callback.onDismissButtonClick()
        }


        bottom_dialog_phone_btn_next.setOnClickListener {
            val phone = bottom_dialog_phone_et_phone.text.trim().toString()
            if (phone.length<8){
                Utils.toastIconError(activity, activity.getString(R.string.telefon_raqamni_toliq_kiriting))
                return@setOnClickListener
            }
            dismiss()
            callback.onNextButtonClick(phone)

        }

    }




    interface PhoneBottomCallback{
        fun onNextButtonClick(phone:String)
        fun onDismissButtonClick()
    }


}