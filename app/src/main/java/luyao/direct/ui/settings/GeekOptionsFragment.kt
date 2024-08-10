package luyao.direct.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import luyao.direct.R
import luyao.direct.adapter.AppAdapter
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.HomeMenuId
import luyao.direct.model.entity.SimpleAppEntity
import luyao.direct.util.DirectInit
import luyao.direct.util.IconPackManager
import luyao.ktx.ext.hideTask

/**
 * author: luyao
 * date:   2021/10/14 10:56
 */
class GeekOptionsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.geek_option_preferences, rootKey)
        setListener()
    }

    @SuppressLint("CheckResult")
    private fun setListener() {
        // 全局图标包
        val availableIconPackMap = DirectInit.iconPackManager.getAvailableIconPacks(true)
        val availableIconPackList = arrayListOf<SimpleAppEntity>()
        var iconPack: IconPackManager.IconPack? = null
        for ((_, value) in availableIconPackMap) {
            availableIconPackList.add(SimpleAppEntity(value.packageName, value.name))
            if (value.packageName == MMKVConstants.iconPack) {
                iconPack = value
            }
        }
        availableIconPackList.add(SimpleAppEntity("", getString(R.string.not_use_icon_bag)))
        findPreference<Preference>("iconPack")?.apply {
            iconPack?.let {
                summary = it.name
            }
        }?.setOnPreferenceClickListener {
            DirectInit.iconPackManager.getAvailableIconPacks(true)
            MaterialDialog(requireActivity()).show {
                cornerRadius(16f)

                customListAdapter(AppAdapter(availableIconPackList))
            }
            true
        }

        // 首页默认显示内容
        findPreference<Preference>("defaultShow")?.apply {
            if (MMKVConstants.defaultShow > 1) MMKVConstants.defaultShow = HomeMenuId.STAR
            summary = if (MMKVConstants.defaultShow == HomeMenuId.STAR) getString(R.string.star_use) else getString(R.string.search_engine)
        }?.setOnPreferenceClickListener {
            MaterialDialog(requireActivity()).show {
                cornerRadius(16f)
                listItemsSingleChoice(
                    items = listOf(getString(R.string.star_use), getString(R.string.search_engine)),
                    initialSelection = MMKVConstants.defaultShow
                ) { _, index, _ ->
                    MMKVConstants.defaultShow = index
                    it.summary =
                        if (index == HomeMenuId.STAR) getString(R.string.star_use) else getString(R.string.search_engine)
                }
            }
//            MenuOrderDialog(requireContext()).show()
            true
        }

        // 是否默认弹出软键盘
        findPreference<SwitchPreference>("showKeyboard")?.apply {
            isChecked = MMKVConstants.showKeyboard
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.showKeyboard = newValue as Boolean
            true
        }

        // chrome custom tab
        findPreference<SwitchPreference>("chromeTab")?.apply {
            isChecked = MMKVConstants.useChromeCustomTab
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.useChromeCustomTab = newValue as Boolean
            true
        }

        // 无痕使用
        findPreference<SwitchPreference>("removeTask")?.apply {
            isChecked = MMKVConstants.removeRecent
        }?.setOnPreferenceChangeListener { _, newValue ->
            requireActivity().hideTask(newValue as Boolean)
            MMKVConstants.removeRecent = newValue
            true
        }

        // 自动关闭直达
        findPreference<SwitchPreference>("autoClose")?.apply {
            isChecked = MMKVConstants.autoClose
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.autoClose = newValue as Boolean
            true
        }
    }

}