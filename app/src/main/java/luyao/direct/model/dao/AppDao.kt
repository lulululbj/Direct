package luyao.direct.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import luyao.direct.model.entity.AppEntity

/**
 *  @author: luyao
 * @date: 2021/7/9 下午11:50
 */
@Dao
interface AppDao {

    @Query("select * from app_entity")
    fun loadAll(): List<AppEntity>

    @Query("select * from app_entity where package_name = :packageName")
    fun getAppByPackageName(packageName: String): AppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(app: AppEntity)

    @Query("update app_entity set app_name = :appName, version_name = :versionName, version_code = :versionCode, is_install = :installed, pinyin = :pinyin, ex_pinyin = :exPinyin where package_name = :packageName")
    fun update(
        packageName: String,
        appName: String,
        versionCode: Int,
        versionName: String,
        installed: Boolean,
        pinyin: String,
        exPinyin: String,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(appList: List<AppEntity>)

    @Query("delete from app_entity")
    suspend fun deleteAll()

    @Query("update app_entity set is_install = :isInstalled")
    fun setAllInstallStatus(isInstalled: Boolean)

    @Query("select * from app_entity where (app_name like :appName or package_name like :packageName or pinyin like :pinyin or ex_pinyin like :pinyin) and is_system = :showSystemApp")
    suspend fun searchAppWithSystem(
        appName: String,
        packageName: String,
        pinyin: String,
        showSystemApp: Boolean
    ): List<AppEntity>

    @Query("select * from app_entity where app_name like :appName or package_name like :packageName or pinyin like :pinyin or ex_pinyin like :pinyin")
    suspend fun searchApp(appName: String, packageName: String, pinyin: String): List<AppEntity>

    @Query("update app_entity set count = count+1, last_time = :lastTime where package_name = :packageName")
    fun addCount(lastTime: Long, packageName: String)

    @Query("select * from app_entity where count > 0 order by last_time desc  limit 30 ")
    fun queryRecent(): List<AppEntity>

    @Query("update app_entity set count = 0")
    fun clearRecentUse()

    @Query("update app_entity set is_star = :isStar, star_time = :startTime, star_order = 0 where package_name = :packageName")
    fun updateStarStatus(packageName: String, isStar: Int, startTime: Long)

    @Query("select * from app_entity where is_star = 1")
    fun getStarList(): List<AppEntity>

    @Query("update app_entity set star_order = :order where package_name = :packageName")
    fun updateStarOrder(packageName: String, order: Int)

    @Query("update app_entity set count = 0 where package_name = :packageName")
    fun resetCount(packageName: String)
}