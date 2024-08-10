package luyao.direct.vm

import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luyao.direct.DirectApp
import luyao.direct.ext.getExPinyin
import luyao.direct.ext.getPinyin
import luyao.direct.ext.newUID
import luyao.direct.model.dao.NewDirectDao
import luyao.direct.model.entity.AppDirect
import luyao.direct.model.entity.NewDirectEntity
import luyao.ktx.util.YLog
import javax.inject.Inject

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/18 11:40
 */
@HiltViewModel
class DirectVM @Inject constructor() : ViewModel() {

    private val blackList = mutableListOf(
        "WXPayEntryActivity",
        "WXEntryActivity",
        "com.alipay.sdk.app.PayResultActivity",
        "AlipayResultActivity",
        "com.tencent.tauth.AuthActivity",
        "OPPOPushActivity",
        "VivoPushActivity",
        "MIUIPushActivity",
    )

    private fun inBlackList(name: String): Boolean {
        blackList.forEach {
            if (name.contains(it)) return true
        }
        return false
    }

    @Inject
    lateinit var directDao: NewDirectDao

    val directData = MutableLiveData<List<Any>>()

    fun loadAppDirect(packageName: String, showAll: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val allData = mutableListOf<Any>()
            if (showAll) {
                val packageManager = DirectApp.App.packageManager
                val packageInfo = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_ACTIVITIES
                )
                val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                packageInfo.activities?.filter { it.exported && it.enabled }?.forEach {
                    val scheme =
                        "intent:#Intent;package=${it.packageName};component=$packageName/${it.name};end"
                    val localDirect = directDao.loadDirectByScheme(scheme)
                    if (!inBlackList(it.name) && (localDirect == null || localDirect.enabled == 1)) {
                        val label = it.loadLabel(packageManager).toString()
                        allData.add(AppDirect(appName, packageName, label, scheme, it.name))
                    }
                }
            } else {
                val directList =
                    directDao.loadDirectByPackageName(packageName).sortedBy { it.enabled }
                allData.addAll(directList)
            }
            directData.postValue(allData)
        }
    }

    fun updateDirectAsync(directEntity: NewDirectEntity, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            updateDirect(directEntity, enabled)
        }
    }

    private fun updateDirect(directEntity: NewDirectEntity, enabled: Boolean) {
        directDao.updateDirectEnable(directEntity.id, if (enabled) 0 else 1)
    }

    fun updateDirectAsync(appDirect: AppDirect, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            updateDirect(appDirect, enabled)
//            loadAppDirect(appDirect.packageName)
        }
    }

    private fun updateDirect(appDirect: AppDirect, enabled: Boolean) {
        // 已存在，直接更新
        directDao.loadDirectByScheme(appDirect.scheme)?.let {
            YLog.e("update ${appDirect.scheme} $enabled")
            directDao.updateDirectEnable(it.id, if (enabled) 0 else 1)
            return
        }

        // 不存在，新增。用户手动新增的快捷方式 id 从 200000 开始

//        val start = 200000
//        val userDirectList = directDao.getAllDirect().filter { it.id >= start }
//        val id = if (userDirectList.isEmpty()) start else userDirectList.last().id + 1
        val directEntity = NewDirectEntity(
            newUID(),
            appDirect.label,
            appDirect.appName,
            appDirect.packageName,
            "",
            appDirect.scheme,
            appDirect.label.getPinyin(),
            appDirect.label.getExPinyin(),
            "",
            0,
            "",
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            "",
            "",
            0
        )
        directDao.insert(directEntity)
    }

    fun selectAll(directList: MutableList<Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            directList.forEach {
                if (it is NewDirectEntity) {
                    it.enabled = 0
                    updateDirect(it, true)
                } else if (it is AppDirect) {
                    it.enabled = true
                    updateDirect(it, true)
                }
            }
        }
    }

}