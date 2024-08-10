package luyao.direct.vm

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import luyao.direct.DirectApp
import luyao.direct.model.entity.UpdateCheckEntity
import luyao.direct.model.entity.UpdateEntity
import luyao.direct.model.entity.net.CheckUpdateRequest
import luyao.direct.model.net.DirectRetrofitClient
import luyao.ktx.ext.versionCode
import luyao.ktx.model.BizResult
import javax.inject.Inject

/**
 * Description:
 * Author: luyao
 * Date: 2023/3/30 16:02
 */
class UpdateRepo @Inject constructor() {

    fun checkUpdate(isInitiative: Boolean = false) =
        bizFlow(
            flow {
                val request = CheckUpdateRequest(DirectApp.App.versionCode)
                emit(DirectRetrofitClient.service.checkUpdate(request))
            }
        ).map { value: BizResult<UpdateEntity> -> handleUpdateResult(isInitiative, value) }


    private fun handleUpdateResult(isInitiative: Boolean = false, result:BizResult<UpdateEntity>): UpdateCheckEntity {
        return if (result is BizResult.Success) {
            handleUpdateResult(isInitiative, result.data)
        } else {
            UpdateCheckEntity(false, null)
        }
    }
    private fun handleUpdateResult(isInitiative: Boolean = false, updateEntity: UpdateEntity): UpdateCheckEntity {
       return if (isInitiative) {
            if (updateEntity.versionCode > DirectApp.App.versionCode) {
                UpdateCheckEntity(true, updateEntity)
            } else {
                UpdateCheckEntity(false, updateEntity)
            }
        } else {
            // 版本号大于当前版本，且该版本未曾被忽略
            if (updateEntity.versionCode > DirectApp.App.versionCode && !MMKV.defaultMMKV()!!
                    .decodeBool("ignore_${updateEntity.versionCode}", false)
            ) {
                UpdateCheckEntity(true, updateEntity)
            } else {
                UpdateCheckEntity(false, updateEntity)
            }
        }
    }


    fun <T> bizFlow(
        flow: Flow<BizResult<T>>
    ) =
        flow
            .flowOn(Dispatchers.IO) // flowOn 仅影响上游
            .onCompletion {

            }
            .catch {
                emit(BizResult.Error(it))
            }



}