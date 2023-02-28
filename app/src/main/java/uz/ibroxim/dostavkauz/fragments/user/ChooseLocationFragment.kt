package uz.ibroxim.dostavkauz.fragments.user

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_choose_location.*
import kotlinx.coroutines.*
import uz.ibroxim.dostavkauz.R
import uz.ibroxim.dostavkauz.dialog.CustomProgressDialog
import uz.ibroxim.dostavkauz.dialog.PhoneBottomDialog
import uz.ibroxim.dostavkauz.utils.SharedPref
import uz.techie.mexmash.data.AppViewModel
import java.util.*


@AndroidEntryPoint
class ChooseLocationFragment : Fragment(R.layout.fragment_choose_location) {

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var phoneBottomDialog: PhoneBottomDialog
    private lateinit var customProgressDialog: CustomProgressDialog
    private var lat = SharedPref.latitude
    private var lon = SharedPref.longitude

    private var countryName = ""
    private var stateName = ""
    private var cityName = ""
    private var knownName = ""

    private var newMail = false

    private val TAG = "ChooseLocationFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(requireContext())
        Log.d(TAG, "onCreate: ")

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")

        arguments?.let {
            newMail = ChooseLocationFragmentArgs.fromBundle(it).newMail
        }


        customProgressDialog = CustomProgressDialog(requireContext())


        val latitude = SharedPref.latitude.toDouble()
        val longitude = SharedPref.longitude.toDouble()

        Log.d(TAG, "mapBottomSheet: latitude " + latitude)
        Log.d(TAG, "mapBottomSheet: longitude " + longitude)

        choose_location_mapview1.map.move(
            CameraPosition(Point(latitude, longitude), 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )



        choose_location_mapview1.map.addCameraListener(cameraListener)

        choose_location_btn_get.setOnClickListener {
            val latLong = choose_location_mapview1.map.cameraPosition.target
            println("LatLong " + latLong.latitude + ", " + latLong.longitude)

            lat = latLong.latitude.toString()
            lon = latLong.longitude.toString()



            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat.toDouble(), lon.toDouble(), 1)
                cityName = addresses[0].locality
                stateName = addresses[0].adminArea
                knownName = addresses[0].featureName
                countryName = addresses[0].countryName

            } catch (e: Exception) {
                Log.e(TAG, "onViewCreated: Exception " + e)
            }

            SharedPref.customer_latitude = lat
            SharedPref.customer_longitude = lon
            SharedPref.customer_country = countryName
            SharedPref.customer_city = cityName
            SharedPref.customer_state = stateName
            SharedPref.customer_knownAreaName = knownName

            if (newMail){
                findNavController().navigate(ChooseLocationFragmentDirections.actionChooseLocationFragmentToCreateMailFragment())
            }
            else{
                findNavController().navigate(ChooseLocationFragmentDirections.actionChooseLocationFragmentToCreateMailItemsFragment())
            }


        }


    }

    private var job: Job? = null
    private val cameraListener = object : CameraListener {
        override fun onCameraPositionChanged(
            map: Map,
            cameraPosition: CameraPosition,
            p2: CameraUpdateReason,
            b: Boolean
        ) {
            Log.d(TAG, "onViewCreated: bbbbb " + b)

            if (b && map.isValid) {

                job?.cancel()
                job = lifecycleScope.launch(Dispatchers.IO){

                    val latLong = cameraPosition.target
                    lat = latLong.latitude.toString()
                    lon = latLong.longitude.toString()

                    Log.d(TAG, "onCameraPositionChanged: lat "+lat)
                    Log.d(TAG, "onCameraPositionChanged: lon "+lon)





                    try {
                        val geocoder = Geocoder(requireContext(), Locale("uz", "UZ"))
                        val addresses = geocoder.getFromLocation(lat.toDouble(), lon.toDouble(), 1)

                        cityName = addresses[0].locality
                        stateName = addresses[0].adminArea
                        knownName = addresses[0].featureName
                        countryName = addresses[0].countryName

                    } catch (e: Exception) {
                        Log.e(TAG, "onViewCreated: Exception " + e)
                        cityName = ""
                        stateName = ""
                        knownName = ""
                        countryName = ""
                    }

                    withContext(Dispatchers.Main){
                        choose_location_tv_address.text = knownName + "  " + cityName
                        choose_location_tv_city.text = stateName + "  " + countryName
                    }


                }


            }


        }

    }


    override fun onStart() {
        super.onStart()
        choose_location_mapview1.onStart()
        MapKitFactory.getInstance().onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        choose_location_mapview1.onStop()
        MapKitFactory.getInstance().onStop()
        job?.cancel()
        Log.d(TAG, "onStop: ")
    }

}