package luyao.direct.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.ext.getBrowserList
import luyao.direct.model.AppDatabase
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.associationEngineList
import luyao.direct.view.SpecialTagDialog
import luyao.direct.worker.DirectWorker
import luyao.ktx.ext.isGranted
import luyao.ktx.ext.showConfirmDialog

/**
 * ================================================
 * Copyright (c) 2020 All rights reserved
 * Description：搜索相关设置
 * Author: luyao
 * Date： Date: 2021/11/26
 * ================================================
 */
class SearchSettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.search_settings_preferences, rootKey)
        setListener()
    }

    @SuppressLint("CheckResult")
    private fun setListener() {

        // 默认浏览器
        findPreference<Preference>("defaultBrowser")?.apply {
            summary = MMKVConstants.defaultBrowserName
        }?.setOnPreferenceClickListener {
            MaterialDialog(requireActivity()).show {
                cornerRadius(16f)
                val browserList = requireContext().getBrowserList()
                var selectedIndex = 0
                browserList.forEachIndexed { index, resolveInfo ->
                    if (resolveInfo.activityInfo.packageName == MMKVConstants.defaultBrowser) {
                        selectedIndex = index
                        return@forEachIndexed
                    }
                }
                listItemsSingleChoice(
                    initialSelection = selectedIndex,
                    items = browserList.map { it.loadLabel(DirectApp.App.packageManager) }) { _, index, text ->
                    MMKVConstants.defaultBrowser = browserList[index].activityInfo.packageName
                    MMKVConstants.defaultBrowserName = text.toString()
                    it.summary = text
                }
            }
            true
        }

        // 系统应用
        val systemAppSwitch = findPreference<SwitchPreference>("systemApp")
        systemAppSwitch?.apply {
            isChecked = MMKVConstants.showSystemApp
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.showSystemApp = newValue as Boolean
            true
        }

        // 应用
        val appSwitch = findPreference<SwitchPreference>("app")
        appSwitch?.apply {
            isChecked = MMKVConstants.showApp
        }?.setOnPreferenceChangeListener { _, newValue ->
            systemAppSwitch?.isVisible = newValue as Boolean
            MMKVConstants.showApp = newValue
            true
        }

        // 是否搜索快捷方式
        findPreference<SwitchPreference>("shortcut")?.apply {
            isChecked = MMKVConstants.showAppDirect
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.showAppDirect = newValue as Boolean
            true
        }

        // 联系人
        findPreference<SwitchPreference>("searchContacts")?.apply {
            isChecked = if (requireContext().isGranted(Manifest.permission.READ_CONTACTS)) {
                MMKVConstants.searchContacts
            } else {
                false
            }
        }?.setOnPreferenceChangeListener { preference, newValue ->
            PermissionX.init(this)
                .permissions(Manifest.permission.READ_CONTACTS)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        getString(R.string.read_contacts_note),
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
                        MMKVConstants.searchContacts = newValue as Boolean
                        val directWorkRequest =
                            OneTimeWorkRequestBuilder<DirectWorker>().build()
                        WorkManager.getInstance(DirectApp.App).enqueue(directWorkRequest)
                    } else {
                        (preference as SwitchPreference).isChecked = !(newValue as Boolean)
                    }
                }
            true
        }

        // 特殊搜索项
        findPreference<Preference>("specialTag")?.setOnPreferenceClickListener {
            SpecialTagDialog(requireActivity()).show()
            true
        }

        // 默认选中上次内容
        val selectLastSearch = findPreference<SwitchPreference>("selectLastSearch")?.apply {
            isChecked = MMKVConstants.selectionLastSearch
        }
        selectLastSearch?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.selectionLastSearch = newValue as Boolean
            true
        }

        // 保留搜索内容
        findPreference<SwitchPreference>("showLastSearch")?.apply {
            isChecked = MMKVConstants.showLastSearch
            selectLastSearch?.isVisible = isChecked
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.showLastSearch = newValue as Boolean
            selectLastSearch?.isVisible = newValue
            true
        }


        val moreAppInfoPreference = findPreference<SwitchPreference>("moreAppInfo")

        // 按包名查找
        findPreference<SwitchPreference>("searchByPackageName")?.apply {
            isChecked = MMKVConstants.searchByPackageName
        }?.setOnPreferenceChangeListener { _, newValue ->
            val result = newValue as Boolean
            MMKVConstants.searchByPackageName = result
            if (result) {
                MMKVConstants.moreAppInfo = true
                moreAppInfoPreference?.isChecked = true
            }
            true
        }

        // 应用基本信息
        moreAppInfoPreference?.apply {
            isChecked = MMKVConstants.moreAppInfo
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.moreAppInfo = newValue as Boolean
            true
        }

        val openIfNoSearchResult =
            findPreference<SwitchPreference>("enterIfNoSearchResult")?.apply {
                isChecked = MMKVConstants.openEngineIfNoSearchResult
                isVisible = MMKVConstants.enterChoice == 1
            }
        openIfNoSearchResult?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.openEngineIfNoSearchResult = newValue as Boolean
            true
        }

        // 输入法回车键选项
        findPreference<Preference>("enterChoice")?.apply {
            summary =
                when (MMKVConstants.enterChoice) {
                    0 -> getString(R.string.open_default_search_engine)
                    1 -> getString(
                        R.string.open_search_first_result
                    )

                    else -> getString(R.string.do_nothing)
                }
        }?.setOnPreferenceClickListener {
            val enterMenu = PopupMenu(requireContext(), listView[it.order + 1], Gravity.BOTTOM)
            enterMenu.run {
                menuInflater.inflate(R.menu.menu_enter_choice, menu)
                setOnMenuItemClickListener { item ->
                    it.summary = item.title
                    MMKVConstants.enterChoice = when (item.itemId) {
                        R.id.enter_0 -> 0
                        R.id.enter_1 -> 1
                        else -> 2
                    }
                    openIfNoSearchResult?.isVisible = MMKVConstants.enterChoice == 1
                    true
                }
                show()
            }
            true
        }

        // 按拼音查找
        findPreference<SwitchPreference>("searchByPin")?.apply {
            isChecked = MMKVConstants.searchByPin
        }?.setOnPreferenceChangeListener { _, newValue ->
            val result = newValue as Boolean
            MMKVConstants.searchByPin = result
            true
        }

        // 自动显示搜索引擎
        findPreference<SwitchPreference>("showSearchEngine")?.apply {
            isChecked = MMKVConstants.showSearchEngine
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.showSearchEngine = newValue as Boolean
            true
        }

        // 历史记录开关
        findPreference<SwitchPreference>("historySwitch")?.apply {
            isChecked = MMKVConstants.saveHistory
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.saveHistory = newValue as Boolean
            true
        }

        // 清空历史记录
        findPreference<Preference>("clearHistory")?.setOnPreferenceClickListener {
            showConfirmDialog(
                title = getString(R.string.Confirm),
                message = getString(R.string.clear_history_note)
            ) {
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getInstance(DirectApp.App).searchHistoryDao().deleteAll()
                }
            }
            true
        }

        // 清空最近使用记录
        findPreference<Preference>("clearRecentUse")?.setOnPreferenceClickListener {
            showConfirmDialog(
                title = getString(R.string.Confirm),
                message = getString(R.string.clear_recent_note)
            ) {
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getInstance(DirectApp.App).appDao().clearRecentUse()
                    AppDatabase.getInstance(DirectApp.App).directDao().clearRecentUse()
                }
            }
            true
        }

        // 展示剪切板内容
        findPreference<SwitchPreference>("showClipboardContent")?.apply {
            isChecked = MMKVConstants.showClipboardContent
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.showClipboardContent = newValue as Boolean
            true
        }

        // 自动复制搜索内容
        findPreference<SwitchPreference>("autoCopyWhenSearch")?.apply {
            isChecked = MMKVConstants.autoCopyWhenSearch
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.autoCopyWhenSearch = newValue as Boolean
            true
        }
        // 联想源
        val associationEnginePreference = findPreference<Preference>("chooseAssociationEngine")
        associationEnginePreference?.apply {
            isVisible = MMKVConstants.showSearchKeyword
            associationEngineList.forEachIndexed { index, engine ->
                if (engine.id == MMKVConstants.associationEngineId) {
                    summary = engine.name
                    return@forEachIndexed
                }
            }
        }?.setOnPreferenceClickListener {
            MaterialDialog(requireActivity()).show {
                cornerRadius(16f)
                var selectedIndex = 0
                associationEngineList.forEachIndexed { index, engine ->
                    if (engine.id == MMKVConstants.associationEngineId) {
                        selectedIndex = index
                        return@forEachIndexed
                    }
                }
                listItemsSingleChoice(
                    initialSelection = selectedIndex,
                    items = associationEngineList.map { it.name }) { _, index, text ->
                    MMKVConstants.associationEngineId = associationEngineList[index].id
                    it.summary = text
                }
            }
            true
        }

        // 仅联想
        val onlyAssociationSwitch = findPreference<SwitchPreference>("onlyAssociationSwitch")
        onlyAssociationSwitch?.apply {
            isVisible = MMKVConstants.showSearchKeyword
            isChecked = MMKVConstants.onlyAssociation
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.onlyAssociation = newValue as Boolean
            true
        }

        // 输入联想开关
        findPreference<SwitchPreference>("inputAssociationSwitch")?.apply {
            isChecked = MMKVConstants.showSearchKeyword
        }?.setOnPreferenceChangeListener { _, newValue ->
            associationEnginePreference?.isVisible = newValue as Boolean
            onlyAssociationSwitch?.isVisible = newValue
            MMKVConstants.showSearchKeyword = newValue
            true
        }

    }
}