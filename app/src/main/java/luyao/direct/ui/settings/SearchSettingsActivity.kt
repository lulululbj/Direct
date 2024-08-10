package luyao.direct.ui.settings

import com.hi.dhl.binding.viewbind
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivitySearchSettingsBinding

/**
 * ================================================
 * Copyright (c) 2020 All rights reserved
 * Description：搜索相关设置
 * Author: luyao
 * Date： Date: 2021/11/26
 * ================================================
 */
class SearchSettingsActivity : DirectBaseActivity() {

    private val binding: ActivitySearchSettingsBinding by viewbind()

    override fun initView() {
        configToolbar(binding.titleLayout.toolBar, getString(luyao.direct.R.string.Search))
        configRootInset(binding.root)
    }

    override fun initData() {
    }
}