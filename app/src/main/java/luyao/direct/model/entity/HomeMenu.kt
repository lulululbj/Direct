package luyao.direct.model.entity

import androidx.fragment.app.Fragment
import com.squareup.moshi.JsonClass
import luyao.direct.ui.menu.HistoryMenuFragment
import luyao.direct.ui.menu.RecentMenuFragment
import luyao.direct.ui.menu.SearchEngineMenuFragment
import luyao.direct.ui.menu.StarMenuFragment

@JsonClass(generateAdapter = true)
data class HomeMenu(val id: Int, val name: String) {
    fun getFragment(): Fragment {
        return when (id) {
            HomeMenuId.SEARCH_ENGINE -> SearchEngineMenuFragment()
            HomeMenuId.STAR -> StarMenuFragment()
            HomeMenuId.RECENT -> RecentMenuFragment()
            HomeMenuId.HISTORY -> HistoryMenuFragment()
            else -> SearchEngineMenuFragment()
        }
    }
}

object HomeMenuId {
    const val SEARCH_ENGINE = 0
    const val STAR = 1
    const val RECENT = 2
    const val HISTORY = 3
}

val HomeMenuList = arrayListOf(
    HomeMenu(HomeMenuId.STAR, "星标"),
    HomeMenu(HomeMenuId.SEARCH_ENGINE, "搜索引擎"),
//    HomeMenu(HomeMenuId.RECENT, "最近使用"),
//    HomeMenu(HomeMenuId.HISTORY, "历史记录")
)