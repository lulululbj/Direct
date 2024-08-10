package luyao.direct.ui.gesture

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.R
import luyao.direct.adapter.DirectSelectDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.base.DirectBaseFragment
import luyao.direct.databinding.FragmentChooseDirectBinding
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.model.entity.RecentEntity
import luyao.direct.model.entity.toRecentEntity
import luyao.direct.vm.DataViewModel

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/6 06:36
 */
@AndroidEntryPoint
class ChooseDirectFragment : DirectBaseFragment(R.layout.fragment_choose_direct) {

    private val binding: FragmentChooseDirectBinding by viewbind()
    private val dataVM by activityViewModels<DataViewModel>()
    private val multiTypeAdapter = MultiTypeAdapter()
    private val directViewDelegate = DirectSelectDelegate()
    private val directList = arrayListOf<NewDirectEntity>()
    private var gestureConfigListener: ((RecentEntity) -> Unit)? = null

    fun setGestureConfigListener(listener: ((RecentEntity) -> Unit)? = null) {
        gestureConfigListener = listener
    }

    override fun initView() {
        initRv()
    }

    override fun initData() {
//        dataVM.getAllDirect()
    }

    fun search(keyword: String = "") {
        dataVM.searchDirect(keyword)
    }

    private fun initRv() {
        binding.run {
            directViewDelegate.setItemClickListener { _, _, directEntity ->
                gestureConfigListener?.invoke(directEntity.toRecentEntity())
            }
            multiTypeAdapter.run {
                register(directViewDelegate)
                items = directList
                directRv.layoutManager = GridLayoutManager(requireContext(), 4)
                directRv.adapter = this
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        dataVM.run {
            allDirectListData.observe(viewLifecycleOwner) {
                submitDirectList(it)
            }
        }
    }

    private fun submitDirectList(it: List<NewDirectEntity>) {
        val callback = MultiDiffCallback(directList, it)
        val result = DiffUtil.calculateDiff(callback)
        directList.clear()
        directList.addAll(it)
        result.dispatchUpdatesTo(multiTypeAdapter)
    }
}