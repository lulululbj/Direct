package luyao.direct.vm

import android.Manifest
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import luyao.direct.DirectApp
import luyao.direct.ext.getEngineList
import luyao.direct.ext.newUID
import luyao.direct.model.AppDatabase
import luyao.direct.model.MMKVConstants
import luyao.direct.model.dao.*
import luyao.direct.model.entity.*
import luyao.direct.util.MoshiUtil
import luyao.direct.util.keyword.KeywordEngineFactory
import luyao.ktx.coroutine.coroutineExceptionHandler
import luyao.ktx.ext.*
import java.util.UUID
import javax.inject.Inject

/**
 *  @author: luyao
 * @date: 2021/7/11 下午4:26
 */
@HiltViewModel
class DataViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var appDao: AppDao

    @Inject
    lateinit var contactDao: ContactDao

//    @Inject
//    lateinit var directDao: DirectDao

    @Inject
    lateinit var newDirectDao: NewDirectDao

    @Inject
    lateinit var historyDao: SearchHistoryDao

    private var keyWords = ""

    class DiffData<T>(val data: T, val needDiff: Boolean)

    val searchResultData = MutableLiveData<List<Any>>()
    val recentResult = MutableLiveData<List<RecentEntity>>()
    val starResult = MutableLiveData<DiffData<List<RecentEntity>>>()
    val searchEngineList = MutableLiveData<DiffData<List<NewDirectEntity>>>() // 搜索引擎列表
    val tagEngineList = MutableLiveData<List<NewDirectEntity>>() // 通过 tag 过滤搜索引擎列表
    val defaultSearchEntity = MutableLiveData<NewDirectEntity>() // 默认搜索引擎
    val historyResult = MutableLiveData<List<SearchHistoryEntity>>() // 历史搜索数据
    val directCountData = MutableLiveData<List<DirectCount>>() // 快捷方式列表

    val keywordListData = MutableLiveData<List<String>>() // 关键词联想
    val allDirectListData = MutableLiveData<List<NewDirectEntity>>() // 所有快捷方式
    val appData: MutableLiveData<List<AppEntity>> = MutableLiveData()


    val engineSpanCount = MutableLiveData<Int>()
    val mainBgColor = MutableLiveData(MMKVConstants.mainBgColor)
    val panelBgColor = MutableLiveData(MMKVConstants.panelBgColor)
    val iconUri = MutableLiveData<Uri>()
    val selectApp = MutableLiveData<String>()
    val selectDirectApp = MutableLiveData<String>()
    val specifyApp = MutableLiveData<String>()
    val searchData = MutableLiveData<String>()
    val engineClickData = MutableLiveData<NewDirectEntity>()

    fun search(word: String, keyword: String = "") {
        keyWords = word
        keyWords.let {
            when {
                // 无输入，根据用户设置展示内容
                it == "" -> {
                    searchResultData.value = arrayListOf()
                    if (MMKVConstants.defaultShow == HomeMenuId.STAR) {
                        updateStarData(keyword)
                    } else {
                        updateSearchEngine()
                    }
                }
                // 展示历史搜索
                it == MMKVConstants.historyTag -> {
                    searchHistory()
                }
                // 展示星标数据
                it == MMKVConstants.starTag -> {
                    searchResultData.value = arrayListOf()
                    updateStarData(keyword)
                }
                // 展示最近使用
                it == MMKVConstants.recentTag -> {
                    searchResultData.value = arrayListOf()
                    updateRecentData()
                }
                // 邮箱
                it.isEmail() -> {
                    searchResultData.value = arrayListOf(EmailEntity(it))
                }
                // 直接访问 url
                it.isUrl() -> {
                    searchResultData.value = arrayListOf(UrlEntity(it))
                }
                // 带出搜索引擎
                needQueryEngine(it) -> {
                    queryEngine(it)
                }

                else -> {
                    // 按关键字查询
                    if (MMKVConstants.showSearchEngine) {
                        updateSearchEngine()
                    }
                    if (MMKVConstants.showSearchKeyword && MMKVConstants.onlyAssociation) return
                    if (MMKVConstants.autoCopyWhenSearch && word.isNotBlank()) DirectApp.App.copyToClipboard(
                        word
                    )
                    searchByKeyWord(it)
                }
            }
        }
    }


    private fun searchByKeyWord(keyword: String) {
        viewModelScope.launch {
            // 搜索本地应用
            val appDeferred = async(Dispatchers.IO) {

                if (MMKVConstants.showApp) {
                    getAppList(keyword)
                } else {
                    arrayListOf()
                }
            }
            // 搜索联系人
            val contactDeferred = async(Dispatchers.IO) {
                if (DirectApp.App.isGranted(Manifest.permission.READ_CONTACTS) && MMKVConstants.searchContacts) contactDao.searchContactByKeyword(
                    "%$keyword%"
                )
                else ArrayList()
            }
            // 搜索快捷方式
            val directDeferred = async(Dispatchers.IO) {
                if (!MMKVConstants.showAppDirect) arrayListOf()
                else {
                    val pinyin = if (MMKVConstants.searchByPin) "%$keyword%" else ""
                    val directList = newDirectDao.searchEnabledDirect("%$keyword%", pinyin)
                    // 用户添加的快捷方式，可能未指定应用，无包名
                    directList.filter { it.packageName.isEmpty() || DirectApp.App.isAppInstalled(it.packageName) }
                }
            }
            val search = arrayListOf<Any>()
            search.addAll(appDeferred.await())
            search.addAll(contactDeferred.await())
            search.addAll(directDeferred.await())

            search.sortByDescending {
                var order = 0
                when (it) {
                    is AppEntity -> {
                        order = it.count
                    }

                    is ContactEntity -> {
                        order = it.count
                    }

                    is NewDirectEntity -> {
                        order = it.count
                        order = it.count
                    }
                }
                order
            }

            if (search.size > 0) {
                search[0].run {
                    when (this) {
                        is AppEntity -> {
                            isFirst = true
                        }

                        is ContactEntity -> {
                            isFirst = true
                        }

                        is NewDirectEntity -> {
                            isFirst = true
                        }
                    }
                }
                search[search.size - 1].run {
                    when (this) {
                        is AppEntity -> {
                            isLast = true
                        }

                        is ContactEntity -> {
                            isLast = true
                        }

                        is NewDirectEntity -> {
                            isLast = true
                        }
                    }
                }
            }

            searchResultData.value = search
        }
    }

    private fun updateStarData(keyword: String = "", needDiff: Boolean = false) {
        viewModelScope.launch {

            val starListDeferred = async(Dispatchers.IO) {
                val starAppList = appDao.getStarList().filter { it.appName.contains(keyword) }
                    .filter { DirectApp.App.isAppInstalled(it.packageName) }
                    .map { it.toRecentEntity() }

                val starDirectList = newDirectDao.getStarList()
                    .filter { it.appName.contains(keyword) || it.label.contains(keyword) }
                    .filter { it.packageName.isEmpty() || DirectApp.App.isAppInstalled(it.packageName) }
                    .map { it.toRecentEntity() }

                val starList = arrayListOf<RecentEntity>()
                starList.addAll(starAppList)
                starList.addAll(starDirectList)
                starList.sortWith(compareBy({ it.order }, { it.starTime }))
                MMKVConstants.starJson = MoshiUtil.recentEntityListAdapter.toJson(starList)
                starList
            }
            starResult.value = DiffData(starListDeferred.await(), needDiff)
        }
    }

    private fun updateRecentData() {
        viewModelScope.launch {

            val recentListDeferred = async(Dispatchers.IO) {
                val recentAppList =
                    appDao.queryRecent().filter { DirectApp.App.isAppInstalled(it.packageName) }
                        .map { it.toRecentEntity() }

                val recentDirectList =
                    newDirectDao.queryRecent()
                        .filter { DirectApp.App.isAppInstalled(it.packageName) }
                        .map { it.toRecentEntity() }

                val recentEntityList = arrayListOf<RecentEntity>()
                recentEntityList.addAll(recentAppList)
                recentEntityList.addAll(recentDirectList)
                recentEntityList.sort()
                val subList = recentEntityList.subList(
                    0,
                    if (recentEntityList.size > 30) 30 else recentEntityList.size
                )
                MMKVConstants.recentJson =
                    MoshiUtil.recentEntityListAdapter.toJson(subList)
                subList
            }

            recentResult.value = recentListDeferred.await()
        }
    }

    fun updateSearchEngine(needDiff: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            newDirectDao.getEnabledSearchEngine().run {
                if (isNotEmpty()) {
                    searchEngineList.postValue(DiffData(this, needDiff))
                }
            }
        }
    }

    fun checkData() {
        viewModelScope.launch(Dispatchers.IO) {
            checkMigrateData()

            val existEngines = newDirectDao.getEnabledSearchEngine()
            val engineList = getEngineList()
            if (existEngines.isEmpty()) {
                newDirectDao.insert(engineList.schemes[1].toNewSearchEngine(1))
                newDirectDao.insert(engineList.schemes[0].toNewSearchEngine(1))
            }
            newDirectDao.getEnabledSearchEngine().run {
                if (isNotEmpty()) {
                    if (MMKVConstants.newSearchId.isEmpty()) {
                        MMKVConstants.newSearchId = first().id
                    }
                    find { it.id == MMKVConstants.newSearchId }?.let {
                        defaultSearchEntity.postValue(it)
                    }
                }
            }
        }
    }

    // 迁移数据 direct_entity -> new_direct_entity
    private fun checkMigrateData() {
        if (MMKVConstants.hasMoveNewDirectTable) return
        AppDatabase.getInstance(DirectApp.App).directDao().loadAll()
            .forEach { item ->
                val newItem = item.toNew()
                AppDatabase.getInstance(DirectApp.App).directDao().insert(item)
                if (item.id == MMKVConstants.searchId) {
                    MMKVConstants.newSearchId = newItem.id
                }
                MMKVConstants.hasMoveNewDirectTable = true
            }
    }

    // 更新星标项顺序
    fun updateStarOrder(starList: List<RecentEntity>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                starList.forEachIndexed { index, entity ->
                    entity.order = index + 1
                    if (entity.type == 0) {
                        appDao.updateStarOrder(entity.packageName, entity.order)
                    } else {
                        newDirectDao.updateStarOrder(entity.id, entity.order)
                    }
                }
            }
            updateStarData(needDiff = true)
        }
    }

    fun updateEngineOrder(engineList: List<NewDirectEntity>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                engineList.forEachIndexed { index, item ->
                    newDirectDao.updateEngineOrder(item.id, index)
                }
            }
            updateSearchEngine(needDiff = true)
        }
    }

    private fun searchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.loadAll().run {
                historyResult.postValue(this)
            }
        }
    }

    // 处理首页 RecyclerView 长按删除
    fun handleRemove(entity: Any, tabPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {


            if (entity is RecentEntity) {
                if (tabPosition == 0) { // 星标
                    if (entity.type == 0) { // app
                        appDao.updateStarStatus(entity.packageName, 0, System.currentTimeMillis())
                    } else { // 最近使用
                        newDirectDao.updateStarStatus(entity.id, 0, System.currentTimeMillis())
                    }
                } else { // 最近使用
                    if (entity.type == 0) { // app
                        appDao.resetCount(entity.packageName)
                    } else { // 最近使用
                        newDirectDao.resetCount(entity.id)
                    }
                }

            } else if (entity is SearchHistoryEntity) { // 历史搜索
                historyDao.delete(entity)
            }
        }
    }

    fun saveDefaultEngine(it: NewDirectEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            defaultSearchEntity.postValue(it)
            MMKVConstants.newSearchId = it.id
        }
    }

    // 删除星标项
    fun deleteStar(entity: RecentEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (entity.type == 0) { // app
                    appDao.updateStarStatus(entity.packageName, 0, System.currentTimeMillis())
                } else { // 最近使用
                    newDirectDao.updateStarStatus(entity.id, 0, System.currentTimeMillis())
                }
            }
            updateStarData()
        }
    }

    private fun needQueryEngine(keyword: String) =
        keyword.isNotBlank() && keyword.contains(MMKVConstants.engineTag) && !keyword.startsWith(
            MMKVConstants.engineTag
        )

    private fun queryEngine(keyword: String) {
        viewModelScope.launch {
            searchResultData.value = arrayListOf()
            val lastIndex = keyword.lastIndexOf(MMKVConstants.engineTag)
            val tag = keyword.substring(lastIndex + 1, keyword.length)

            withContext(Dispatchers.IO) {
                val result = arrayListOf<NewDirectEntity>()
                newDirectDao.getAllSearchEngine().let {
                    it.forEachIndexed { _, directEntity ->
                        directEntity.isQueryByTag = true
                        if (directEntity.label.contains(tag, true) || directEntity.tag.contains(
                                tag, true
                            ) || directEntity.pinyin.contains(
                                tag, true
                            ) || directEntity.exPinyin.contains(tag, true)
                        ) {
                            result.add(directEntity)
                        }
                    }
                    result.forEachIndexed { index, directEntity ->
                        if (index == 0) directEntity.isFirst = true
                        if (index == (result.size - 1)) directEntity.isLast = true
                    }
                    tagEngineList.postValue(result)
                }
            }
        }
    }

    fun updateDirect() {
        viewModelScope.launch(Dispatchers.IO) {
            val directCountList = mutableListOf<DirectCount>()
            newDirectDao.loadAll()
                .filter { it.isSearch == 0 && DirectApp.App.isAppInstalled(it.packageName) }
                .groupBy { it.packageName }.entries.forEach {
                    directCountList.add(
                        DirectCount(
                            it.key, it.value.filter { direct -> direct.enabled == 0 }.size, it.value
                        )
                    )
                }
            directCountData.postValue(directCountList.sortedBy { it.count }.reversed())
        }
    }

    fun handleClipboardContent(pasteContent: String) {
        searchResultData.value = arrayListOf(PasteEntity(pasteContent))
    }

    // 关键字联想
    fun searchKeyword(word: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

            if (word.isEmpty()) {
                keywordListData.postValue(arrayListOf())
                return@launch
            }

            KeywordEngineFactory.getEngine().queryKeyword(word) {
                keywordListData.postValue(it)
            }

        }
    }

    fun sendKeyword(keyWord: String) {
        searchData.value = keyWord
    }

    fun sendEngineClick(it: NewDirectEntity) {
        engineClickData.value = it
    }

    fun getAllDirect() {
        viewModelScope.launch(Dispatchers.IO) {
            allDirectListData.postValue(newDirectDao.getAllDirect())
        }
    }

    fun searchDirect(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val pinyin = if (MMKVConstants.searchByPin) "%$keyword%" else ""
            val directList = newDirectDao.searchEnabledDirect("%$keyword%", pinyin)

            allDirectListData.postValue(directList.filter { DirectApp.App.isAppInstalled(it.packageName) })
        }
    }

    fun searchApp(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appData.postValue(getAppList(keyword))
        }
    }

    private suspend fun getAppList(keyword: String): List<AppEntity> {
        val packageName = if (MMKVConstants.searchByPackageName) "%$keyword%" else ""
        val pinyin = if (MMKVConstants.searchByPin) "%$keyword%" else ""
        val results = if (MMKVConstants.showSystemApp) {
            appDao.searchApp("%$keyword%", packageName, pinyin)
        } else {
            appDao.searchAppWithSystem("%$keyword%", packageName, pinyin, false)
        }
        val dataList = arrayListOf<AppEntity>()
        results.forEach {
            // 过滤两种数据
            // 1.未安装
            // 2.已安装，未禁用，无法启动的
            if (DirectApp.App.isAppInstalled(it.packageName)) {
                if (!DirectApp.App.isAppEnabled(it.packageName) || DirectApp.App.canLaunch(it.packageName)) {
                    dataList.add(it)
                }
            }
        }
        return dataList
    }
}