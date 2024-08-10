package luyao.direct.ui

import android.content.Intent
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.R
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.adapter.SelectAppViewDelegate
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityChooseAppBinding
import luyao.direct.model.entity.AppEntity
import luyao.direct.vm.SelectAppVM

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/8 22:19
 */
@AndroidEntryPoint
class ChooseAppActivity : DirectBaseActivity() {

    private val binding: ActivityChooseAppBinding by viewbind()
    private val vm by viewModels<SelectAppVM>()
    private val multiTypeAdapter = MultiTypeAdapter()
    private val selectViewDelegate = SelectAppViewDelegate(appClick = {
        handleSelectApp(it)
    },
        appLongClick = {})

    private fun handleSelectApp(it: AppEntity) {
        setResult(RESULT_OK, Intent().apply {
            putExtra("packageName", it.packageName)
        })
        finish()
    }

    private val appList = arrayListOf<AppEntity>()

    override fun initView() {
        configRootInset(binding.root)
        configToolbar(binding.titleLayout.toolBar, getString(R.string.search_app))
        initRv()
    }

    override fun initData() {
        vm.searchApp("")
        binding.searchEt.doAfterTextChanged {
            vm.searchApp(binding.searchEt.text.toString())
        }
    }

    private fun initRv() {
        selectViewDelegate.setSelectListener { appEntity, selected ->
            if (selected) handleSelectApp(appEntity)
        }
        binding.appRv.run {
            layoutManager = GridLayoutManager(this@ChooseAppActivity, 4)
            multiTypeAdapter.register(selectViewDelegate)
            multiTypeAdapter.items = appList
            adapter = multiTypeAdapter
        }
    }

    override fun observe() {
        super.observe()
        vm.appData.observe(this) {
            val callback = MultiDiffCallback(appList, it)
            val result = DiffUtil.calculateDiff(callback)
            appList.clear()
            appList.addAll(it)
            result.dispatchUpdatesTo(multiTypeAdapter)
        }
    }
}