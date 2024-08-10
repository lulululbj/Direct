package luyao.direct.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import luyao.direct.ext.getEngineList
import luyao.direct.model.dao.NewDirectDao
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.model.entity.toNewSearchEngine
import javax.inject.Inject

@HiltViewModel
class EngineViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var directDao: NewDirectDao

    val engineListData = MutableLiveData<List<NewDirectEntity>>()

    fun loadEngine(keyWord: String = "") {
        viewModelScope.launch(Dispatchers.IO) {

            getEngineList().schemes
                .filter {
                    it.label.contains(keyWord) || it.packageName.contains(keyWord)
                            || it.desc.contains(keyWord)
                }
                .map { it.toNewSearchEngine() }
//                .map {
//                    directDao.loadSearchEngineByLabel(it.label)?.let {  existEngine ->
//                        it.enabled = existEngine.enabled
//                        it.showPanel = existEngine.showPanel
//                        it.id = existEngine.id
//                    }
//                    it
//                }
                .run {
                    engineListData.postValue(this)
                }
        }
    }

    fun handleEngine(directEntity: NewDirectEntity, checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
//            val existEngine = directDao.loadSearchEngineByLabel(directEntity.label)
//            if (existEngine != null) {
//                if (checked) {
//                    directDao.enableSearchEngine(existEngine.id)
//                } else {
//                    directDao.disableSearchEngine(existEngine.id)
//                }
//            } else {
                directEntity.enabled = if (checked) 0 else 1
                directEntity.showPanel = if (checked) 1 else 0
                directDao.insert(directEntity)
//            }

        }
    }
}