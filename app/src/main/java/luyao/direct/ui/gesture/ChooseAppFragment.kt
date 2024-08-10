package luyao.direct.ui.gesture

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.R
import luyao.direct.adapter.ChooseGestureViewDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.adapter.SelectAppViewDelegate
import luyao.direct.base.DirectBaseFragment
import luyao.direct.databinding.FragmentChooseAppBinding
import luyao.direct.model.entity.AppEntity
import luyao.direct.model.entity.RecentEntity
import luyao.direct.model.entity.toRecentEntity
import luyao.direct.vm.DataViewModel
import luyao.direct.vm.SelectAppVM

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/6 09:55
 */
@AndroidEntryPoint
class ChooseAppFragment : DirectBaseFragment(R.layout.fragment_choose_app) {

    private val vm by activityViewModels<DataViewModel>()
    private val binding by viewbind<FragmentChooseAppBinding>()
    private val appList = arrayListOf<AppEntity>()
    private val multiTypeAdapter = MultiTypeAdapter()
    private var gestureConfigListener: ((RecentEntity) -> Unit)? = null
    private val selectViewDelegate = ChooseGestureViewDelegate(appClick = {
        gestureConfigListener?.invoke(it.toRecentEntity())
    },
        appLongClick = {})

    fun setGestureConfigListener(listener: ((RecentEntity) -> Unit)? = null) {
        gestureConfigListener = listener
    }

    override fun initView() {
        initRv()
    }

    override fun initData() {
//        searchApp()
    }

    fun searchApp(keyword: String = "") {
        vm.searchApp(keyword)
    }

    private fun initRv() {
        binding.appRv.run {
            layoutManager = GridLayoutManager(requireContext(), 4)
            multiTypeAdapter.register(selectViewDelegate)
            multiTypeAdapter.items = appList
            adapter = multiTypeAdapter
        }
    }

    override fun startObserve() {
        super.startObserve()
        vm.appData.observe(this) {
            val callback = MultiDiffCallback(appList, it)
            val result = DiffUtil.calculateDiff(callback)
            appList.clear()
            appList.addAll(it)
            result.dispatchUpdatesTo(multiTypeAdapter)
        }
    }
}