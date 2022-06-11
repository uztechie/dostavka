package uz.ibroxim.dostavkauz

import android.Manifest
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.vmadalin.easypermissions.models.PermissionRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    val viewModel by viewModels<AppViewModel>()
    lateinit var navController: NavController
    private val TAG = "LoginActivity"


    private lateinit var successFailedDialog: SuccessFailedDialog
    private val  REQUEST_CODE = 222

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_luncher)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentContainerLauncher) as NavHostFragment
        navController = navHostFragment.findNavController()

        successFailedDialog = SuccessFailedDialog(this, object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButtonClick(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){
                    finish()
                }
            }

        })

        requestPermissions()
        Utils.getLastKnownLocation(this)

    }


    private fun requestPermissions(){
        val request = PermissionRequest.Builder(this)
            .code(REQUEST_CODE)
            .perms(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

            ))
            .build()
        EasyPermissions.requestPermissions(this, request)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            SettingsDialog.Builder(this).build().show()
        }
        else{
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsGranted: ")
        if (Utils.isNetworkAvailable(this)){
            requestToTurnOnGPS()
        }
        else{
            successFailedDialog.show()
            successFailedDialog.setStatusImage(R.drawable.wifi_off)
            successFailedDialog.setTitle(getString(R.string.internet_boglanish))
            successFailedDialog.setMessage(getString(R.string.ilovadan_foydalanish_uchun_internetga))
            successFailedDialog.setButtonText(getString(R.string.yopish))
            successFailedDialog.showCloseButton(false)
            successFailedDialog.setClickAction(SuccessFailedDialog.ACTION_SUCCESS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun requestToTurnOnGPS() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(
                    ApiException::class.java
                )
                // All location settings are satisfied. The client can initialize location
                // requests here.
                Log.d(TAG,"getLocation2 success")
                Utils.getLastKnownLocation(this)

            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                             // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this@LoginActivity,
                                LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }



    private fun loadRegions(){
        viewModel.regions.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    response.message?.let {

                    }

                }
                is Resource.Success -> {
                    response.data?.let {
                        Log.d(TAG, "regionBottomSheet: success submitted " + it)
                        viewModel.insertRegions(it)
                    }
                }
            }
        }
        viewModel.loadRegions()
    }
    private fun loadDistricts(){
        viewModel.districts.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    response.message?.let {
                    }

                }
                is Resource.Success -> {
                    response.data?.let {
                        Log.d(TAG, "regionBottomSheet: success submitted " + it)
                        viewModel.insertDistricts(it)
                    }
                }
            }
        }
        viewModel.loadDistricts()
    }
    private fun loadQuarters(){
        viewModel.quarters.observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                }
                is Resource.Error -> {

                    response.message?.let {

                    }

                }
                is Resource.Success -> {
                    response.data?.let {
                        Log.d(TAG, "regionBottomSheet: success submitted " + it)
                        viewModel.insertQuarters(it)
                    }
                }
            }
        }
        viewModel.loadQuarters()
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main).launch {
            loadDistricts()
            loadQuarters()
            loadRegions()
        }

    }

}