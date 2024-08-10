package luyao.direct.ui.settings.direct

import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.R
import luyao.direct.adapter.AppDirectListener
import luyao.direct.adapter.AppDirectViewDelegate
import luyao.direct.adapter.AppLocalDirectViewDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityAppDirectBinding
import luyao.direct.model.entity.AppDirect
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.view.DirectEditDialog
import luyao.direct.vm.DirectVM
import luyao.ktx.ext.isAppInstalled

/**
 * Description: 展示指定应用的快捷方式列表
 * Author: luyao
 * Date: 2022/10/18 11:10
 */
@AndroidEntryPoint
class AppDirectListActivity : DirectBaseActivity() {

    private val binding: ActivityAppDirectBinding by viewbind()
    private val directVM: DirectVM by viewModels()
    private val appPackageName by lazy { intent?.getStringExtra("packageName") ?: packageName }

    // false 仅展示 db 中的
    // true  仅显示所有 exported 的
    private val showAll by lazy { intent?.getBooleanExtra("showAll", false) ?: false }
    private val multiTypeAdapter = MultiTypeAdapter()
    private lateinit var directEditDialog: DirectEditDialog

    // 数据库中存储的快捷方式
    private val appDirectViewDelegate = AppDirectViewDelegate(this, object : AppDirectListener {
        override fun onCheckChanged(direct: NewDirectEntity, newValue: Boolean) {
            directVM.updateDirectAsync(direct, newValue)
        }

        override fun onClick(direct: NewDirectEntity) {
            directEditDialog.setDirect(direct)
            directEditDialog.show()
        }
    })

    // exported 为 true 的 activity
    private val appLocalDirectViewDelegate = AppLocalDirectViewDelegate().apply {
        setCheckChangeListener { appDirect, b ->
            directVM.updateDirectAsync(appDirect, b)
        }
    }
    private val directList = mutableListOf<Any>()

    val openDocument =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (directEditDialog.isShowing) {
                directEditDialog.handleIconUri(uri)
            }

        }

    // DirectEditDialog 图标选择
    val chooseAppIcon =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            activityResult.data?.let {
                val packageName = it.getStringExtra("packageName") ?: ""
                if (directEditDialog.isShowing) {
                    directEditDialog.setAppIcon(packageName)
                }
            }
        }

    override fun initView() {
        val appName = if (isAppInstalled(appPackageName)) packageManager.getPackageInfo(
            appPackageName,
            0
        ).applicationInfo.loadLabel(packageManager) else ""
        configToolbar(binding.titleLayout.toolBar, appName.toString())
        configRootInset(binding.root)
        initRv()
        directEditDialog = DirectEditDialog(this) {
            if (isAppInstalled(appPackageName)) {
                directVM.loadAppDirect(appPackageName)
            }
        }
    }

    override fun initData() {
        if (isAppInstalled(appPackageName)) {
            directVM.loadAppDirect(appPackageName, showAll)
        }
    }

    private fun initRv() {
        binding.directRv.run {
            layoutManager = LinearLayoutManager(this@AppDirectListActivity)
            multiTypeAdapter.register(appDirectViewDelegate)
            multiTypeAdapter.register(appLocalDirectViewDelegate)
            multiTypeAdapter.items = directList
            adapter = multiTypeAdapter
        }
    }

    private fun submitDirectList(it: List<Any>) {
        val callback = MultiDiffCallback(directList, it)
        val result = DiffUtil.calculateDiff(callback)
        directList.clear()
        directList.addAll(it)
        result.dispatchUpdatesTo(multiTypeAdapter)
    }

    override fun observe() {
        super.observe()
        directVM.run {
            directData.observe(this@AppDirectListActivity) {
                submitDirectList(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_all_select, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_select_all) {
            directVM.selectAll(directList)
            directList.forEach {
                if (it is NewDirectEntity) {
                    it.enabled = 0
                } else if (it is AppDirect) {
                    it.enabled = true
                }
            }
            multiTypeAdapter.notifyItemRangeChanged(0, directList.size)
        }
        return super.onOptionsItemSelected(item)
    }
}