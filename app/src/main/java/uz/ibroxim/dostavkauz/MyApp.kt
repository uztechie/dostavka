package uz.ibroxim.dostavkauz

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        MapKitFactory.setApiKey("f7073cfb-4c8d-4c17-acde-d17e662299ec")
    }
}