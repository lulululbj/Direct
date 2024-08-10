package luyao.direct.ui.settings

import com.hi.dhl.binding.viewbind
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityLaunchModeSettingsBinding
import luyao.direct.databinding.ActivitySidebarSettingsBinding

/**
 * ================================================
 * Copyright (c) 2020 All rights reserved
 * Description：悬浮窗相关设置
 * Author: luyao
 * Date： Date: 2022/12/03
 * ================================================
 */
class SidebarSettingsActivity : DirectBaseActivity() {

    private val binding: ActivitySidebarSettingsBinding by viewbind()

    override fun initView() {
        configToolbar(binding.titleLayout.toolBar, getString(luyao.direct.R.string.sidebar_setting))
        configRootInset(binding.root)
    }

    override fun initData() {
    }
}