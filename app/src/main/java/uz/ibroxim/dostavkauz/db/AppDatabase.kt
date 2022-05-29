package uz.techie.airshop.db

import android.transition.Slide
import androidx.room.Database
import androidx.room.RoomDatabase
import uz.ibroxim.dostavkauz.models.User
import uz.techie.mexmash.models.*

@Database(
    entities = [
        User::class,
        Region::class,
        District::class,
        Quarter::class
               ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun AppDao(): AppDao
}