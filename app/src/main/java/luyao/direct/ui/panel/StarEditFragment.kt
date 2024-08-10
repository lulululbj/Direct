package luyao.direct.ui.panel

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hi.dhl.binding.viewbind
import luyao.direct.R
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.adapter.StarEditViewDelegate
import luyao.direct.base.DirectBaseFragment
import luyao.direct.databinding.FragmentStarEditBinding
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.RecentEntity
import luyao.direct.ui.DirectActivity
import luyao.direct.view.KtxItemTouchHelperCallback
import luyao.direct.vm.DataViewModel
import luyao.ktx.ext.showConfirmDialog
import luyao.ktx.ext.showNoteDialog

/**
 *  @author: luyao
 * @date: 2021/11/6 上午7:04
 */
class StarEditFragment : DirectBaseFragment(R.layout.fragment_star_edit) {

    private val binding: FragmentStarEditBinding by viewbind()
    private val vm by activityViewModels<DataViewModel>()
    private val multiTypeAdapter = MultiTypeAdapter()
    private val starEditViewDelegate = StarEditViewDelegate()
    private val starList = arrayListOf<RecentEntity>()
    private val isEditPage by lazy { arguments?.getBoolean(IS_EDIT_PAGE, true) ?: true }
    private var gestureConfigListener: ((RecentEntity) -> Unit)? = null

    fun setGestureConfigListener(listener: ((RecentEntity) -> Unit)? = null) {
        gestureConfigListener = listener
    }

    companion object {
        const val IS_EDIT_PAGE = "isEditPage"
        fun newInstance(canEdit: Boolean = true) = StarEditFragment().apply {
            arguments = Bundle().apply {
                putBoolean(IS_EDIT_PAGE, canEdit)
            }
        }
    }

    override fun initView() {
        binding.run {
            starEditTv.setOnClickListener { help() }
            starHelp.setOnClickListener { help() }
            starHelp.isVisible = isEditPage
            starEditTv.isVisible = isEditPage
        }
        initRecyclerView()
    }

    override fun initData() {
//        if (!isEditPage) {
//            search()
//        }
    }

    fun search(keyWord: String = "") {
        vm.search(MMKVConstants.starTag, keyWord)
    }

    override fun startObserve() {
        vm.starResult.observe(viewLifecycleOwner) {
            submitEngineList(it.data)
        }

        vm.panelBgColor.observe(viewLifecycleOwner) {
//            binding.starEditTv.setProperTextColor()
        }
    }

    private fun initRecyclerView() {
        starEditViewDelegate.setItemClickListener { view, position, recentEntity ->
            if (isEditPage) {
                showConfirmDialog(
                    message = getString(R.string.confirm_delete_star),
                    onConfirm = {
                        (requireActivity() as DirectActivity).explosionField.explode(view)
                        vm.deleteStar(recentEntity)
                    }
                )
            } else {
                gestureConfigListener?.invoke(recentEntity)
            }
        }
        multiTypeAdapter.run {
            register(starEditViewDelegate)
            items = starList
            binding.starRecyclerView.layoutManager =
                GridLayoutManager(
                    requireContext(),
                    if (isEditPage) MMKVConstants.engineSpanCount else 4
                )
            binding.starRecyclerView.adapter = this
            if (isEditPage) {
                touchHelper.attachToRecyclerView(binding.starRecyclerView)
            }
        }
    }

    private fun submitEngineList(it: List<RecentEntity>) {
        val callback = MultiDiffCallback(starList, it)
        val result = DiffUtil.calculateDiff(callback)
        starList.clear()
        starList.addAll(it)
        result.dispatchUpdatesTo(multiTypeAdapter)
    }

    private val touchHelper = ItemTouchHelper(KtxItemTouchHelperCallback(
        dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or
                ItemTouchHelper.DOWN or ItemTouchHelper.UP,
        dataList = starList,
        adapter = multiTypeAdapter,
        onClearView = {
            vm.updateStarOrder(starList)
        }
    ))

    private fun help() {
        requireActivity().showNoteDialog(message = getString(R.string.manage_star_note))
    }
}