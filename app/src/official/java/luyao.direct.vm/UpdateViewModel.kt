package luyao.direct.vm

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.azhon.appupdate.manager.DownloadManager
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.entity.UpdateCheckEntity
import luyao.direct.model.entity.net.CheckUpdateRequest
import luyao.direct.model.net.DirectRetrofitClient
import luyao.ktx.base.BaseViewModel
import luyao.ktx.ext.versionCode
import javax.inject.Inject

/**
 *  @author: luyao
 * @date: 2021/10/7 上午7:00
 */
@HiltViewModel
class UpdateViewModel @Inject constructor(val updateRepo: UpdateRepo) : BaseViewModel() {

    val updateCheckEntityValue = MutableLiveData<UpdateCheckEntity>()

    // 是否用户主动检测。设置页中的主动检测，无需判断是否忽略该版本
    fun checkUpdate(isInitiative: Boolean = false, jumpStore: Boolean = false) {

        updateRepo.checkUpdate(isInitiative)
            .onEach { updateCheckEntityValue.postValue(it) }
            .launchIn(viewModelScope)
//        flowNet(
//            flow {
//                val request = CheckUpdateRequest(DirectApp.App.versionCode)
//                emit(DirectRetrofitClient.service.checkUpdate(request))
//            },
//            onError = {
//                updateCheckEntityValue.postValue(
//                    UpdateCheckEntity(false, null)
//                )
//            },
//            onSuccess = { updateEntity ->
//                if (isInitiative) {
//                    if (updateEntity.versionCode > DirectApp.App.versionCode) {
//                        updateCheckEntityValue.postValue(
//                            UpdateCheckEntity(
//                                true,
//                                updateEntity
//                            )
//                        )
//                    } else {
//                        updateCheckEntityValue.postValue(
//                            UpdateCheckEntity(
//                                false,
//                                updateEntity
//                            )
//                        )
//                    }
//                } else {
//                    // 版本号大于当前版本，且该版本未曾被忽略
//                    if (updateEntity.versionCode > DirectApp.App.versionCode && !MMKV.defaultMMKV()!!
//                            .decodeBool("ignore_${updateEntity.versionCode}", false)
//                    ) {
//                        updateCheckEntityValue.postValue(
//                            UpdateCheckEntity(
//                                true,
//                                updateEntity
//                            )
//                        )
//                    } else {
//                        updateCheckEntityValue.postValue(
//                            UpdateCheckEntity(
//                                false,
//                                updateEntity
//                            )
//                        )
//                    }
//                }
//            }
//        )
    }

    fun handleUpdate(
        updateCheckEntity: UpdateCheckEntity?,
        activity: Activity,
        isInitiative: Boolean = false
    ) {
        updateCheckEntity?.let {
            if (it.updateEntity == null || !it.needUpdate) {
                if (isInitiative)
                    Toast.makeText(activity, "当前已是最新版本", Toast.LENGTH_SHORT).show()
                return
            }
            MaterialDialog(activity).show {
                cancelable(false)
                cancelOnTouchOutside(false)
                title(R.string.find_update)
                message(text = it.updateEntity.desc)
                if (isInitiative) {
                    negativeButton(R.string.Cancel)
                } else {
                    if (!it.updateEntity.force) {
                        checkBoxPrompt(R.string.ignore_this_update) { checked ->
                            MMKV.defaultMMKV()
                                ?.encode("ignore_${it.updateEntity.versionCode}", checked)
                        }
                        negativeButton(R.string.Cancel)
                    }
                }
                positiveButton(R.string.update) { _ ->
                    DownloadManager.getInstance(activity)
                        .setApkName("Direct.apk")
                        .setApkUrl(it.updateEntity.apkUrl)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setApkMD5(it.updateEntity.md5)
                        .download()
                }
            }
        }
    }
}