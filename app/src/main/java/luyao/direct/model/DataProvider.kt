package luyao.direct.model

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Process
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.MutableLiveData
import luyao.direct.BuildConfig
import luyao.direct.DirectApp
import luyao.direct.ext.getChannel
import luyao.direct.ext.getEngineList
import luyao.direct.ext.getExPinyin
import luyao.direct.ext.getPinyin
import luyao.direct.model.entity.*
import luyao.direct.util.MoshiUtil
import luyao.ktx.ext.getAppName
import luyao.ktx.util.TimerCounter
import luyao.ktx.util.YLog
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 *  @author: luyao
 * @date: 2021/7/11 上午12:07
 */
object DataProvider {

    val loadProgress = MutableLiveData<Int>() // 数据加载进度，全局使用

    //    val directDao = AppDatabase.getInstance(DirectApp.App).directDao()
    val newDirectDao = AppDatabase.getInstance(DirectApp.App).newDirectDao()

    /**
     *  获取已安装应用
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun Context.loadInstalledApp() {
        TimerCounter.start("loadInstalledApp")
        TimerCounter.start("getInstalledPackages")
        val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
        val appList = arrayListOf<AppEntity>()
        for (packageInfo in installedPackages) {
            val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            val packageName = packageInfo.packageName
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.versionCode
            val isSystem =
                (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM
//            val appIcon = packageInfo.applicationInfo.loadIcon(packageManager)
//            val iceState = IceBox.getAppEnabledSetting(this, packageName)
            appList.add(
                AppEntity(
                    packageName,
                    appName,
                    versionName ?: "",
                    versionCode,
                    null,
                    isSystem,
                    true,
                    0,
                    0,
                    appName.getPinyin(), appName.getExPinyin(), 0, 0, 0,
                )
            )
            AppIconCache.checkCache(packageName)
        }

        AppDatabase.getInstance(DirectApp.App).appDao().run {
            setAllInstallStatus(false)

            appList.forEach {
                if (getAppByPackageName(it.packageName) == null) {
                    insert(it)
                } else {
                    update(
                        it.packageName,
                        it.appName,
                        it.versionCode,
                        it.versionName,
                        it.isInstalled,
                        it.pinyin,
                        it.exPinyin,
                    )
                }
            }
        }
        TimerCounter.end("loadInstalledApp")
    }

    fun Context.loadInstalledApp2() {
        TimerCounter.start("loadInstalledApp2")
        val launcherApps = getSystemService<LauncherApps>()
        launcherApps?.run {
            val activityList = getActivityList(null, Process.myUserHandle())
            val appList = arrayListOf<AppEntity>()
            for (activityInfo in activityList) {
                val appName = activityInfo.applicationInfo.loadLabel(packageManager).toString()
                val packageName = activityInfo.applicationInfo.packageName
                val packageInfo = this@loadInstalledApp2.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_ACTIVITIES
                )
                val versionName = packageInfo.versionName
                val versionCode = packageInfo.versionCode
                val isSystem =
                    (activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM
//            val appIcon = packageInfo.applicationInfo.loadIcon(packageManager)
//            val iceState = IceBox.getAppEnabledSetting(this, packageName)
                val appEntity = AppEntity(
                    packageName,
                    appName,
                    versionName ?: "",
                    versionCode,
                    null,
                    isSystem,
                    true,
                    0,
                    0,
                    appName.getPinyin(), appName.getExPinyin(), 0, 0, 0,
                )
                appList.add(appEntity)
//                YLog.e(appEntity.toString())
                AppIconCache.checkCache(packageName, activityInfo)
            }

            AppDatabase.getInstance(DirectApp.App).appDao().run {
                setAllInstallStatus(false)

                appList.forEach {
                    if (getAppByPackageName(it.packageName) == null) {
                        insert(it)
                    } else {
                        update(
                            it.packageName,
                            it.appName,
                            it.versionCode,
                            it.versionName,
                            it.isInstalled,
                            it.pinyin,
                            it.exPinyin,
                        )
                    }
                }
            }
            TimerCounter.end("loadInstalledApp2")
        }
    }


    @SuppressLint("Range")
    fun Context.queryContacts() {
        TimerCounter.start("queryContacts")
        if (ContextCompat.checkSelfPermission(DirectApp.App, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        )
            return
        val contactList = arrayListOf<ContactEntity>()
        val uri: Uri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts._ID
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val displayName =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val hasNumber =
                    it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null,
                    null
                )
                phoneCursor?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val phoneNumber = cursor.getString(
                            cursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)
                        )
                        contactList.add(ContactEntity(contactId, phoneNumber, displayName, 0, 0))
                    }
                }
            }
        }
        AppDatabase.getInstance(DirectApp.App).contactDao().run {
            deleteAll()
            insertAll(contactList)
        }
        TimerCounter.end("queryContacts")
    }

    fun loadSearchEngineNew() {
        TimerCounter.start("loadSearchEngine")
        val engineList = getEngineList()
        val engines = engineList.schemes.map {
            NewDirectEntity(
                id = it.id,
                isSearch = it.isSearch,
                label = it.label,
                packageName = it.packageName,
                desc = it.desc,
                searchUrl = it.searchUrl,
                scheme = it.scheme,
                order = it.order,
                iconUrl = it.iconUrl
            )
        }
        val searchEngineList = DirectListEntity(engineList.version, engines)
//        DirectRetrofitClient.service.loadSearchEngine(MMKVConstants.engineVersion)
//            .onSuccess { searchEngineList ->
        if (searchEngineList.version <= MMKVConstants.engineVersion) return
        MMKVConstants.engineVersion = searchEngineList.version
        searchEngineList.schemes.forEachIndexed { index, engine ->
            if (MMKVConstants.isFirstLoad && (index == 0 || index == 1)) {
                engine.showPanel = 1 // 默认启用的搜索引擎
            }
            if (engine.localIcon == null) engine.localIcon = ""
            if (engine.tag == null) engine.tag = ""
            engine.appName = DirectApp.App.getAppName(engine.packageName)
            engine.pinyin = engine.label.getPinyin()
            engine.exPinyin = engine.label.getExPinyin()
            val dbEntity = newDirectDao.loadById(engine.id)
            if (dbEntity == null) {
                newDirectDao.insert(engine)
            } else {
                newDirectDao.updateEngine(
                    engine.id,
                    engine.label,
                    engine.searchUrl ?: "",
                    engine.scheme,
                    engine.appName ?: "",
                    engine.pinyin,
                    engine.exPinyin,
                    engine.iconUrl ?: ""
                )
            }
        }
        TimerCounter.end("loadSearchEngine")
//            }
    }

    /**
     *  获取云端快捷方式
     */
    fun loadDirect() {
        if (DirectApp.App.getChannel() == "google") return
        TimerCounter.start("loadDirect")
        val schemeUrl =
            if (BuildConfig.DEBUG) "https://gitee.com/lulululbj/direct/raw/dev/v2/direct.json"
            else "https://gitee.com/lulululbj/direct/raw/master/v2/direct.json"
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(schemeUrl)
            .build()

        val result = kotlin.runCatching {
            client.newCall(request).execute().use { response ->
                response.body?.let {
                    val directEntityList =
                        MoshiUtil.directListEntityAdapter.fromJson(it.string())
                    if (directEntityList != null) {
                        if (directEntityList.version > MMKVConstants.directVersion) {
                            MMKVConstants.directVersion = directEntityList.version
                            directEntityList.schemes.forEach { entity ->
                                entity.appName = DirectApp.App.getAppName(entity.packageName)
                                entity.pinyin = entity.label.getPinyin()
                                entity.exPinyin = entity.label.getExPinyin()
                                if (entity.localIcon == null) entity.localIcon = ""
                                if (entity.tag == null) entity.tag = ""
                                val dbEntity = newDirectDao.loadById(entity.id)
                                if (dbEntity == null) {
                                    newDirectDao.insert(entity)
                                } else {
                                    newDirectDao.updateDirect(
                                        entity.id,
                                        entity.label,
                                        entity.packageName,
                                        entity.desc,
                                        entity.scheme,
                                        entity.appName ?: "",
                                        entity.pinyin,
                                        entity.exPinyin
                                    )
                                }
                            }
                        }
                    }
                }
                TimerCounter.end("loadDirect")
            }
        }
        YLog.e("loadDirect ${result.isSuccess} ${result.exceptionOrNull()?.message}")
    }

    //private fun Context.test() {
//    getSystemService<UserManager>()?.run {
//        userProfiles.forEach { userHandle ->
//            getSystemService<LauncherApps>()?.run {
//                getActivityList(null, userHandle).forEach {
//                    if (it.applicationInfo.packageName == "com.tencent.mm") {
//                        val service = getSystemService<ActivityManager>()!!
//                        service.
//                        YLog.e("test ${it.label} ${it.componentName} $userHandle")
//                    }
//                }
//            }
//
//
//        }
//    }
//}


}

