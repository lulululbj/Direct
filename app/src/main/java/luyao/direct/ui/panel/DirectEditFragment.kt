package luyao.direct.ui.panel

import android.Manifest
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import luyao.direct.R
import luyao.direct.adapter.DirectCountViewDelegate
import luyao.direct.adapter.EngineAddViewDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.base.DirectBaseFragment
import luyao.direct.databinding.FragmentDirectEditBinding
import luyao.direct.model.entity.AddEntity
import luyao.direct.ui.ChooseAppActivity
import luyao.direct.ui.DirectActivity
import luyao.direct.ui.settings.direct.AppDirectListActivity
import luyao.direct.util.showCollectFloatView
import luyao.direct.vm.DataViewModel
import luyao.ktx.ext.showConfirmDialog
import luyao.ktx.ext.startActivity
import luyao.ktx.util.YLog

/**
 * Description: 快捷方式管理
 * Author: luyao
 * Date: 2022/10/18 08:56
 */
class DirectEditFragment : DirectBaseFragment(R.layout.fragment_direct_edit) {

    private val binding: FragmentDirectEditBinding by viewbind()
    private val multiTypeAdapter = MultiTypeAdapter()
    private val directCountViewDelegate = DirectCountViewDelegate()
    private val directAddViewDelegate by lazy { EngineAddViewDelegate() }
    private val directList = mutableListOf<Any>()
    private val vm by activityViewModels<DataViewModel>()
    private val addEntity = AddEntity()

    override fun initView() {
        initRv()
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as DirectActivity).updateDirect()
    }

    private fun initRv() {
        binding.directRecyclerView.run {
            layoutManager = GridLayoutManager(requireContext(), 5)
            directCountViewDelegate.setItemClickListener { _, _, directCount ->
                // 展示指定 packageName 对应的 db 中的快捷方式
                requireActivity().startActivity<AppDirectListActivity>(
                    "packageName" to directCount.packageName,
                    "showAll" to false
                )
            }
            directAddViewDelegate.setClickListener {
                showPopMenu(it)
            }
            multiTypeAdapter.register(directCountViewDelegate)
            multiTypeAdapter.register(directAddViewDelegate)
            multiTypeAdapter.items = directList
            adapter = multiTypeAdapter
        }
    }

    override fun startObserve() {
        super.startObserve()
        vm.run {
            directCountData.observe(viewLifecycleOwner) {
                val result = arrayListOf<Any>().apply {
                    addAll(it)
                    add(addEntity)
                }
                submitDirectList(result)
            }

            selectDirectApp.observe(viewLifecycleOwner) {
                // 展示指定 packageName 对应的快捷方式
                requireActivity().startActivity<AppDirectListActivity>(
                    "packageName" to it,
                    "showAll" to true
                )
            }
        }
    }

    private fun submitDirectList(it: List<Any>) {
        val callback = MultiDiffCallback(directList, it)
        val result = DiffUtil.calculateDiff(callback)
        directList.clear()
        directList.addAll(it)
        result.dispatchUpdatesTo(multiTypeAdapter)
    }

    private fun showPopMenu(view: View) {
        PopupMenu(requireActivity(), view, Gravity.BOTTOM).run {
            menuInflater.inflate(R.menu.menu_add_direct, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.add_new_direct -> {
                        (requireActivity() as DirectActivity).showDirectEditDialog()
                    }
                    R.id.choose_app_direct -> {
                        (requireActivity() as DirectActivity).chooseDirectAppLauncher.launch(
                            Intent(
                                activity,
                                ChooseAppActivity::class.java
                            )
                        )
                    }
                    R.id.collect_direct -> {
                        startCollect()
                    }
                }
                true
            }
            show()
        }
    }

    private fun startCollect() {

        showConfirmDialog(getString(R.string.collect), "采集快捷方式需要您授予 使用情况访问权限 和 悬浮窗权限 后，在需要快捷跳转的目标页面，点击右下角悬浮按钮进行采集。\n\n另外，大部分目标页面需要 Root 权限方可启动，添加前请注意测试。") {
            XXPermissions.with(this)
                .permission(Manifest.permission.PACKAGE_USAGE_STATS)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (all) {
                            showCollectFloatView()
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            YLog.e("never")
                        } else {
                            YLog.e("no")
                        }
                    }
                })
        }

//        PermissionX.init(this)
//            .permissions(Manifest.permission.PACKAGE_USAGE_STATS)
//            .onExplainRequestReason { scope, deniedList ->
//                scope.showRequestReasonDialog(
//                    deniedList,
//                    getString(R.string.write_backup_note),
//                    getString(R.string.permission_again),
//                    getString(R.string.permission_no_need)
//                )
//            }
//            .onForwardToSettings { scope, deniedList ->
//                scope.showForwardToSettingsDialog(
//                    deniedList,
//                    getString(R.string.never_ask_note),
//                    getString(R.string.go_to_setting),
//                    getString(R.string.suan_le)
//                )
//            }
//            .request { allGranted, _, _ ->
//                if (allGranted) {
//
//                }
//            }
    }

}