package luyao.direct.vm

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import luyao.direct.DirectApp
import luyao.direct.model.entity.UpdateCheckEntity
import luyao.ktx.ext.jumpAppStore

/**
 *  @author: luyao
 * @date: 2021/10/7 上午7:00
 */
class UpdateViewModel : ViewModel() {

    val updateCheckEntityValue = MutableLiveData<UpdateCheckEntity>()

    // 是否用户主动检测。设置页中的主动检测，无需判断是否忽略该版本
    fun checkUpdate(isInitiative: Boolean = false, jumpStore: Boolean = false) {
        if (jumpStore) {
            DirectApp.App.jumpAppStore("luyao.direct", "com.android.vending")
        }
    }

    fun handleUpdate(
        updateCheckEntity: UpdateCheckEntity?,
        activity: Activity,
        isInitiative: Boolean = false
    ) {

    }


}



