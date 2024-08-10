package luyao.direct.util

import android.content.Context
import luyao.direct.model.MMKVConstants

/**
 * ================================================
 * Copyright (c) 2020 All rights reserved
 * Description：一些初始化工作
 * Author: luyao
 * Date： Date: 2021/12/6
 * ================================================
 */
object DirectInit {

    val iconPackManager by lazy { IconPackManager() }
    var iconPack: IconPackManager.IconPack? = null

    fun init(context: Context) {
        initIconPackManager(context)
    }

    private fun initIconPackManager(context: Context) {
        iconPackManager.setContext(context)
        val map = iconPackManager.getAvailableIconPacks(true)
        for ((key, value) in map) {
            if (key == MMKVConstants.iconPack)
                iconPack = value
        }
    }
}