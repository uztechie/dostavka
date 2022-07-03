package uz.ibroxim.dostavkauz

import android.app.Application
import android.content.Context
import com.chibatching.kotpref.Kotpref
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import org.acra.ACRA
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.MailSenderConfigurationBuilder
import org.acra.data.StringFormat

@HiltAndroidApp
class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        MapKitFactory.setApiKey("f7073cfb-4c8d-4c17-acde-d17e662299ec")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        val builder = CoreConfigurationBuilder(this)
            .setBuildConfigClass(BuildConfig::class.java)
            .setReportFormat(StringFormat.JSON)
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder::class.java)
            .setMailTo("ibroximbox@gmail.com")
            .setReportAsFile(true)
            .setSubject(getString(R.string.app_name))
            .setBody("***Iltimos xatolik haqida bizga xabar bering ***\n\nNAME:\nSCENARIO:")
            .setEnabled(true)
        if (BuildConfig.DEBUG) {
            ACRA.DEV_LOGGING = true
        }
        ACRA.init(this, builder)
    }
}