package luyao.direct.ui.menu

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import luyao.direct.R
import luyao.direct.adapter.EngineViewDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.databinding.FragmentCommonMenuBinding
import luyao.direct.ext.go
import luyao.direct.model.MMKVConstants
import luyao.direct.model.dao.saveSearchHistory
import luyao.direct.model.entity.SearchHistoryEntity
import luyao.direct.ui.DirectActivity
import luyao.direct.vm.DataViewModel
import luyao.ktx.base.BaseFragment
import luyao.ktx.util.YLog

class SearchEngineMenuFragment : BaseFragment(R.layout.fragment_common_menu) {

    private val binding: FragmentCommonMenuBinding by viewbind()
    private val engineViewDelegate by lazy { EngineViewDelegate(
        engineClick = { it, view, position -> vm.sendEngineClick(it) }
    ) } // 搜索引擎
    private val engineList = ArrayList<Any>()
    private val engineAdapter by lazy { MultiTypeAdapter() }
    private val vm by activityViewModels<DataViewModel>()

    override fun initView() {
        initRv()
//        vm.updateSearchEngine()
    }

    override fun initData() {
//                vm.updateSearchEngine()
    }

    private fun initRv() {
        binding.menuRv.run {
            layoutManager = GridLayoutManager(requireActivity(), MMKVConstants.engineSpanCount)
            engineAdapter.register(engineViewDelegate) // 搜索引擎
            engineAdapter.items = engineList
            adapter = engineAdapter
        }
    }

    override fun startObserve() {
        super.startObserve()

        vm.run {
            searchEngineList.observe(viewLifecycleOwner) { data ->
                YLog.e("receive engine")
                // 过滤不显示到面板的引擎
                submitEngineList(data.data.filter { it.showPanel == 1 }, MMKVConstants.engineSpanCount)
            }

            engineSpanCount.observe(viewLifecycleOwner) { count ->
                (binding.menuRv.layoutManager as GridLayoutManager).spanCount = count
            }

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