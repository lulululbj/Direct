package luyao.direct.view

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import luyao.direct.adapter.HomeMenuOrderViewDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.databinding.DialogMenuOrderBinding
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.HomeMenu
import luyao.direct.model.entity.HomeMenuId
import luyao.direct.model.entity.HomeMenuList
import luyao.direct.util.MoshiUtil
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.itemPadding
import java.util.*

class MenuOrderDialog(context: Context) : Dialog(context) {

    private val binding = DialogMenuOrderBinding.inflate(layoutInflater)
    private val homeMenuAdapter = MultiTypeAdapter()
    private val homeMenuOrderViewDelegate = HomeMenuOrderViewDelegate()
    private val menuList = arrayListOf<HomeMenu>()
    private val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val flag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(flag, 0)
        }

        override fun isLongPressDragEnabled() = false

        override fun isItemViewSwipeEnabled() = false

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.bindingAdapterPosition
            val toPosition = target.bindingAdapterPosition
            var i = fromPosition
            if (fromPosition < toPosition) {
                while (i < toPosition) {
                    Collections.swap(menuList, i, i + 1)
                    i++
                }
            } else {
                while (i > toPosition) {
                    Collections.swap(menuList, i, i - 1)
                    i--
                }
            }
            homeMenuAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            MMKVConstants.homeMenuOrderJson = MoshiUtil.homeMenuListAdapter.toJson(menuList)
        }
    })

    init {
        initView()
        setContentView(binding.root)
        window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        initData()
    }

    private fun initView() {
        binding.root.run {
            itemPadding(dp2px(8).toInt())
            homeMenuOrderViewDelegate.setOnDragListener(touchHelper)
            layoutManager = GridLayoutManager(context, 4)
            homeMenuAdapter.run {
                register(homeMenuOrderViewDelegate)
                items = menuList
            }
            adapter = homeMenuAdapter
            touchHelper.attachToRecyclerView(this)
        }
    }

    private fun initData() {
        if (MMKVConstants.homeMenuOrderJson.isEmpty()) {
            submitMenuList(HomeMenuList)
        } else {
            val menuList = MoshiUtil.homeMenuListAdapter.fromJson(MMKVConstants.homeMenuOrderJson)
                ?.filter { it.id != HomeMenuId.RECENT && it.id != HomeMenuId.HISTORY }
            if (menuList != null) {
                submitMenuList(menuList)
            }
        }
    }

    private fun submitMenuList(it: List<HomeMenu>) {
        val callback = MultiDiffCallback(menuList, it)
        val result = DiffUtil.calculateDiff(callback)
        menuList.clear()
        menuList.addAll(it)
        result.dispatchUpdatesTo(homeMenuAdapter)
    }

}