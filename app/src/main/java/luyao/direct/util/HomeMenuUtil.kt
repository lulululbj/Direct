package luyao.direct.util

import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.HomeMenu
import luyao.direct.model.entity.HomeMenuId
import luyao.direct.model.entity.HomeMenuList


fun getMenuOrderList(): List<HomeMenu> {
    return if (MMKVConstants.homeMenuOrderJson.isEmpty()) {
        HomeMenuList
    } else {
        MoshiUtil.homeMenuListAdapter.fromJson(MMKVConstants.homeMenuOrderJson)?.filter { it.id != HomeMenuId.RECENT && it.id != HomeMenuId.HISTORY } ?: arrayListOf()
    }
}