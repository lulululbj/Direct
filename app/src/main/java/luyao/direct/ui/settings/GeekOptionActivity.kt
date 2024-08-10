package luyao.direct.ui.settings

import com.hi.dhl.binding.viewbind
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityGeekOptionBinding

/**
 * 备份和恢复
 * author: luyao
 * date:   2021/10/14 10:04
 */
class GeekOptionActivity : DirectBaseActivity() {

    private val binding: ActivityGeekOptionBinding by viewbind()

    override fun initView() {
        configToolbar(binding.titleLayout.toolBar, getString(luyao.direct.R.string.geek_setting))
        configRootInset(binding.root)
    }

    override fun initData() {
    }
}