package luyao.direct.worker

import android.Manifest
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.*
import luyao.direct.DirectApp
import luyao.direct.ext.createNotification
import luyao.direct.model.AppDatabase
import luyao.direct.model.DataProvider.loadDirect
import luyao.direct.model.DataProvider.loadInstalledApp
import luyao.direct.model.DataProvider.loadInstalledApp2
import luyao.direct.model.DataProvider.loadSearchEngineNew
import luyao.direct.model.DataProvider.queryContacts
import luyao.direct.model.MMKVConstants
import luyao.direct.util.getFloatTag
import luyao.direct.util.showFloatView
import luyao.ktx.coroutine.coroutineExceptionHandler
import luyao.ktx.ext.isGranted
import luyao.ktx.util.TimerCounter

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/12 10:36
 */
class DirectWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (MMKVConstants.showNotification) {
            withContext(Dispatchers.Main) {
                if (NotificationManagerCompat.from(DirectApp.App).areNotificationsEnabled()) {
                    applicationContext.createNotification()
                }
            }
        }

        if (MMKVConstants.showSideBar) {
            withContext(Dispatchers.Main) {
                showFloatView(getFloatTag())
            }
        }

        coroutineScope {
            launch(coroutineExceptionHandler) {
//                val engineDeferred = async(Dispatchers.IO) {
//                loadSearchEngine()
//                    loadSearchEngineNew()
//                }
                val directDeferred = async(Dispatchers.IO) {
                    loadDirect()
                }

                val installedDeferred = async(Dispatchers.IO) {
                    DirectApp.App.loadInstalledApp2()
                }

                val contactsDeferred = async(Dispatchers.IO) {
                    if (applicationContext.isGranted(Manifest.permission.READ_CONTACTS) && MMKVConstants.searchContacts)
                        DirectApp.App.queryContacts()
                }

                val moveDirectToNewTable = async(Dispatchers.IO) {
                    if (MMKVConstants.hasMoveNewDirectTable) return@async
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

                kotlin.runCatching {
//                    engineDeferred.await()
                    directDeferred.await()
                    installedDeferred.await()
                    contactsDeferred.await()
                    moveDirectToNewTable.await()
                }
            }
        }
        return Result.success()
    }


}