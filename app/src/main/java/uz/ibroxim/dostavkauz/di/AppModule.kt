package uz.ibroxim.dostavkauz.di

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.ibroxim.dostavkauz.utils.Constants
import uz.techie.airshop.db.AppDatabase
import uz.techie.airshop.network.RetrofitApi
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofitApi(): RetrofitApi =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_WITH_API)
            .client(httpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitApi::class.java)



    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "dostavka.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()


    fun httpClient(): OkHttpClient {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        val client: OkHttpClient = builder.build()
        return client
    }

    var gson = GsonBuilder()
        .setLenient()
        .create()

}