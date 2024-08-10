package luyao.direct.ui.engine

import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.adapter.EngineAddViewDelegate
import luyao.direct.adapter.EngineEditViewDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.base.DirectBaseFragment
import luyao.direct.databinding.FragmentSearchEngineEditBinding
import luyao.direct.model.AppDatabase
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.AddEntity
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.ui.DirectActivity
import luyao.direct.view.EngineEditDialog
import luyao.direct.view.KtxItemTouchHelperCallback
import luyao.direct.vm.DataViewModel
import luyao.ktx.coroutine.coroutineExceptionHandler
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.itemPadding
import luyao.ktx.ext.showNoteDialog
import luyao.ktx.ext.startActivity

/**
 * 搜索引擎管理
 * author: luyao
 * date:   2021/10/29 10:06
 */
class SearchEngineEditFragment : DirectBaseFragment(R.layout.fragment_search_engine_edit) {

    private val binding: FragmentSearchEngineEditBinding by viewbind()
    private val vm by activityViewModels<DataViewModel>()
    private val engineEditViewDelegate by lazy { EngineEditViewDelegate() }
    private val engineAddViewDelegate by lazy { EngineAddViewDelegate() }
    private val multiTypeAdapter = MultiTypeAdapter()
    private val searchEngineList = arrayListOf<Any>()
    private val engineDialog by lazy {
        EngineEditDialog(requireActivity() as DirectActivity, null).apply {
            setOnDismissListener {
                vm.updateSearchEngine(true)
            }
        }
    }
    private val addEntity = AddEntity()

    override fun initView() {
        binding.engineIconTv.setOnClickListener {
            help()
        }
        binding.engineHelp.setOnClickListener {
            help()
        }
        initRecyclerView()
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        vm.updateSearchEngine()
    }

    private fun initRecyclerView() {
        engineEditViewDelegate.setClickListener { it, view, position ->
            engineDialog.setDeleteListener {
                (requireActivity() as DirectActivity).explosionField.explode(view)
                searchEngineList.removeAt(position)
                multiTypeAdapter.notifyItemRemoved(position)
            }
            engineDialog.setEntity(it, view).show()
        }
        engineAddViewDelegate.setClickListener {
            showPopMenu(it)
        }

        multiTypeAdapter.run {
            register(engineEditViewDelegate)
            register(engineAddViewDelegate)
            items = searchEngineList
        }
        binding.engineRecyclerView.run {
            itemPadding(0, dp2px(8).toInt(), 0, 0)
            layoutManager = GridLayoutManager(context, MMKVConstants.engineSpanCount)
            adapter = multiTypeAdapter
        }
        touchHelper.attachToRecyclerView(binding.engineRecyclerView)
    }

    override fun startObserve() {

        vm.searchEngineList.observe(this) {
            val result = arrayListOf<Any>().apply {
                addAll(it.data)
                add(addEntity)
            }
            submitList(result)
        }

        vm.panelBgColor.observe(this) {
            arrayListOf(binding.engineIconTv).forEach { _ ->
//                it.setProperTextColor()
            }
        }

        vm.engineSpanCount.observe(this) { count ->
            binding.engineRecyclerView.layoutManager.run {
                if (this is GridLayoutManager)
                    spanCount = count
            }
        }

        vm.iconUri.observe(this) {
            if (engineDialog.isShowing) {
                engineDialog.setLocalIcon(it)
            }
        }

        vm.selectApp.observe(viewLifecycleOwner) {
            if (engineDialog.isShowing) {
                engineDialog.showSelectAppIcon(it)
            }
        }

        vm.run {

            specifyApp.observe(viewLifecycleOwner) {
                if (engineDialog.isShowing) {
                    engineDialog.setSpecifyApp(it)
                }
            }
        }
    }

    private fun submitList(it: List<Any>) {
        val callback = MultiDiffCallback(searchEngineList, it)
        val result = DiffUtil.calculateDiff(callback)
        searchEngineList.clear()
        searchEngineList.addAll(it)
        result.dispatchUpdatesTo(multiTypeAdapter)
    }

    private val touchHelper = ItemTouchHelper(KtxItemTouchHelperCallback(
        dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP,
        adapter = multiTypeAdapter,
        dataList = searchEngineList,
        onClearView = {
            searchEngineList.forEachIndexed { index, any ->
                if (any is NewDirectEntity) {
                    any.order = index
                }
            }
            lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                searchEngineList.filterIsInstance<NewDirectEntity>().forEach {
                    AppDatabase.getInstance(DirectApp.App).newDirectDao()
                        .updateEngineOrder(it.id, it.order)
                }
                withContext(Dispatchers.Main) {
                    vm.updateSearchEngine(needDiff = true)
                }
            }
        }
    ))

    private fun help() {
        requireActivity().showNoteDialog(message = getString(R.string.manager_search_engine_note))
    }

    private fun showPopMenu(view: View) {
        PopupMenu(requireActivity(), view, Gravity.BOTTOM).run {
            menuInflater.inflate(R.menu.menu_add_engine, menu)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.addFromUser) {
                    engineDialog.setEntity(null).show()
                } else if (it.itemId == R.id.addFromServer) {
                    startActivity<EngineManageActivity>()
                }
                true
            }
            show()
        }
    }
}