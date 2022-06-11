package uz.ibroxim.dostavkauz.fragments.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.ibroxim.dostavkauz.DriverActivity
import uz.ibroxim.dostavkauz.UserActivity
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.utils.Constants
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val TAG = "LoginFragment"

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var storedVerificationId: String? = ""
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var successFailedDialog: SuccessFailedDialog

    private val viewModel by viewModels<AppViewModel>()

    private val START_TIME = 120000L
    private var leftTime = START_TIME
    private var isTimerRunning = false
    var phone = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (viewModel.getUser().isNotEmpty()) {
            val user = viewModel.getUser()[0]
            SharedPref.token = "Token " + user.token

            Log.d(TAG, "onViewCreated: not empty ")
            if (user.type == Constants.USER_TYPE_DRIVER){
                startActivity(Intent(requireActivity(), DriverActivity::class.java))
                requireActivity().finish()
            }
            else {
                startActivity(Intent(requireActivity(), UserActivity::class.java))
                requireActivity().finish()
            }





        }

        customProgressDialog = CustomProgressDialog(requireContext())
        successFailedDialog = SuccessFailedDialog(requireContext(), object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS) {

                } else {

                }
            }

        })

        stepPhoneInputVisibility()

        login_btn_request_code.setOnClickListener {
            phone = login_et_phone.text.toString()
            val phoneWithPlus = "+$phone"

            Log.d(TAG, "onViewCreated: phone number " + phone)

            if (phone.length <= 10) {
                Utils.toastIconError(requireActivity(), "Iltimos telefon raqamini to'liq kiriting")
                return@setOnClickListener
            }

            customProgressDialog.show()
            startPhoneNumberVerification(phoneWithPlus)

        }


        login_btn_login.setOnClickListener {
            val code = login_et_code.text.toString()
            if (code.length <= 5) {
                Utils.toastIconError(
                    requireActivity(),
                    getString(R.string.iltimos_tasdiqlash_kodini_toliq_kiriting)
                )
                return@setOnClickListener
            }
            customProgressDialog.show()
            verifyPhoneNumberWithCode(storedVerificationId, code)
        }

        login_tv_change_phone.setOnClickListener {
            stepPhoneInputVisibility()
        }

        login_tv_request_sms_code.setOnClickListener {
            val phone = login_et_phone.text.toString()
            resendVerificationCode(phone, resendToken)
            customProgressDialog.show()
        }



        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                val code = credential.smsCode
                login_et_code.setText(code)

//                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                customProgressDialog.dismiss()
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.


                Log.d(TAG, "onCodeSent:$verificationId")
                resetTimer()
                startTimer()
                stepCodeInputVisibility()
                customProgressDialog.dismiss()

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }


    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }


    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    checkPhoneFromServer()

                    val user = task.result?.user
                } else {
                    customProgressDialog.dismiss()
                    // Sign in failed, display a message and update the UI
                    Utils.toastIconError(requireActivity(), task.exception?.message)
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Utils.toastIconError(
                            requireActivity(),
                            getString(R.string.siz_kiritgan_kod_xato)
                        )
                    }
                }
            }
    }

    private fun checkPhoneFromServer() {

        Log.d(TAG, "checkPhoneFromServer: phone "+phone)

        viewModel.loginCustomerResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    Log.d(TAG, "checkPhoneFromServer: error " + it.message)
                    customProgressDialog.dismiss()
                    it.message?.let { message ->
                        Utils.toastIconError(requireActivity(), message)
                    }

                }
                is Resource.Success -> {
                    Log.d(TAG, "checkPhoneFromServer: success ")
                    customProgressDialog.dismiss()

                    it.data?.let { login ->
                        if (login.status == 200) {
                            login.data?.let { user ->

                                lifecycleScope.launch {
                                    Log.d(TAG, "checkPhoneFromServer: user " + user)
                                    viewModel.insertUser(user)
                                    delay(500)
                                }.invokeOnCompletion {
                                    SharedPref.token = "Token " + user.token

                                    if (user.type == Constants.USER_TYPE_DRIVER){
                                        startActivity(Intent(requireActivity(), DriverActivity::class.java))
                                        requireActivity().finish()
                                    }
                                    else{
                                        startActivity(Intent(requireActivity(), UserActivity::class.java))
                                        requireActivity().finish()
                                    }

                                }
                            }


                        } else if (login.status == 404) {
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                                    phone
                                )
                            )
                        } else {
                            Utils.toastIconError(requireActivity(), login.message)
                        }
                    }

                }
            }
        }
        Log.d(TAG, "checkPhoneFromServer: phoneee "+phone)
        viewModel.loginCustomer(phone)
    }


    private fun startTimer() {
        countDownTimer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                isTimerRunning = false
                login_tv_change_phone.visibility = View.VISIBLE
                login_tv_request_sms_code.visibility = View.VISIBLE
            }

        }.start()
        isTimerRunning = true
    }

    private fun updateTimer() {
        val minute = leftTime / 1000 / 60
        val seconds = leftTime / 1000 % 60

        val formattedTime = String.format("%02d:%02d", minute, seconds)
        login_timer.text = formattedTime
    }

    private fun pauseTimer() {
        try {
            countDownTimer.cancel()
            isTimerRunning = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun resetTimer() {
        leftTime = START_TIME
        updateTimer()
    }


    private fun stepPhoneInputVisibility() {
        login_et_phone.visibility = View.VISIBLE
        login_btn_request_code.visibility = View.VISIBLE
        login_et_code.visibility = View.INVISIBLE
        login_btn_login.visibility = View.INVISIBLE
        login_tv_request_sms_code.visibility = View.INVISIBLE
        login_tv_change_phone.visibility = View.INVISIBLE
        login_tv_description.visibility = View.INVISIBLE
        login_et_phone.isEnabled = true
    }


    private fun stepCodeInputVisibility() {
        login_et_phone.visibility = View.VISIBLE
        login_btn_request_code.visibility = View.INVISIBLE
        login_et_code.visibility = View.VISIBLE
        login_btn_login.visibility = View.VISIBLE
        login_tv_request_sms_code.visibility = View.INVISIBLE
        login_tv_change_phone.visibility = View.INVISIBLE
        login_tv_description.visibility = View.VISIBLE
        login_et_phone.isEnabled = false

        login_tv_description.text =
            "${getString(R.string.tasdiqlash_kodi_ushbu_raqamga_yuborildi)} \n$phone"

    }


    override fun onDestroyView() {
        super.onDestroyView()
        pauseTimer()
    }


}