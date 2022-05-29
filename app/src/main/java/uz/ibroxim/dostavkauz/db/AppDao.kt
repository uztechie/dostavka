package uz.techie.airshop.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.ibroxim.dostavkauz.models.User
import uz.techie.mexmash.models.*

@Dao
interface AppDao {

    //profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("delete from user")
    suspend fun deleteUser()

    @Transaction
    suspend fun deleteInsertUser(user: User){
//        deleteUser()
        insertUser(user)
    }

    @Query("select * from user limit 1")
    fun getUser():List<User>

    @Query("select * from user limit 1")
    fun getUserLive():LiveData<List<User>>




    //regions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegions(list:List<Region>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistricts(list:List<District>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuarters(list:List<Quarter>)


    @Query("select * from region order by name")
    fun getRegions():LiveData<List<Region>>

    @Query("select * from district where region=:id order by name")
    fun getDistricts(id:Int):LiveData<List<District>>

    @Query("select * from quarter where district=:id order by name")
    fun getQuarters(id:Int):LiveData<List<Quarter>>



}