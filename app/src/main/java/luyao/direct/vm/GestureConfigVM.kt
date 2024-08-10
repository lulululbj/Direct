package luyao.direct.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import luyao.direct.model.entity.RecentEntity

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/6 13:30
 */
class GestureConfigVM : ViewModel() {

    val configVM = MutableLiveData<RecentEntity>()

}