package luyao.direct.ui.settings

import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.R
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.SettingsActivityBinding
import luyao.direct.model.MMKVConstants
import luyao.direct.ui.NewAboutActivity
import luyao.direct.vm.UpdateViewModel
import luyao.ktx.ext.handleInset
import luyao.ktx.ext.openBrowser
import luyao.ktx.ext.startActivity

@AndroidEntryPoint
class SettingsActivity : DirectBaseActivity() {

    private val binding: SettingsActivityBinding by viewbind()
    private val updateVM by viewModels<UpdateViewModel>()

    override fun initView() {
        configRootInset(binding.root)
        configToolbar(binding.titleLayout.toolBar, getString(luyao.direct.R.string.setting))
        initListener()
    }

    override fun initData() {

    }

    private fun initListener() {
        binding.run {
            launchMode.settingClick = {
                startActivity<LaunchModeSettingsActivity>()
            }

            searchSetting.settingClick = {
                startActivity<SearchSettingsActivity>()
            }

            geekSetting.settingClick = {
                startActivity<GeekOptionActivity>()
            }

            backup.settingClick = {
                startActivity<BackupActivity>()
            }

            checkUpdate.settingClick = {
                openBrowser("https://www.coolapk.com/apk/luyao.direct")
            }

            about.settingClick = {
                startActivity<NewAboutActivity>()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    @SuppressLint("CheckResult")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_night_mode) {
            MaterialDialog(this).show {
                listItemsSingleChoice(
                    R.array.night_mode,
                    initialSelection = getNightModeSelection()
                ) { _, index, _ ->
                    saveNightMode(index)
                }
            }
        }
        return true
    }

    private fun getNightModeSelection() = when (MMKVConstants.nightMode) {
        AppCompatDelegate.MODE_NIGHT_NO -> 0
        AppCompatDelegate.MODE_NIGHT_YES -> 1
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> 2
        else -> 0
    }

    private fun saveNightMode(index: Int) {
        when (index) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                MMKVConstants.nightMode = AppCompatDelegate.MODE_NIGHT_NO
            }

            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                MMKVConstants.nightMode = AppCompatDelegate.MODE_NIGHT_YES
            }

            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                MMKVConstants.nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
    }

}