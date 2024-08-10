package luyao.direct.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import luyao.direct.model.entity.NewDirectEntity

/**
 * author: luyao
 * date:   2021/9/27 13:57
 */
@Dao
interface NewDirectDao {

    @Query("select * from new_direct_entity where id = :id")
    fun loadById(id: String): NewDirectEntity?

    @Query("select * from new_direct_entity where label = :label and is_search = 1")
    fun loadSearchEngineByLabel(label: String): NewDirectEntity?

    @Query("select * from new_direct_entity")
    fun loadAll(): List<NewDirectEntity>

    @Query("select * from new_direct_entity where package_name = :packageName and is_search = 0")
    fun loadDirectByPackageName(packageName: String): List<NewDirectEntity>

    @Query("select * from new_direct_entity where scheme = :scheme and is_search = 0")
    fun loadDirectByScheme(scheme: String): NewDirectEntity?

    @Query("delete from new_direct_entity where id = :id")
    fun deleteById(id: String)

    @Query("delete from new_direct_entity")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: NewDirectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(appList: List<NewDirectEntity>)

    @Query("select * from new_direct_entity where (label like :keyWord or app_name like :keyWord or pinyin like :pinyin or ex_pinyin like :pinyin) and is_search = 0 and enabled = 0")
    fun searchEnabledDirect(keyWord: String, pinyin: String): List<NewDirectEntity>

    @Query("select * from new_direct_entity where is_search = 1 and enabled = 0 order by id")
    fun loadSearchEngineOrderById(): List<NewDirectEntity>

    @Query("select * from new_direct_entity where is_search = 1 and enabled = 0 and show_panel = 1 order by engine_order")
    fun getEnabledSearchEngine(): List<NewDirectEntity>

    @Query("select * from new_direct_entity where is_search = 1 order by engine_order")
    fun getAllSearchEngine(): List<NewDirectEntity>

    @Query("select * from new_direct_entity where is_search = 0")
    fun getAllDirect(): List<NewDirectEntity>

    @Query("select * from new_direct_entity where is_search = 1 and (label like :keyWord or app_name like :keyWord or pinyin like :keyWord or ex_pinyin like :keyWord) ")
    fun getSearchEngineByKeyword(keyWord: String): List<NewDirectEntity>

    @Query("update new_direct_entity set is_star = :isStar, star_time = :starTime, star_order = 0 where id = :directId")
    fun updateStarStatus(directId: String, isStar: Int, starTime: Long)

    @Query("update new_direct_entity set count = count+1, last_time = :lastTime where id = :directId")
    fun addCount(lastTime: Long, directId: String)

    @Query("select * from new_direct_entity where count > 0 order by last_time desc  limit 30 ")
    fun queryRecent(): List<NewDirectEntity>

    @Query("update new_direct_entity set count = 0")
    fun clearRecentUse()

    @Query("select * from new_direct_entity where is_star = 1")
    fun getStarList(): List<NewDirectEntity>

    @Query("update new_direct_entity set enabled = 0")
    fun setAllDisable()

    @Query("update new_direct_entity set enabled = 1 where id = :id")
    fun setDisabled(id: String)

    @Query("update new_direct_entity set star_order = :order where id =:directId")
    fun updateStarOrder(directId: String, order: Int)

    @Query("update new_direct_entity set engine_order = :order where id =:directId")
    fun updateEngineOrder(directId: String, order: Int)

    @Query("update new_direct_entity set count = 0 where id =:directId")
    fun resetCount(directId: String)

    @Query("update new_direct_entity set enabled = 0, label = :name, search_url = :url, scheme = :scheme, local_icon = :localIcon, tag = :tag, show_panel = :showInPanel, package_name = :specifyAppPackageName, icon_url = :iconUrl where id = :id")
    fun update(
        id: String,
        name: String,
        url: String,
        scheme: String,
        localIcon: String,
        tag: String,
        showInPanel: Int,
        specifyAppPackageName: String,
        iconUrl: String
    )

    // 更新搜索引擎数据
    @Query("update new_direct_entity set enabled = 0, label = :name, search_url = :url, scheme = :scheme,app_name = :appName, pinyin = :pinyin, ex_pinyin = :exPinyin, icon_url = :iconUrl where id = :id")
    fun updateEngine(
        id: String,
        name: String,
        url: String,
        scheme: String,
        appName: String,
        pinyin: String,
        exPinyin: String,
        iconUrl: String
    )

    // 更新快捷方式数据
    @Query("update new_direct_entity set enabled = 0, label = :label, package_name = :packageName, `desc` = :desc, scheme = :scheme,app_name = :appName, pinyin = :pinyin, ex_pinyin = :exPinyin where id = :id")
    fun updateDirect(
        id: String,
        label: String,
        packageName: String,
        desc: String,
        scheme: String,
        appName: String,
        pinyin: String,
        exPinyin: String
    )

    // 更新快捷方式是否启用
    @Query("update new_direct_entity set enabled = :enabled where id = :id")
    fun updateDirectEnable(id: String, enabled: Int)


    @Query("update new_direct_entity set enabled = 0, label = :label,  scheme = :scheme, local_icon = :localIcon, package_name = :selectPackageName, app_name = :selectAppName where id = :id ")
    fun updateDirect(
        id: String,
        label: String,
        scheme: String,
        selectAppName: String,
        selectPackageName: String,
        localIcon: String
    )

    @Query("update new_direct_entity set enabled = 0, label = :label,  scheme = :scheme, local_icon = :localIcon, package_name = :selectPackageName, app_name = :selectAppName, exec_mode = :execMode where id = :id ")
    fun updateDirect(
        id: String,
        label: String,
        scheme: String,
        selectAppName: String,
        selectPackageName: String,
        localIcon: String,
        execMode: Int
    )

    @Query("update new_direct_entity set enabled = 1, show_panel = 0 where id = :id")
    fun disableSearchEngine(id: String)

    @Query("update new_direct_entity set enabled = 0, show_panel = 1 where id = :id")
    fun enableSearchEngine(id: String)

}