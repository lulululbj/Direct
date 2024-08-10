package luyao.direct.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat
import luyao.direct.R
import luyao.direct.ext.*
import luyao.direct.model.MMKVConstants
import luyao.direct.util.*
import luyao.ktx.ext.openAssist
import luyao.ktx.ext.startActivity
import luyao.ktx.ext.toast

/**
 * ================================================
 * Copyright (c) 2020 All rights reserved
 * Description：搜索相关设置
 * Author: luyao
 * Date： Date: 2021/11/26
 * ================================================
 */
class LaunchModeSettingFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_launch_mode_preferences, rootKey)
        setListener()
    }

    @SuppressLint("CheckResult")
    private fun setListener() {

        // 数字助理
        findPreference<Preference>("setAssist")?.setOnPreferenceClickListener {
            requireContext().openAssist {
                toast(R.string.system_not_support)
            }
            true
        }

        // 悬浮窗设置
        findPreference<Preference>("sideBarSetting")?.setOnPreferenceClickListener {
            startActivity<SidebarSettingsActivity>()
            true
        }

        // 通知栏
        findPreference<SwitchPreference>("needNotification")?.apply {
            isChecked = MMKVConstants.showNotification
        }?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                XXPermissions.with(this)
                    .permission(Permission.POST_NOTIFICATIONS)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                            if (all) {
                                MMKVConstants.showNotification = newValue
                                requireContext().createNotification()
                            }
                        }

                        override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                            super.onDenied(permissions, never)
                            MMKVConstants.showNotification = false
                        }
                    })
            } else {
                MMKVConstants.showNotification = newValue
                requireContext().cancelNotification(NOTIFICATION_ID)
            }
            true
        }

        // 星标通知
        findPreference<SwitchPreference>("needNotificationStar")?.apply {
            isChecked = MMKVConstants.showStarNotification
        }?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                requireContext().createDirectNotification(0)
            } else {
                requireContext().cancelNotification(STAR_NOTIFICATION_ID)
            }
            MMKVConstants.showStarNotification = newValue
            true
        }

        // 最近使用通知
        findPreference<SwitchPreference>("needNotificationRecent")?.apply {
            isChecked = MMKVConstants.showRecentNotification
        }?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                requireContext().createDirectNotification(1)
            } else {
                requireContext().cancelNotification(RECENT_NOTIFICATION_ID)
            }
            MMKVConstants.showRecentNotification = newValue
            true
        }
    }

//    private fun checkOverlayPermission() {
//        if (!Settings.canDrawOverlays(requireContext())) {
//            overlaySetting.launch(
//                Intent(
//                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:${requireActivity().packageName}")
//                )
//            )
//        } else {
//            showFloatView()
//        }
//    }


}