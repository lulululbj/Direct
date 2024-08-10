package luyao.direct.ui.settings

import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityBackupBinding

/**
 * 备份和恢复
 * author: luyao
 * date:   2021/10/14 10:04
 */
@AndroidEntryPoint
class BackupActivity : DirectBaseActivity() {

    private val binding: ActivityBackupBinding by viewbind()

    override fun initView() {
        configToolbar(binding.titleLayout.toolBar, getString(luyao.direct.R.string.backup_title))
        configRootInset(binding.root)
    }

    override fun initData() {
    }
}