package uz.ibroxim.dostavkauz.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.dialog_success_failed.*
import uz.ibroxim.dostavkauz.R

class SuccessFailedDialog(context:Context, private val callback:SuccessFailedCallback):Dialog(context, R.style.AlertDialogTheme) {


    var mClickAction = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
        setContentView(R.layout.dialog_success_failed)

        dialog_success_btn_action1.setOnClickListener {
            callback.onActionButton1Click(mClickAction)
            dismiss()
        }

        dialog_success_btn_action2.setOnClickListener {
            callback.onActionButton2Click(mClickAction)
            dismiss()
        }

        dialog_success_close_btn_close.setOnClickListener {
            dismiss()
        }

    }


    fun setStatusImage(imageId:Int){
        dialog_success_close_img_status.setImageDrawable(ContextCompat.getDrawable(context, imageId))
    }
    fun setTitle(title:String){
        dialog_success_close_tv_title.text = title
    }
    fun setMessage(message:String){
        dialog_success_close_tv_message.text = message
    }
    fun setButton1Text(text:String){
        dialog_success_btn_action1.setText(text)
    }
    fun setButton2Text(text:String){
        dialog_success_btn_action2.setText(text)
    }

    fun showButton2(show:Boolean){
        dialog_success_btn_action2.isVisible = show
    }

    fun showCloseButton(isVisible:Boolean){
        if (isVisible){
            dialog_success_close_btn_close.visibility = View.VISIBLE
        }
        else{
            dialog_success_close_btn_close.visibility = View.INVISIBLE
        }
    }
    fun setClickAction(action:String){
        mClickAction = action
    }


    interface SuccessFailedCallback{
        fun onActionButton1Click(clickAction:String)
        fun onActionButton2Click(clickAction:String)
    }

    companion object{
        val ACTION_SUCCESS = "ACTION_SUCCESS"
        val ACTION_FAILED = "ACTION_FAILED"
    }

}