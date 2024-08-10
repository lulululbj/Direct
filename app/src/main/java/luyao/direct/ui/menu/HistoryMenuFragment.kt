package luyao.direct.ui.menu

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import luyao.direct.R
import luyao.direct.adapter.HistoryViewDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.databinding.FragmentCommonMenuBinding
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.AppEntity
import luyao.direct.model.entity.ContactEntity
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.vm.DataViewModel
import luyao.ktx.base.BaseFragment
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.itemPadding
import luyao.ktx.util.YLog
import luyao.view.explosion.ExplosionField

class HistoryMenuFragment : BaseFragment(R.layout.fragment_common_menu) {

    private val binding: FragmentCommonMenuBinding by viewbind()
    private val historyViewDelegate by lazy { HistoryViewDelegate() } // 历史记录
    private val historyList = ArrayList<Any>()
    private val historyAdapter by lazy { MultiTypeAdapter() }
    private val vm by activityViewModels<DataViewModel>()
    private val explosionField: ExplosionField by lazy {
        ExplosionField.attach2Window(
            requireActivity()
        )
    }


    override fun initView() {
        initRv()
//        vm.search(MMKVConstants.recentTag)
    }

    override fun initData() {
//                vm.search(MMKVConstants.recentTag)
    }

    private fun initRv() {
        binding.menuRv.run {
            itemPadding(dp2px(4).toInt())
            layoutManager = GridLayoutManager(requireActivity(), 2)
            historyViewDelegate.setItemLongClickListener { view, i, searchHistoryEntity ->
                handleLongClick(view, i, searchHistoryEntity)
            }
            historyViewDelegate.setItemClickListener { _, _, searchHistoryEntity ->
                vm.sendKeyword(searchHistoryEntity.keyWord)
//                binding.searchEt.setText(searchHistoryEntity.keyWord)
            }
            historyAdapter.register(historyViewDelegate) // 搜索引擎
            historyAdapter.items = historyList
            adapter = historyAdapter
        }
    }

    private fun handleLongClick(view: View, position: Int, entity: Any) {
        // App，快捷方式，联系人长按不删除
        if (entity is AppEntity || entity is ContactEntity || entity is NewDirectEntity) return
        explosionField.explode(view)
        historyList.removeAt(position)
        historyAdapter.notifyItemRemoved(position)
        vm.handleRemove(entity, 0)
    }

    override fun startObserve() {
        super.startObserve()

        vm.run {
            historyResult.observe(viewLifecycleOwner) {
                YLog.e("receive history")
                submitEngineList(it, MMKVConstants.engineSpanCount)
            }
        }
    }

    private fun submitEngineList(it: List<Any>, spanCount: Int) {
        val callback = MultiDiffCallback(historyList, it)
        val result = DiffUtil.calculateDiff(callback)
        historyList.clear()
        historyList.addAll(it)
        result.dispatchUpdatesTo(historyAdapter)
    }
}