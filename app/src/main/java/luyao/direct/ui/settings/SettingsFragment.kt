package luyao.direct.ui.settings

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.ext.getChannel
import luyao.direct.model.DirectConstants
import luyao.direct.vm.UpdateViewModel
import luyao.ktx.ext.openBrowser
import luyao.ktx.ext.versionCode
import luyao.ktx.ext.versionName
import luyao.ktx.web.WebLauncher

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val updateVM by viewModels<UpdateViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        setListener()
    }

    @SuppressLint("CheckResult")
    private fun setListener() {

        updateVM.updateCheckEntityValue.observe(this) {
            updateVM.handleUpdate(it, requireActivity(), true)
        }

        // 搜索
//        findPreference<Preference>("search")?.setOnPreferenceClickListener {
//            startActivity(Intent(requireActivity(), SearchSettingsActivity::class.java))
//            true
//        }

        // 通知
//        findPreference<Preference>("notification")?.setOnPreferenceClickListener {
//            startActivity(Intent(requireActivity(), NotificationSettingsActivity::class.java))
//            true
//        }


        // 备份和恢复
//        findPreference<Preference>("backup")?.setOnPreferenceClickListener {
//            requireActivity().startActivity(Intent(requireActivity(), BackupActivity::class.java))
//            true
//        }

        // 意见反馈
        findPreference<Preference>("feedback")?.setOnPreferenceClickListener {
            val postData =
                "clientInfo=Direct&clientVersion=${requireActivity().versionName}(${requireActivity().versionCode})" +
                        "&os=Android(${Build.VERSION.SDK_INT})&osVersion=${Build.BRAND}_${Build.MODEL}"
            WebLauncher().start(requireActivity()) {
                title(R.string.feedback)
                url(DirectConstants.FEEDBACK_URL)
                titleTextColor(R.color.title_text_color)
                postData(postData)
            }
            true
        }

        // 隐私政策
        findPreference<Preference>("secretLicense")?.setOnPreferenceClickListener {
            WebLauncher().start(requireActivity()) {
                title(R.string.secret_license)
                url(DirectConstants.SECRET_LICENSE_URL)
                titleTextColor(R.color.title_text_color)
            }
            true
        }

        // 用户协议
        findPreference<Preference>("userLicense")?.setOnPreferenceClickListener {
            WebLauncher().start(requireActivity()) {
                title(R.string.user_license)
                url(DirectConstants.USER_LICENSE_URL)
                titleTextColor(R.color.title_text_color)
            }
            true
        }

        // 检测升级
        findPreference<Preference>("update")?.setOnPreferenceClickListener {
            requireContext().openBrowser("https://www.coolapk.com/apk/luyao.direct")
//            updateVM.checkUpdate(true, jumpStore = true)
            true
        }

    }
}