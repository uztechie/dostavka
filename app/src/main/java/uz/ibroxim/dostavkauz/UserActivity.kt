package uz.ibroxim.dostavkauz

import android.Manifest
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.vmadalin.easypermissions.models.PermissionRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.utils.Resource
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.ibroxim.dostavkauz.utils.Utils
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class UserActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val viewModel by viewModels<AppViewModel>()


    private lateinit var successFailedDialog: SuccessFailedDialog

    private val  REQUEST_CODE = 222
    private val TAG = "MainActivity"

    lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        navController = navHostFragment.findNavController()

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val id = destination.id
            if (id == R.id.loginFragment || id == R.id.registerFragment || id == R.id.chooseLocationFragment ||
                    id == R.id.uploadCustomerPassportFragment || id == R.id.passportInfoGuideFragment){
                bottomNavigationView.visibility = View.GONE
            }
            else{
                bottomNavigationView.visibility = View.VISIBLE
            }
        }

        successFailedDialog = SuccessFailedDialog(this, object :
            SuccessFailedDialog.SuccessFailedCallback {
            override fun onActionButton1Click(clickAction: String) {
                if (clickAction == SuccessFailedDialog.ACTION_SUCCESS){
                    finish()
                }
            }

            override fun onActionButton2Click(clickAction: String) {

            }

        })

        requestPermissions()
        Utils.getLastKnownLocation(this)





    }

    fun getMainViewModel():AppViewModel{
        return viewModel
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
            successFailedDialog.setButton1Text(getString(R.string.yopish))
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
                                this@UserActivity,
                                LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: SendIntentException) {
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


    private fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_NETWORK_STATE
                ) != PackageManager.PERMISSION_GRANTED
            )
                return


            var locationGPS: Location? = null
            var locationNetwork: Location? = null

            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            //Add try catch because this returns null for first time installation of the app
            try {
                locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } catch (e: Exception) {
                Log.d(TAG,"GPS Location is null")
            }

            try {
                locationNetwork =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } catch (e: Exception) {
                Log.d(TAG,"Network Location is null")
            }


            var isWithLocation: Boolean = false
            if (locationGPS != null) {
                if (isGPSEnabled) {
                    Log.d(TAG,"GPS - GPS Enabled")
                    SharedPref.latitude = locationGPS.latitude.toString()
                    SharedPref.longitude = locationGPS.longitude.toString()
                    isWithLocation = true
                }
            }

            if (!isWithLocation) {
                if (locationNetwork != null) {
                    if (isNetworkEnabled) {
                        Log.d(TAG,"Network - Network Enabled")

                        SharedPref.latitude = locationGPS?.latitude.toString()
                        SharedPref.longitude = locationGPS?.longitude.toString()

                        isWithLocation = true
                    }
                }
            }

            if (!isWithLocation) {
                SharedPref.latitude = "0.0"
                SharedPref.longitude = "0.0"
            }
        }

        Log.d(TAG,"getAppLocation latitude " + SharedPref.latitude)
        Log.d(TAG,"getAppLocation longitude " + SharedPref.longitude)


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

