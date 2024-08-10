package luyao.direct.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luyao.direct.DirectApp
import luyao.direct.model.MMKVConstants
import luyao.direct.model.dao.AppDao
import luyao.direct.model.entity.AppEntity
import luyao.ktx.ext.isAppInstalled
import javax.inject.Inject

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/9 17:20
 */
@HiltViewModel
class SelectAppVM @Inject constructor() : ViewModel() {

    val appData: MutableLiveData<List<AppEntity>> = MutableLiveData()

    @Inject
    lateinit var appDao: AppDao

    fun searchApp(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val packageName = if (MMKVConstants.searchByPackageName) "%$keyword%" else ""
            val pinyin = if (MMKVConstants.searchByPin) "%$keyword%" else ""
            val results = if (MMKVConstants.showSystemApp) {
                appDao.searchApp("%$keyword%", packageName, pinyin)
            } else {
                appDao.searchAppWithSystem("%$keyword%", packageName, pinyin, false)
            }
            val dataList = arrayListOf<AppEntity>()
            results.forEach {
                if (DirectApp.App.isAppInstalled(it.packageName)) {
                    dataList.add(it)
                }
            }
            appData.postValue(dataList)
        }
    }
}