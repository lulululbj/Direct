package luyao.direct.model.dao

import luyao.direct.DirectApp
import luyao.direct.model.AppDatabase
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.SearchHistoryEntity
import luyao.ktx.app.AppScope

/**
 *  @author: luyao
 * @date: 2021/7/31 上午7:58
 */
fun addAppCount(packageName: String) {
    AppScope.launchIO {
        AppDatabase.getInstance(DirectApp.App).run {
            appDao().addCount(System.currentTimeMillis(), packageName)
        }
    }
}

fun addDirectCount(id: String) {
    AppScope.launchIO {
        AppDatabase.getInstance(DirectApp.App).newDirectDao().addCount(System.currentTimeMillis(), id)
    }
}

fun saveSearchHistory(keyWord: String, type: Int) {
    if (keyWord.isEmpty()) return
    if (!MMKVConstants.saveHistory) return
    AppScope.launchIO {
        AppDatabase.getInstance(DirectApp.App).run {
            val entity = searchHistoryDao().load(keyWord)
            if (entity == null) {
                searchHistoryDao().insert(
                    SearchHistoryEntity(
                        0,
                        keyWord,
                        type,
                        System.currentTimeMillis(),
                        1
                    )
                )
            } else {
                searchHistoryDao().updateAndAddCount(keyWord, System.currentTimeMillis())
            }
        }
    }
}