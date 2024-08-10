package luyao.direct.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.hi.dhl.binding.viewbind
import com.iammert.library.AnimatedTabItemConfig
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.adapter.*
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityEverywhereBinding
import luyao.direct.databinding.ViewstubPanelBinding
import luyao.direct.ext.callPhone
import luyao.direct.ext.getChannel
import luyao.direct.ext.go
import luyao.direct.ext.initLicense
import luyao.direct.model.MMKVConstants
import luyao.direct.model.dao.saveSearchHistory
import luyao.direct.model.entity.*
import luyao.direct.ui.engine.SearchEngineEditFragment
import luyao.direct.ui.panel.DirectEditFragment
import luyao.direct.ui.panel.PanelSettingFragment
import luyao.direct.ui.panel.StarEditFragment
import luyao.direct.ui.settings.SettingsActivity
import luyao.direct.util.FLOAT_COLLECT_TAG
import luyao.direct.util.customtab.CustomTabActivityHelper
import luyao.direct.util.hideFloatView
import luyao.direct.util.loadIcon
import luyao.direct.view.*
import luyao.direct.view.windowinset.ControlFocusInsetsAnimationCallback
import luyao.direct.view.windowinset.InnerTranslateDeferringInsetsAnimationCallback
import luyao.direct.view.windowinset.RootViewDeferringInsetsCallback
import luyao.direct.view.windowinset.TranslateDeferringInsetsAnimationCallback
import luyao.direct.vm.AppViewModel
import luyao.direct.vm.DataViewModel
import luyao.direct.vm.UpdateViewModel
import luyao.ktx.ext.*
import luyao.ktx.util.TimerCounter
import luyao.ktx.util.YLog
import luyao.view.explosion.ExplosionField
import luyao.view.keyboard.KeyboardHeightObserver
import luyao.view.keyboard.KeyboardHeightProvider
import java.util.*

@AndroidEntryPoint
class DirectActivity : DirectBaseActivity(),
    CustomTabActivityHelper.ConnectionCallback, KeyboardHeightObserver {

    private val binding: ActivityEverywhereBinding by viewbind()
    private val searchResult = ArrayList<Any>()
    private val searchAdapter by lazy { MultiTypeAdapter() }
    private val appViewDelegate by lazy {
        AppViewDelegate(this,
            shareApk = {
                appVM.shareApk(it)
            },
            backupApk = {
                appVM.backupApk(it)
            })
    } // app
    private val contactViewDelegate by lazy { ContactViewDelegate() } // 联系人
    private val directViewDelegate by lazy { DirectViewDelegate(this) } // 快捷方式
    private val urlViewDelegate by lazy { UrlViewDelegate() } // 直接访问 url
    private val pasteViewDelegate by lazy { // 粘贴内容
        PasteViewDelegate(
            clickPaste = {
                binding.searchEt.setText(it)
            },
            deletePaste = {
                searchResult.removeAt(it)
                searchAdapter.notifyItemRemoved(it)
            }
        )
    }
    private val emailViewDelegate by lazy { EmailViewDelegate() } // 发送邮件
    private val tagEngineViewDelegate by lazy { TagEngineViewDelegate() }

    private var isEngineEditMode = false
    private val engineList = ArrayList<Any>()
    private val engineAdapter by lazy { MultiTypeAdapter() }
    private val recentViewDelegate by lazy {
        RecentViewDelegate(click = { view, recentEntity ->
            // 编辑模式下，点击删除星标
            // 非编辑模式，点击直接打开星标
            if (isEngineEditMode) {
                showConfirmDialog(message = getString(R.string.confirm_delete_star)) {
                    explosionField.explode(view)
                    vm.deleteStar(recentEntity)
                }
            } else {
                recentEntity.go()
                this.finish()
            }
        }, longClick = {
            // 编辑模式下，长按拖拽排序
            // 非编辑模式下，长按开启编辑模式
            if (!isEngineEditMode) {
                setEngineEditMode()
            }
        })
    } // 星标

    private val engineViewDelegate by lazy {
        EngineViewDelegate(
            engineClick = { it, view, position ->
                if (isEngineEditMode) {
                    // 编辑模式下，点击搜索引擎，进入编辑页面
                    showEngineEditDialog(it, position, view)
                } else {
                    // 非编辑模式下，有输入内容直接搜索，无输入内容，切换默认搜索引擎
                    vm.sendEngineClick(it)
                }
            },
            engineLongClick = {
                if (!isEngineEditMode) {
                    // 非编辑模式下，长按开启编辑模式
                    setEngineEditMode()
                }
            }
        )
    } // 搜索引擎

    private fun showEngineEditDialog(it: NewDirectEntity, position: Int, view: View) {
        if (engineEditDialog == null) {
            engineEditDialog = EngineEditDialog(this, null).apply {
                setOnDismissListener { vm.updateSearchEngine(true) }
            }
        }
        engineEditDialog?.setDeleteListener {
            explosionField.explode(view)
            if (position < engineList.size) {
                engineList.removeAt(position)
                engineAdapter.notifyItemRemoved(position)
            }
        }
        engineEditDialog?.setEntity(it, view)?.show()
    }

    private var engineEditDialog: EngineEditDialog? = null // 搜索引擎编辑弹窗
    private val historyViewDelegate by lazy { HistoryViewDelegate() } // 历史记录

    private val keywordList = mutableListOf<String>()
    private val keywordAdapter by lazy { MultiTypeAdapter() }
    private val keywordViewDelegate by lazy {
        KeywordViewDelegate {
            binding.searchEt.setText(it)
            binding.searchEt.setSelection(binding.searchEt.text?.length ?: 0)
        }
    }

    private val vm by viewModels<DataViewModel>()
    private val updateVM by viewModels<UpdateViewModel>()
    private val appVM by viewModels<AppViewModel>()
    private var customTabActivityHelper: CustomTabActivityHelper? = null
    private var customTabEnabled = MMKVConstants.useChromeCustomTab
    val explosionField: ExplosionField by lazy { ExplosionField.attach2Window(this) }
    private var keyBoardHeight = 0
    private var imeVisible = MMKVConstants.showKeyboard
    private var hasShowPanel = false

    private var keyboardHeightProvider: KeyboardHeightProvider? = null
    private var virtualNavigationHeight = 0
    private var showPanel = false
    private var windowInsetsEnabled = false
    private var isShowEngine = false // 记录显示的是搜索引擎还是星标项
    private var isShowStar = false // 记录显示的是搜索引擎还是星标项
    private val settingFragmentList = arrayListOf<Fragment>()
    private var directEditDialog: DirectEditDialog? = null

    private val deferringInsetsListener = RootViewDeferringInsetsCallback(
        persistentInsetTypes = WindowInsetsCompat.Type.systemBars(),
        deferredInsetTypes = WindowInsetsCompat.Type.ime(),
    )

    val openDocument =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (engineEditDialog?.isShowing == true) {
                engineEditDialog?.setLocalIcon(uri)
            }

            vm.iconUri.value = uri


            if (directEditDialog?.isShowing == true) {
                directEditDialog?.handleIconUri(uri)
            }
        }

    // DirectEditDialog 图标选择
    val chooseAppIcon =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            activityResult.data?.let {
                val packageName = it.getStringExtra("packageName") ?: ""
                if (directEditDialog?.isShowing == true) {
                    directEditDialog?.setAppIcon(packageName)
                }
            }
        }

    // 图标选择应用
    val chooseAppLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            activityResult.data?.let {
                val packageName = it.getStringExtra("packageName") ?: ""
                vm.selectApp.value = packageName
                if (engineEditDialog?.isShowing == true) {
                    engineEditDialog?.showSelectAppIcon(packageName)
                }
            }
        }

    // 快捷方式选择应用
    val chooseDirectAppLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            activityResult.data?.let {
                val packageName = it.getStringExtra("packageName") ?: ""
                vm.selectDirectApp.value = packageName
            }
        }

    // 搜索引擎指定应用
    val specifyAppLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            activityResult.data?.let {
                val packageName = it.getStringExtra("packageName") ?: ""
                vm.specifyApp.value = packageName
                if (engineEditDialog?.isShowing == true) {
                    engineEditDialog?.setSpecifyApp(packageName)
                }
            }
        }
    private var stubBinding: ViewstubPanelBinding? = null

    override fun initView() {

//        test()
        // 高于 30 使用 WindowInsetsAnimation
        // 低于 30 使用 KeyboardChangeObserver
        if (!isBeforeR()) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars =
                true
            enableWindowInsetsAnimation(true)
        } else {
            keyboardHeightProvider = KeyboardHeightProvider(this)
            keyboardHeightProvider?.setKeyboardHeightObserver(this)
            binding.root.post { keyboardHeightProvider?.start() }
        }

        window.statusBarColor = Color.TRANSPARENT
        binding.searchEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus and !windowInsetsEnabled) {
                enableWindowInsetsAnimation(true)
            }
        }

        initEngineRv()
        initSearch()
        initRecyclerView()
        binding.searchLayout.setBackgroundResource(R.drawable.bg_top_direct)
        binding.viewStub.setOnInflateListener { _, inflated ->
            stubBinding = ViewstubPanelBinding.bind(inflated)
            stubBinding?.root?.handleInset { view, insets ->
                view.setPadding(
                    0,
                    0,
                    0,
                    insets.bottom
                )
            }
            initPanel()
        }

        if (customTabEnabled) {
            customTabActivityHelper = CustomTabActivityHelper()
            customTabActivityHelper?.setConnectionCallback(this)
        }
    }

    override fun initData() {
        if (MMKVConstants.defaultInputTag.isNotEmpty()) {
            binding.searchEt.setText(MMKVConstants.defaultInputTag)
            binding.searchEt.setSelection(MMKVConstants.defaultInputTag.length)
        }

        initLicense {
            vm.checkData()
//            vm.updateDefaultSearchEngine()
            vm.search("")
            checkUpdate()
        }
    }

    private fun initSearch() {
        binding.searchEt.onActionSearch { text ->
            if (MMKVConstants.enterChoice == 0) { // 默认搜索引擎
                vm.defaultSearchEntity.value?.go(this, text)
                saveSearchHistory(text, SearchHistoryEntity.ENGINE)
                if (MMKVConstants.autoClose) {
                    finish()
                }
            } else if (MMKVConstants.enterChoice == 1) { // 打开第一个搜索项
                if (searchResult.isNotEmpty()) {
                    when (val data = searchResult[0]) {
                        is AppEntity -> {
                            DirectApp.App.openApp(data.packageName)
                            saveSearchHistory(text, SearchHistoryEntity.APP)
                        }

                        is ContactEntity -> {
                            callPhone(text)
                            saveSearchHistory(text, SearchHistoryEntity.CONTACT)
                        }

                        is NewDirectEntity -> {
                            if (data.isSearch == 0) {
                                data.go(DirectApp.App)
                                saveSearchHistory(text, SearchHistoryEntity.DIRECT)
                            } else {
                                val lastIndex = binding.searchEt.text.toString()
                                    .lastIndexOf(MMKVConstants.engineTag)
                                val word = binding.searchEt.text.toString().substring(0, lastIndex)
                                data.go(this@DirectActivity, word)
                                saveSearchHistory(word, SearchHistoryEntity.ENGINE)
                            }
                        }
                    }
                    if (MMKVConstants.autoClose) {
                        finish()
                    }
                } else if (MMKVConstants.openEngineIfNoSearchResult) {
                    vm.defaultSearchEntity.value?.go(this, text)
                    saveSearchHistory(text, SearchHistoryEntity.ENGINE)
                    if (MMKVConstants.autoClose) {
                        finish()
                    }
                }
            }
        }

        binding.searchEt.doAfterTextChanged { s ->
            s?.let {
                val word = if (it.isBlank()) it.toString() else it.trim().toString()
                if (MMKVConstants.showSearchKeyword) {
                    vm.searchKeyword(word)
                }
                vm.search(word)
            }

            vm.defaultSearchEntity.value?.let {
                try {
                    if (it.searchUrl.isNullOrBlank()) return@let
                    val url = String.format(
                        Locale.getDefault(),
                        it.searchUrl,
                        s.toString()
                    )
                    customTabActivityHelper?.mayLaunchUrl(
                        Uri.parse(url),
                        null,
                        null
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.searchImage.setOnClickListener {
            if (imeVisible) {
                if (!hasShowPanel) {
                    binding.viewStub.inflate()
                    hasShowPanel = true
                }

                if (isBeforeR()) {
                    showPanel = true
                } else {
                    enableWindowInsetsAnimation(false)
                }
                hideKeyboard()
                imeVisible = false
                when (stubBinding?.panelViewPager?.currentItem ?: 0) {
                    0 -> vm.updateSearchEngine()
                    1 -> vm.search(MMKVConstants.starTag)
                    2 -> vm.updateSearchEngine()
                }
            } else {
                imeVisible = true
                enableWindowInsetsAnimation(true)
                showKeyboard()
            }
        }

        binding.searchImage.setOnLongClickListener {
            if (isShowEngine) {
                vm.search(MMKVConstants.starTag)
            } else if (isShowStar) {
                vm.updateSearchEngine()
            }
            true
        }

        binding.settings.setOnClickListener {
            startActivity<SettingsActivity>()
        }
    }

    private fun initEngineRv() {
        binding.noteLayout.setOnClickListener {
            setEngineEditMode()
        }
        binding.engineRv.run {
            layoutManager = GridLayoutManager(this@DirectActivity, MMKVConstants.engineSpanCount)
            engineAdapter.register(recentViewDelegate)
            engineAdapter.register(engineViewDelegate)
            engineAdapter.register(historyViewDelegate)
            engineAdapter.items = engineList
            adapter = engineAdapter
//            engineTouchHelper.attachToRecyclerView(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRecyclerView() {
        contactViewDelegate.setAction(
            phoneAction = { callPhone(it) },
            smsAction = { DirectApp.App.sendSms(it) })

        // 输入内容后面加上 -，可以带出搜索引擎
        tagEngineViewDelegate.setItemClickListener {
            val lastIndex = binding.searchEt.text.toString().lastIndexOf(MMKVConstants.engineTag)
            val word = binding.searchEt.text.toString().substring(0, lastIndex)
            it.go(this@DirectActivity, word)
            saveSearchHistory(word, SearchHistoryEntity.ENGINE)
            if (MMKVConstants.autoClose) {
                finish()
            }
        }
        binding.searchRecyclerView.run {
            layoutManager =
                LinearLayoutManager(this@DirectActivity, LinearLayoutManager.VERTICAL, true)
        }

        historyViewDelegate.setItemLongClickListener { view, i, searchHistoryEntity ->
            handleHistoryLongClick(view, i, searchHistoryEntity)
        }
        historyViewDelegate.setItemClickListener { _, _, searchHistoryEntity ->
            vm.sendKeyword(searchHistoryEntity.keyWord)
        }

        searchAdapter.run {
            register(appViewDelegate) // 应用
            register(contactViewDelegate) // 联系人
            register(urlViewDelegate) // url
            register(emailViewDelegate) // 邮件
            register(pasteViewDelegate) // 粘贴板
            register(historyViewDelegate)
            register(NewDirectEntity::class) // 搜索引擎
                .to(directViewDelegate, tagEngineViewDelegate)
                .withKotlinClassLinker { _, data ->
                    when (data.isQueryByTag) {
                        false -> DirectViewDelegate::class
                        true -> TagEngineViewDelegate::class
                    }
                }
            items = searchResult
            binding.searchRecyclerView.adapter = this
        }

        // 点击空白区域退出应用
        // target 33时，有空指针问题，谨慎升级
        val gestureDetector =
            GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    handleBack()
                    return true
                }
            })

        binding.searchRecyclerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }

        if (MMKVConstants.showSearchKeyword) {
            binding.keywordRecyclerView.isVisible = true
            // 搜索联想
            binding.keywordRecyclerView.run {
                setMaxHeight(dp2px(36).toInt()) // 1 行，横向滑动
                itemPadding(dp2px(4).toInt(), dp2px(4).toInt(), dp2px(8).toInt(), dp2px(8).toInt())
                layoutManager =
                    FlexboxLayoutManager(this@DirectActivity, FlexDirection.COLUMN, FlexWrap.WRAP)
                keywordAdapter.run {
                    register(keywordViewDelegate)
                    items = keywordList
                }
                adapter = keywordAdapter
            }
        }

    }

    override fun onResume() {
        super.onResume()

        binding.root.post {

//            binding.searchEt.requestFocus()

            if (MMKVConstants.showKeyboard) {
                showKeyboard()
            }

            if (MMKVConstants.showLastSearch && MMKVConstants.lastSearch.isNotBlank()) {
                binding.searchEt.setText(MMKVConstants.lastSearch)
                if (MMKVConstants.selectionLastSearch) {
                    binding.searchEt.setSelection(0, MMKVConstants.lastSearch.length)
                } else {
                    binding.searchEt.setSelection(MMKVConstants.lastSearch.length)
                }
            }

            if (MMKVConstants.showClipboardContent) {
                window.decorView.post {
                    val pasteContent = getClipboardText()
                    if (pasteContent.isNotEmpty() && pasteContent != MMKVConstants.lastClipboardContent) {
                        MMKVConstants.lastClipboardContent = pasteContent
                        vm.handleClipboardContent(pasteContent)
                    }
                }
            }

            TimerCounter.end("Start2")
        }



        intent?.let {
            if (!(it.action != Intent.ACTION_SEND || it.type?.contains("text") != true)) {
                it.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                    binding.searchEt.setText(text)
                    binding.searchEt.setSelection(text.length)
                }
            }

            val packageName = it.getStringExtra("collect_package")
            val scheme = it.getStringExtra("collect_scheme")
            val exported = it.getBooleanExtra("collect_exported", false)
            if (!packageName.isNullOrEmpty() && !scheme.isNullOrEmpty()) {
                if (directEditDialog == null) {
                    directEditDialog = DirectEditDialog(this) {
                        vm.updateDirect()
                    }
                }
                directEditDialog?.setDirect(packageName, scheme, exported)
                directEditDialog?.show()
                hideFloatView(FLOAT_COLLECT_TAG)
            }
        }
//        Looper.myQueue().addIdleHandler {
//            AppWidgetManager.getInstance(DirectApp.App).run {
//                val starAppWidgetId =
//                    getAppWidgetIds(ComponentName(DirectApp.App, StarWidgetProvider::class.java))
//                notifyAppWidgetViewDataChanged(starAppWidgetId, R.id.widgetGridView)
//
//                val recentAppWidgetId =
//                    getAppWidgetIds(ComponentName(DirectApp.App, RecentWidgetProvider::class.java))
//                notifyAppWidgetViewDataChanged(recentAppWidgetId, R.id.widgetGridView)
//            }
//            false
//        }
        hideTask(MMKVConstants.removeRecent)
    }

    override fun observe() {

        vm.run {

            // 每行图标数
            engineSpanCount.observe(this@DirectActivity) {
                if (binding.engineRv.layoutManager is GridLayoutManager) {
                    (binding.engineRv.layoutManager as GridLayoutManager).spanCount = it
                }
            }

            // 默认搜索引擎
            defaultSearchEntity.observe(this@DirectActivity) {
                it.loadIcon(binding.searchImage)
            }

            // 主页背景色
            mainBgColor.observe(this@DirectActivity) {
                binding.translateLayout.setBackgroundColor(it)
            }

            keywordListData.observe(this@DirectActivity) {
                submitKeywordList(it)
            }

            // 历史记录数据
            searchData.observe(this@DirectActivity) {
                binding.searchEt.setText(it)
                binding.searchEt.setSelection(it.length)
            }

            // 切换搜索引擎事件
            engineClickData.observe(this@DirectActivity) {
                if (binding.searchEt.text.isNullOrEmpty()) {
                    // 内容为空时点击，可快速切换默认搜索引擎
                    vm.saveDefaultEngine(it)
                } else {
                    it.go(this@DirectActivity, binding.searchEt.text.toString())
                    saveSearchHistory(binding.searchEt.text.toString(), SearchHistoryEntity.ENGINE)
                    if (MMKVConstants.autoClose) {
                        finish()
                    }
                }
            }

            // 星标数据
            starResult.observe(this@DirectActivity) {
                isShowStar = true
                isShowEngine = false
                submitEngineList(it.data, MMKVConstants.engineSpanCount, it.needDiff)
            }

            // 搜索引擎数据
            searchEngineList.observe(this@DirectActivity) { data ->
                isShowEngine = true
                isShowStar = false
                // 过滤不显示到面板的引擎
                submitEngineList(
                    data.data.filter { it.showPanel == 1 },
                    MMKVConstants.engineSpanCount,
                    data.needDiff
                )
            }

            // 历史记录数据
            historyResult.observe(this@DirectActivity) {
                isShowEngine = false
                isShowStar = false
                submitEngineList(it, 2)
            }

            // 最近使用数据
            recentResult.observe(this@DirectActivity) {
                isShowEngine = false
                isShowStar = false
                submitEngineList(it, MMKVConstants.engineSpanCount)
            }

            searchResultData.observe(this@DirectActivity) {
                binding.searchRecyclerView.run {
                    if (layoutManager is GridLayoutManager)
                        layoutManager =
                            LinearLayoutManager(this@DirectActivity, RecyclerView.VERTICAL, true)
                    if (itemDecorationCount > 0) {
                        removeItemDecorationAt(0)
                    }
                }
                submitList(it)
                searchAdapter.notifyItemRangeChanged(0, searchResult.size, "payload")
            }

            tagEngineList.observe(this@DirectActivity) {
                if (binding.searchRecyclerView.layoutManager is GridLayoutManager) {
                    binding.searchRecyclerView.layoutManager =
                        LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
                }
                submitList(it)
                searchResult.forEachIndexed { index, _ ->
                    searchAdapter.notifyItemChanged(
                        index,
                        "payload"
                    )
                }
            }

        }


        updateVM.updateCheckEntityValue.observe(this) {
            updateVM.handleUpdate(it, this, false)
        }

//        vm.panelBgColor.observe(this) {
//            initTabStyle(it)
//            binding.searchLayout.background = topCornerPanelBgDrawable()
//            stubBinding?.panelLayout?.background = bottomCornerPanelBgDrawable()
//            searchAdapter.notifyItemRangeChanged(0, searchAdapter.itemCount, "payload")
////            searchAdapter.run {
////                items.forEachIndexed { index, _ ->
////                    notifyItemChanged(
////                        index,
////                        "payload"
////                    )
////                }
////            }
//        }


        appVM.run {
            shareApkFileData.observe(this@DirectActivity) {
                shareApkFile(getString(R.string.share_to), it)
            }
            requestPermissionData.observe(this@DirectActivity) {
                PermissionX.init(this@DirectActivity)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(
                            deniedList,
                            getString(R.string.backup_apk_note),
                            getString(R.string.permission_again),
                            getString(R.string.permission_no_need)
                        )
                    }
                    .onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            getString(R.string.never_ask_note),
                            getString(R.string.go_to_setting),
                            getString(R.string.suan_le)
                        )
                    }
                    .request { allGranted, _, _ ->
                        if (allGranted) {
                            appVM.backupApkFile(it.first, it.second)
                        }
                    }
            }
        }
    }

    private fun submitList(it: List<Any>) {
        binding.searchEt.text.toString().run {
            appViewDelegate.setKeyWord(this)
            directViewDelegate.setKeyWord(this)
            contactViewDelegate.setKeyWord(this)
        }
        val callback = MultiDiffCallback(searchResult, it)
        val result = DiffUtil.calculateDiff(callback)
        searchResult.clear()
        searchResult.addAll(it)
        result.dispatchUpdatesTo(searchAdapter)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun submitEngineList(it: List<Any>, spanCount: Int, needDiff: Boolean = false) {
        (binding.engineRv.layoutManager as GridLayoutManager).spanCount = spanCount
        if (needDiff || engineList.isEmpty()) {
            val callback = MultiDiffCallback(engineList, it)
            val result = DiffUtil.calculateDiff(callback)
            engineList.clear()
            engineList.addAll(it)
            result.dispatchUpdatesTo(engineAdapter)
        } else {
            engineList.clear()
            engineList.addAll(it)
            engineAdapter.notifyDataSetChanged()
        }
    }

    private fun submitKeywordList(it: List<String>) {
        val callback = MultiDiffCallback(keywordList, it)
        val result = DiffUtil.calculateDiff(callback)
        keywordList.clear()
        keywordList.addAll(it)
        result.dispatchUpdatesTo(keywordAdapter)
    }

    private fun checkUpdate() {
        updateVM.checkUpdate()
    }

    override fun onStart() {
        super.onStart()
        customTabActivityHelper?.bindCustomTabsService(this)
    }

    override fun onStop() {
        super.onStop()
        customTabActivityHelper?.unbindCustomTabsService(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        customTabActivityHelper?.setConnectionCallback(null)
        keyboardHeightProvider?.setKeyboardHeightObserver(null)
        keyboardHeightProvider?.close()
    }

    private fun enableWindowInsetsAnimation(boolean: Boolean) {
        windowInsetsEnabled = boolean
        if (isBeforeR()) return
        ViewCompat.setWindowInsetsAnimationCallback(
            binding.translateLayout,
            if (boolean) deferringInsetsListener else null
        )
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.translateLayout,
            if (boolean) deferringInsetsListener else null
        )
        ViewCompat.setWindowInsetsAnimationCallback(
            binding.searchRecyclerView,
            if (boolean)
                TranslateDeferringInsetsAnimationCallback(
                    view = binding.searchRecyclerView,
                    persistentInsetTypes = WindowInsetsCompat.Type.systemBars(),
                    deferredInsetTypes = WindowInsetsCompat.Type.ime(),
                    dispatchMode = WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE
                ) else null
        )
        ViewCompat.setWindowInsetsAnimationCallback(
            binding.searchLayout,
            if (boolean)
                InnerTranslateDeferringInsetsAnimationCallback(
                    view = binding.searchLayout,
                    persistentInsetTypes = WindowInsetsCompat.Type.systemBars(),
                    deferredInsetTypes = WindowInsetsCompat.Type.ime(),
                    dispatchMode = WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE,
                    onProgressListener = { height ->
                        keyBoardHeight = height
                        stubBinding?.let {
                            it.panelLayout.updateLayoutParams<FrameLayout.LayoutParams> {
                                this.height = keyBoardHeight
                            }
                        }
                    },
                    onEndListener = {
                        imeVisible = ViewCompat.getRootWindowInsets(binding.searchLayout)
                            ?.isVisible(WindowInsetsCompat.Type.ime()) == true
                        stubBinding?.panelLayout?.visibility =
                            if (imeVisible) View.VISIBLE else View.GONE
                    }
                ) else null
        )

        ViewCompat.setWindowInsetsAnimationCallback(
            binding.searchEt,
            if (boolean) ControlFocusInsetsAnimationCallback(binding.searchEt) else null
        )
    }

    private var isFirst = true
    private fun handleKeyboardChangeBeforeR(height: Int) {
        YLog.e("handleKeyboardChangeBeforeR $height")
        if (isFirst) {
            isFirst = false
            if (MMKVConstants.showKeyboard) showKeyboard()
        }
        if (binding.translateLayout.visibility == View.GONE) {
            binding.translateLayout.visibility = View.VISIBLE
        }
        keyBoardHeight = if (height > 0) height else 0
        imeVisible = keyBoardHeight > 0

        if (showPanel) {
            showPanel = false
            return
        }
        if (keyBoardHeight > 0) {
            binding.translateLayout.translationY = (-keyBoardHeight).toFloat()
        } else {
            binding.translateLayout.translationY = 0f
        }
        stubBinding?.let {
            it.panelLayout.updateLayoutParams<FrameLayout.LayoutParams> {
                this.height = keyBoardHeight
            }
            it.panelLayout.visibility = if (keyBoardHeight > 0) View.VISIBLE else View.GONE
        }
    }

    private fun initPanel() {
        initTabStyle(MMKVConstants.panelBgColor)
//        stubBinding?.panelLayout?.setBackgroundResource(R.drawable.bg_top_direct)
        settingFragmentList.add(PanelSettingFragment()) // 面板设置
        settingFragmentList.add(SearchEngineEditFragment()) // 搜索引擎
        settingFragmentList.add(StarEditFragment.newInstance()) // 星标
        // gp 版本暂时不放开快捷方式
        if (applicationContext.getChannel() == "official") {
            settingFragmentList.add(DirectEditFragment()) // 快捷方式
        }

        stubBinding?.run {
            panelViewPager.offscreenPageLimit = 1
            panelViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> {}
                        1 -> vm.updateSearchEngine()
                        2 -> vm.search(MMKVConstants.starTag)
                        3 -> updateDirect()
                    }
                }
            })
            panelViewPager.adapter =
                CommonFragmentStateAdapter(settingFragmentList, this@DirectActivity)
            panelTabLayout.setupViewPager(panelViewPager)

            panelLayout.updateLayoutParams<FrameLayout.LayoutParams> {
                this.height = keyBoardHeight
            }
            panelLayout.setBackgroundResource(R.drawable.bg_bottom_direct)
            panelLayout.visibility = if (keyBoardHeight > 0) View.VISIBLE else View.GONE

        }
    }

    fun updateDirect() {
        vm.updateDirect()
    }

    private fun initTabStyle(color: Int) {
        stubBinding?.panelTabLayout?.tabs?.forEachIndexed { index, item ->
            item.setItemConfig(
                AnimatedTabItemConfig(
                    getPanelDrawable(index),
                    ContextCompat.getColor(applicationContext, R.color.colorPrimary),
                    color,
                    dp2px(24),
                    dp2px(10).toInt()
                )
            )
        }
    }

    override fun onVirtualBottomHeight(i: Int) {
        virtualNavigationHeight = i
        YLog.e("onVirtualBottomHeight $i")
    }

    private var lastHeight = -1
    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        if (lastHeight != height + virtualNavigationHeight) {
            lastHeight = height + virtualNavigationHeight
            handleKeyboardChangeBeforeR(height + virtualNavigationHeight)
        }
    }

    private fun hideKeyboard() {
        WindowCompat.getInsetsController(window, binding.root).hide(WindowInsetsCompat.Type.ime())
    }

    private fun showKeyboard() {
        YLog.e("showKeyboard")
        if (isBeforeR()) {
            binding.searchEt.postDelayed({ showKeyboard(binding.searchEt) }, 100)
        } else {
            WindowCompat.getInsetsController(window, binding.root)
                .show(WindowInsetsCompat.Type.ime())
        }
    }

    fun showDirectEditDialog() {
        if (directEditDialog == null) {
            directEditDialog = DirectEditDialog(this) {
                vm.updateDirect()
            }
        }
        if (!directEditDialog!!.isShowing) {
            directEditDialog!!.show()
        }
    }

    private fun handleHistoryLongClick(view: View, position: Int, entity: Any) {
        // App，快捷方式，联系人长按不删除
        if (entity is AppEntity || entity is ContactEntity || entity is NewDirectEntity) return
        explosionField.explode(view)
        engineList.removeAt(position)
        engineAdapter.notifyItemRemoved(position)
        vm.handleRemove(entity, 0)
    }

    private val engineTouchHelper = ItemTouchHelper(KtxItemTouchHelperCallback(
        dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP,
        adapter = engineAdapter,
        dataList = engineList,
        onClearView = {
            if (isShowEngine) {
                vm.updateEngineOrder(engineList.filterIsInstance<NewDirectEntity>())
            } else if (isShowStar) {
                vm.updateStarOrder(engineList.filterIsInstance<RecentEntity>())
            }
        }
    ))

    private fun setEngineEditMode() {
        isEngineEditMode = !isEngineEditMode
        binding.noteLayout.isVisible = isEngineEditMode
        recentViewDelegate.isEditMode = isEngineEditMode
        engineViewDelegate.isEditMode = isEngineEditMode
        engineTouchHelper.attachToRecyclerView(if (isEngineEditMode) binding.engineRv else null)
    }
}


