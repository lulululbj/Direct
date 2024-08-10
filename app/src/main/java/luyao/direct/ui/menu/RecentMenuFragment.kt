package luyao.direct.ui.menu

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import luyao.direct.R
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.adapter.RecentViewDelegate
import luyao.direct.databinding.FragmentCommonMenuBinding
import luyao.direct.model.MMKVConstants
import luyao.direct.ui.DirectActivity
import luyao.direct.vm.DataViewModel
import luyao.ktx.base.BaseFragment
import luyao.ktx.util.YLog

class RecentMenuFragment : BaseFragment(R.layout.fragment_common_menu) {

    private val binding: FragmentCommonMenuBinding by viewbind()
    private val recentViewDelegate by lazy { RecentViewDelegate() } // 星标，最近使用
    private val engineList = ArrayList<Any>()
    private val engineAdapter by lazy { MultiTypeAdapter() }
    private val vm by activityViewModels<DataViewModel>()

    override fun initView() {
        initRv()
//        vm.search(MMKVConstants.recentTag)
    }

    override fun initData() {
//                vm.search(MMKVConstants.recentTag)
    }

    private fun initRv() {
        binding.menuRv.run {
            layoutManager = GridLayoutManager(requireActivity(), MMKVConstants.engineSpanCount)
            engineAdapter.register(recentViewDelegate) // 搜索引擎
            engineAdapter.items = engineList
            adapter = engineAdapter
        }
    }

    override fun startObserve() {
        super.startObserve()

        vm.run {
            recentResult.observe(viewLifecycleOwner) {
                YLog.e("receive recent")
                submitEngineList(it, MMKVConstants.engineSpanCount)
            }

//            engineSpanCount.observe(viewLifecycleOwner) { count ->
//                (binding.menuRv.layoutManager as GridLayoutManager).spanCount = count
//            }

        }
    }

    private fun submitEngineList(it: List<Any>, spanCount: Int) {
        val callback = MultiDiffCallback(engineList, it)
        val result = DiffUtil.calculateDiff(callback)
        engineList.clear()
        engineList.addAll(it)
        result.dispatchUpdatesTo(engineAdapter)
        (binding.menuRv.layoutManager as GridLayoutManager).spanCount = spanCount
    }
}