package luyao.direct.view

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.databinding.DialogEngineBinding
import luyao.direct.ext.getExPinyin
import luyao.direct.ext.getPinyin
import luyao.direct.ext.newUID
import luyao.direct.model.AppDatabase
import luyao.direct.model.AppIconCache
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.ui.ChooseAppActivity
import luyao.direct.ui.DirectActivity
import luyao.direct.util.loadIcon
import luyao.ktx.ext.*
import java.util.*

/**
 * 搜索引擎新增、编辑弹窗
 * author: luyao
 * date:   2021/10/29 13:28
 */
class EngineEditDialog(
    private val activity: DirectActivity,
    private var directEntity: NewDirectEntity? = null
) : Dialog(activity) {

    private val iconWidth = dp2px(32).toInt()
    private var userIconUrl = ""
    private var localBitmap: Bitmap? = null
    private var itemView: View? = null
    val binding = DialogEngineBinding.inflate(layoutInflater)
    private var deleteListener: ((NewDirectEntity) -> Unit)? = null
    private var specifyAppPackageName: String = ""

    fun setDeleteListener(listener: (NewDirectEntity) -> Unit) {
        deleteListener = listener
    }

    init {
        initView()
        setContentView(binding.root)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun initView() {
        setEntity(directEntity)
        binding.run {
            engineIcon.setOnClickListener {
                showPopMenu(it)
            }
            engineConfirm.setOnClickListener { attemptSave() }
            engineDelete.setOnClickListener {
                activity.showConfirmDialog(message = activity.getString(R.string.Confirm_delete)) {
                    attemptDelete()
                }
            }
            engineTest.setOnClickListener { testEngine() }
            specifyApp.setOnClickListener {
                if (specifyAppPackageName.isEmpty()) {
                    activity.specifyAppLauncher.launch(
                        Intent(
                            activity,
                            ChooseAppActivity::class.java
                        )
                    )
                } else {
                    specifyAppPackageName = ""
                    setSpecifyApp("")
                }

            }
            engineHelp.setOnClickListener { activity.showNoteDialog(R.string.engine_help) }
        }

    }

    fun setEntity(entity: NewDirectEntity?, view: View? = null): EngineEditDialog {
        itemView = view
        localBitmap = null
        directEntity = entity
        binding.run {
            if (entity == null) {
                userIconUrl = ""
                engineIcon.setImageResource(R.drawable.ic_circlr_add)
                specifyApp.setImageResource(R.drawable.ic_circlr_add)
                specifyAppIcon.isVisible = false
                specifyAppPackageName = ""
                engineName.setText("")
                engineUrl.setText("")
                engineScheme.setText("")
                engineTag.setText("")
                panelSwitch.isChecked = true
            } else {
                setSpecifyApp(entity.packageName)
                entity.loadIcon(engineIcon)
                engineName.setText(entity.label)
                engineTag.setText(entity.tag)
                engineUrl.setText(entity.searchUrl)
                engineScheme.setText(entity.scheme)
                panelSwitch.isChecked = entity.showPanel == 1
                userIconUrl = entity.iconUrl ?: ""
            }
        }
        return this
    }

    @SuppressLint("CheckResult")
    private fun showPopMenu(view: View) {
        PopupMenu(activity, view, Gravity.BOTTOM).run {
            menuInflater.inflate(R.menu.menu_choose_icon, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.imageUrl -> {
                        MaterialDialog(activity).show {
                            title(R.string.input_image_address)
                            input(allowEmpty = false) { _, text ->
                                getRemoteImage(text.toString())
                            }
                            positiveButton(R.string.Confirm)
                        }
                    }
                    R.id.chooseFile -> {
                        activity.openDocument.launch(("image/*"))
                    }
                    R.id.chooseApp -> {
                        activity.chooseAppLauncher.launch(
                            Intent(
                                activity,
                                ChooseAppActivity::class.java
                            )
                        )
                    }
                }
                true
            }
            show()
        }
    }

    fun setLocalIcon(uri: Uri?) {
        uri?.let {
            DirectApp.App.contentResolver.openInputStream(it).use { input ->
                input?.run {
                    val bitmap = BitmapFactory.decodeStream(this)
                    val scaleBitmap = bitmap.scale(dp2px(32).toInt(), dp2px(32).toInt())
                    binding.engineIcon.setImageBitmap(scaleBitmap)
                    localBitmap = scaleBitmap
                    userIconUrl = ""
                }
            }
        }
    }

    private fun attemptSave() {
        val name = binding.engineName.text.toString()
        val url = binding.engineUrl.text.toString()
        val scheme = binding.engineScheme.text.toString()

        if (name.isEmpty()) {
            toast(R.string.please_input_engine_name)
            return
        }

        if (url.isEmpty() && scheme.isEmpty() && specifyAppPackageName.isEmpty()) {
            toast(R.string.add_engine_input_note)
            return
        }

        activity.lifecycleScope.launch {
            binding.engineConfirm.visibility = View.INVISIBLE
            binding.engineProgress.visibility = View.VISIBLE

            val deferred = async(Dispatchers.IO) {
                try {
                    if (directEntity == null) { // 新增
                        val localIcon = if (localBitmap == null) {
                            ""
                        } else {
                            Base64.encodeToString(localBitmap!!.toByteArray(), Base64.DEFAULT)
                        }
                        val engineList =
                            AppDatabase.getInstance(DirectApp.App).directDao()
                                .getAllSearchEngine()
                        var order = 0
                        val start = 100000 // 从 100000 开始为用户手动添加

                        if (engineList.isNotEmpty()) {
                            order = engineList[engineList.size - 1].order + 1
                        }

                        val sortById = engineList.sortedBy { it.id }
                        val id = if (engineList.isEmpty()) start else {
                            val lastId = sortById[engineList.size - 1].id
                            if (lastId < start) start else lastId + 1
                        }
                        val directEntity = NewDirectEntity(
                            newUID(),
                            name,
                            "",
                            specifyAppPackageName,
                            "",
                            scheme,
                            name.getPinyin(),
                            name.getExPinyin(),
                            userIconUrl,
                            1,
                            url,
                            order,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            localIcon,
                            binding.engineTag.text.toString(),
                            if (binding.panelSwitch.isChecked) 1 else 0
                        )
                        AppDatabase.getInstance(DirectApp.App).newDirectDao().insert(directEntity)
                    } else { // 修改
                        directEntity.run {
                            val localIcon = if (localBitmap == null) {
                                this!!.localIcon
                            } else {
                                Base64.encodeToString(localBitmap!!.toByteArray(), Base64.DEFAULT)
                            }
                            AppDatabase.getInstance(DirectApp.App).newDirectDao()
                                .update(
                                    this!!.id,
                                    name,
                                    url,
                                    scheme,
                                    localIcon,
                                    binding.engineTag.text.toString(),
                                    if (binding.panelSwitch.isChecked) 1 else 0,
                                    specifyAppPackageName,
                                    userIconUrl
                                )
                        }
                    }
                    true
                } catch (e: Exception) {
                    false
                }
            }
            if (deferred.await()) {
                binding.engineConfirm.visibility = View.VISIBLE
                binding.engineProgress.visibility = View.INVISIBLE
                dismiss()
            } else {
                binding.engineConfirm.visibility = View.VISIBLE
                binding.engineProgress.visibility = View.INVISIBLE
                toast(R.string.save_fail)
            }
        }
    }

    private fun attemptDelete() {
        activity.lifecycleScope.launch {
            val deferred = async(Dispatchers.IO) {
                if (directEntity != null && itemView != null) {
                    AppDatabase.getInstance(context).newDirectDao()
                        .disableSearchEngine(directEntity!!.id)
                    true
                } else {
                    false
                }
            }
            if (deferred.await()) {
                deleteListener?.invoke(directEntity!!)
            }
            dismiss()
        }
    }

    fun showSelectAppIcon(packageName: String) {
        localBitmap = activity.packageManager.run {
            getPackageInfo(packageName, 0).applicationInfo.loadIcon(this)
                .toBitmap(width = iconWidth, height = iconWidth)
        }
        binding.engineIcon.setImageBitmap(localBitmap)
        userIconUrl = ""
    }

    private fun testEngine() {
        val url = binding.engineUrl.text.toString()
        val scheme = binding.engineScheme.text.toString()

        if (url.isEmpty() && scheme.isEmpty() && specifyAppPackageName.isEmpty()) {
            toast(R.string.add_engine_input_note)
            return
        }

        try {
            if (scheme.isNotEmpty()) {
                val intent = Intent.parseUri(
                    String.format(
                        Locale.getDefault(),
                        scheme,
                        activity.getString(R.string.app_name)
                    ),
                    Intent.URI_INTENT_SCHEME
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else if (url.isNotEmpty()) {
                val browserUrl = String.format(
                    Locale.getDefault(),
                    url,
                    activity.getString(R.string.app_name)
                )
                context.openBrowser(browserUrl, MMKVConstants.defaultBrowser)
            }
        } catch (e: Exception) {
            toast(R.string.engine_test_fail)
        }
    }

    fun setSpecifyApp(packageName: String?) {

        if (packageName.isNullOrEmpty()) {
            binding.specifyAppIcon.isVisible = false
            specifyAppPackageName = ""
            binding.specifyApp.setImageResource(R.drawable.ic_circlr_add)
        } else {
            binding.specifyAppIcon.isVisible = true
            specifyAppPackageName = packageName
            val iconBitmap = AppIconCache.get(packageName)
            binding.specifyAppIcon.setImageBitmap(iconBitmap)
            binding.specifyApp.setImageResource(R.drawable.ic_clear_text)
        }
    }

    private fun getRemoteImage(url: String) {
        Glide.with(activity)
            .asBitmap()
            .load(url)
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    toast("图片加载失败")
                    return true
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    userIconUrl = url
                    activity.runOnUiThread {
                        binding.engineIcon.setImageBitmap(resource)
                    }

                    return true
                }
            }).submit()
    }
}