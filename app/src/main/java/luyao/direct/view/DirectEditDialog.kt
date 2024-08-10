package luyao.direct.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.hi.dhl.binding.viewbind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.databinding.DialogDirectEditBinding
import luyao.direct.ext.getExPinyin
import luyao.direct.ext.getPinyin
import luyao.direct.ext.newUID
import luyao.direct.model.AppDatabase
import luyao.direct.model.ExecMode
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.ui.ChooseAppActivity
import luyao.direct.ui.DirectActivity
import luyao.direct.ui.settings.direct.AppDirectListActivity
import luyao.direct.util.CommandUtil
import luyao.direct.util.loadIcon
import luyao.ktx.ext.*

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/19 14:15
 */
class DirectEditDialog(val activity: FragmentActivity, private val onRefresh: () -> Unit) :
    Dialog(activity) {

    private val iconWidth = dp2px(32).toInt()
    private val binding: DialogDirectEditBinding by viewbind()
    private var directEntity: NewDirectEntity? = null
    private var localBitmap: Bitmap? = null
    private var selectPackageName = ""
    private var selectAppName = ""
    private var directDao = AppDatabase.getInstance(DirectApp.App).newDirectDao()
//    private val openDocument =
//        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            handleIconUri(uri)
//        }

    fun handleIconUri(uri: Uri?) {
        uri?.let {
            DirectApp.App.contentResolver.openInputStream(it).use { input ->
                input?.run {
                    val bitmap = BitmapFactory.decodeStream(this)
                    val scaleBitmap = bitmap.scale(iconWidth, iconWidth)
                    binding.directIcon.setImageBitmap(scaleBitmap)
                    localBitmap = scaleBitmap
                }
            }
        }
    }


    fun setAppIcon(packageName: String) {
        val packageInfo = activity.packageManager.getPackageInfo(packageName, 0)
        localBitmap =
            packageInfo.applicationInfo.loadIcon(activity.packageManager)
                .toBitmap(width = iconWidth, height = iconWidth)
        binding.directIcon.setImageBitmap(localBitmap)
    }

    init {
        initView()
        setContentView(binding.root)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun initView() {
        setDirect(directEntity)
        binding.run {
            directIcon.setOnClickListener {
                showPopMenu(it)
            }
            directApp.setOnClickListener {
                if (activity is DirectActivity) {
                    activity.chooseAppIcon.launch(
                        Intent(
                            activity,
                            ChooseAppActivity::class.java
                        )
                    )
                } else if (activity is AppDirectListActivity) {
                    activity.chooseAppIcon.launch(
                        Intent(
                            activity,
                            ChooseAppActivity::class.java
                        )
                    )
                }
            }
            directDelete.setOnClickListener { attemptDelete() }
            directConfirm.setOnClickListener { saveAppDirect() }
            directTest.setOnClickListener { testDirect() }
        }
    }

    private fun showPopMenu(view: View) {
        PopupMenu(activity, view, Gravity.BOTTOM).run {
            menuInflater.inflate(R.menu.menu_choose_icon, menu)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.chooseFile) {
                    if (activity is DirectActivity) {
                        activity.openDocument.launch(("image/*"))
                    } else if (activity is AppDirectListActivity) {
                        activity.openDocument.launch(("image/*"))
                    }
                } else if (it.itemId == R.id.chooseApp) {
                    if (activity is DirectActivity) {
                        activity.chooseAppIcon.launch(
                            Intent(activity, ChooseAppActivity::class.java)
                        )
                    } else if (activity is AppDirectListActivity) {
                        activity.chooseAppIcon.launch(
                            Intent(activity, ChooseAppActivity::class.java)
                        )
                    }
                }
                true
            }
            show()
        }
    }

    private fun testDirect() {
        val scheme = binding.directScheme.text.toString()

        if (scheme.isEmpty()) {
            toast(R.string.please_input_direct_scheme)
            return
        }

        if (binding.rootSwitch.isChecked) {
            CommandUtil.rootExec(scheme)
        } else {
            context.startScheme(scheme) {
                toast(R.string.scheme_unavaliable)
            }
        }

    }

    fun setDirect(direct: NewDirectEntity?) {
        this.directEntity = direct
        this.selectPackageName = direct?.packageName ?: ""
        this.selectAppName = direct?.appName ?: ""
        binding.run {
            if (direct == null) {
                directIcon.setImageResource(R.drawable.ic_circlr_add)
                directName.setText("")
                directApp.text = ""
                directScheme.setText("")
            } else {
                direct.loadIcon(directIcon)
                directName.setText(direct.label)
                directApp.text = direct.appName
                directScheme.setText(direct.scheme)
                rootSwitch.isChecked = direct.execMode == ExecMode.ROOT
            }
        }
    }

    fun setDirect(packageName: String, scheme: String, exported: Boolean) {
        setPackageName(packageName)
        binding.directScheme.setText(scheme)
        binding.rootNote.isVisible = !exported
        binding.rootSwitch.isChecked = !exported
    }

    private fun saveAppDirect() {
        binding.run {
            val label = directName.text.toString()
            val scheme = directScheme.text.toString()

            if (label.isEmpty()) {
                toast(R.string.please_input_scheme_name)
                return@run
            }

            if (scheme.isEmpty()) {
                toast(R.string.please_input_direct_scheme)
                return@run
            }

            activity.lifecycleScope.launch {
                binding.directConfirm.visibility = View.INVISIBLE
                binding.directProgress.visibility = View.VISIBLE
                val execMode = if (binding.rootSwitch.isChecked) ExecMode.ROOT else ExecMode.SCHEME
                val deferred = async(Dispatchers.IO) {
                    val localIcon = if (localBitmap == null) {
                        ""
                    } else {
                        Base64.encodeToString(localBitmap!!.toByteArray(), Base64.DEFAULT)
                    }
                    if (directEntity == null) { // 新增
                        // 不存在，新增。用户手动新增的快捷方式 id 从 200000 开始
//                        val start = 200000
//                        val userDirectList = directDao.getAllDirect().filter { it.id >= start }
//                        val id =
//                            if (userDirectList.isEmpty()) start else userDirectList.last().id + 1

                        val directEntity = NewDirectEntity(
                            newUID(),
                            label,
                            selectAppName,
                            selectPackageName,
                            "",
                            scheme,
                            label.getPinyin(),
                            label.getExPinyin(),
                            "",
                            0,
                            "",
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            localIcon,
                            "",
                            0,
                            execMode
                        )
                        directDao.insert(directEntity)
                    } else { // 更新
                        directDao.updateDirect(
                            directEntity!!.id,
                            label,
                            scheme,
                            selectAppName,
                            selectPackageName,
                            localIcon,
                            execMode
                        )
                    }
                    true
                }
                if (deferred.await()) {
                    binding.directConfirm.visibility = View.VISIBLE
                    binding.directProgress.visibility = View.INVISIBLE
                    dismiss()
                } else {
                    binding.directConfirm.visibility = View.VISIBLE
                    binding.directProgress.visibility = View.INVISIBLE
                    toast(R.string.save_fail)
                }
                onRefresh.invoke()
            }
        }
    }

    private fun attemptDelete() {
        directEntity?.let {
            activity.showConfirmDialog(
                activity.getString(R.string.note),
                activity.getString(R.string.Confirm_delete)
            ) {
                activity.lifecycleScope.launch(Dispatchers.IO) {
                    directDao.deleteById(it.id)
                    onRefresh.invoke()
                }
                dismiss()
            }
        }
    }

    private fun setPackageName(packageName: String) {
        selectPackageName = packageName
        val packageInfo = activity.packageManager.getPackageInfo(packageName, 0)
        val appName =
            packageInfo.applicationInfo.loadLabel(activity.packageManager).toString()
        selectAppName = appName
        binding.directApp.text = appName
        if (localBitmap == null) {
            setAppIcon(packageName)
        }
    }
}