package uz.ibroxim.dostavkauz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import uz.ibroxim.dostavkauz.dialog.SuccessFailedDialog
import uz.ibroxim.dostavkauz.fragments.driver.DriverSearchOrderFragment
import uz.techie.mexmash.data.AppViewModel

@AndroidEntryPoint
class DriverActivity : AppCompatActivity() {
    val viewModel by viewModels<AppViewModel>()


    private lateinit var successFailedDialog: SuccessFailedDialog

    private val  REQUEST_CODE = 222
    private val TAG = "MainActivity"

    lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentContainerDriver) as NavHostFragment
        navController = navHostFragment.findNavController()

        bottomNavigationView = findViewById(R.id.bottomNavigationViewDriver)
        bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val id = destination.id
            if (id == R.id.newOrderDetailsFragment || id == R.id.acceptedOrdersFragment || id == R.id.acceptedOrderDetailsFragment ||
                    id == R.id.orderUpdateFragment){
                bottomNavigationView.visibility = View.GONE
            }
            else{
                bottomNavigationView.visibility = View.VISIBLE
            }
        }

    }


}