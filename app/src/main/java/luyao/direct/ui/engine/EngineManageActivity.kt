package luyao.direct.ui.engine

import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.R
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.adapter.SelectEngineViewDelegate
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityManageEngineBinding
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.vm.EngineViewModel
import luyao.ktx.ext.showConfirmDialog

@AndroidEntryPoint
class EngineManageActivity : DirectBaseActivity() {

    private val binding: ActivityManageEngineBinding by viewbind()
    private val engineVM by viewModels<EngineViewModel>()

    private val engineList = arrayListOf<NewDirectEntity>()
    private val engineAdapter by lazy { MultiTypeAdapter() }
    private val engineViewDelegate by lazy {
        SelectEngineViewDelegate().apply {
            setCheckChangeListener { directEntity, isChecked ->
                showConfirmDialog(message = getString(R.string.cofirm_add_this_engine)) {
                    engineVM.handleEngine(directEntity,isChecked)
                }
            }
        }
    }

    override fun initView() {
        configRootInset(binding.root)
        configToolbar(binding.titleLayout.toolBar, getString(R.string.search_engine))
        engineAdapter.run {
            register(engineViewDelegate)
            items = engineList
            binding.engineRv.layoutManager = GridLayoutManager(this@EngineManageActivity, 3)
            binding.engineRv.adapter = this
        }

        binding.searchEt.doOnTextChanged { text, _, _, _ ->
            loadData(text.toString())
        }
    }

    override fun initData() {
        loadData()
    }

    private fun loadData(keyWord: String = "") {
        engineVM.loadEngine(keyWord)
    }

    override fun observe() {
        super.observe()
        engineVM.run {
            engineListData.observe(this@EngineManageActivity) {
                submitList(it)
            }
        }
    }

    private fun submitList(list: List<NewDirectEntity>) {
        val callback = MultiDiffCallback(engineList, list)
        val result = DiffUtil.calculateDiff(callback)
        engineList.clear()
        engineList.addAll(list)
        result.dispatchUpdatesTo(engineAdapter)
    }
}