package luyao.direct.model

import android.Manifest
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.ktx.ext.isGranted
import luyao.ktx.util.MMKVDelegate

/**
 *  @author: luyao
 * @date: 2021/7/11 下午4:56
 */
object DirectConstants {
    const val FEEDBACK_URL = "https://support.qq.com/product/440659?d-wx-push=1"
    const val SECRET_LICENSE_URL = "https://www.yuque.com/bingxinshuotm/fkhvug/gyo3m0"
    const val USER_LICENSE_URL = "https://www.yuque.com/bingxinshuotm/fkhvug/qq1vzv"
    const val JIANGUO_WEBDAV_URL = "https://dav.jianguoyun.com/dav/"
    const val WEBDAV_HELP_URL = "https://content.jianguoyun.com/3991.html"
}

object ExecMode {
    const val SCHEME = 0
    const val ROOT = 1
}

object MMKVConstants {

    var searchId by MMKVDelegate("search_position", 11) // 选择的搜索引擎
    var newSearchId by MMKVDelegate("search_position", "") // 选择的搜索引擎
    var needGuide by MMKVDelegate("need_guide", true) // 是否开启导航页
    var showSystemApp by MMKVDelegate("show_system_app", true) // 是否显示系统应用
    var showApp by MMKVDelegate("show_app", true) // 是否显示应用
    var searchByPackageName by MMKVDelegate("search_by_package_name", false) // 根据包名查找应用
    var moreAppInfo by MMKVDelegate("more_app_info", false) // 列表中显示应用基本信息
    var searchContacts by MMKVDelegate(
        "search_contacts",
        DirectApp.App.isGranted(Manifest.permission.READ_CONTACTS)
    ) // 是否搜索联系人
    var isFirstLoad by MMKVDelegate("is_first_load", true)
    var showNotification by MMKVDelegate("show_notification", false) // 是否显示通知
    var showStarNotification by MMKVDelegate("show_star_notification", false) // 是否显示星标通知
    var showRecentNotification by MMKVDelegate("show_recent_notification", false) // 是否显示最近使用通知
    var directVersion by MMKVDelegate("direct_version", -1) // gitee 快捷方式数据版本
    var engineVersion by MMKVDelegate("engine_version", -1) // gitee 搜索引擎数据版本
    var enterChoice by MMKVDelegate("enter_choice", 0) // 输入法回车选项，0 调用搜索引擎 1 调用app或快捷方式
    var showLastSearch by MMKVDelegate("show_last_search", false) // 搜索后是否清空搜索内容
    var lastSearch by MMKVDelegate("last_search", "") // 上次搜索内容
    var selectionLastSearch by MMKVDelegate("selection_last_search",  true) // 是否默认选中上次搜索内容

    var recentJson by MMKVDelegate("recent_json", "")
    var starJson by MMKVDelegate("star_json", "")

    var webDavUserName by MMKVDelegate("webdav_username", "")
    var webDavPassword by MMKVDelegate("webdav_password", "")
    var webDavServer by MMKVDelegate("webdav_server", DirectConstants.JIANGUO_WEBDAV_URL)
    var lastBackupCloud by MMKVDelegate("last_backup_cloud", 0L)
    var lastBackupLocal by MMKVDelegate("last_backup_local", 0L)
    var useChromeCustomTab by MMKVDelegate("use_chrome_custom_tab", false)
    var defaultBrowser by MMKVDelegate("default_browser", "") // 默认浏览器包名
    var defaultBrowserName by MMKVDelegate("default_browser_name", "") // 默认浏览器名称
    var engineSpanCount by MMKVDelegate("engine_span_count", 6)
    var mainBgColor by MMKVDelegate(
        "main_bg_color",
        ContextCompat.getColor(DirectApp.App, R.color.main_translucent)
    )
    var panelBgColor by MMKVDelegate(
        "panel_bg_color",
        ContextCompat.getColor(DirectApp.App, R.color.white)
    )

    var starTag by MMKVDelegate("star_tag", " ")
    var recentTag by MMKVDelegate("recent_tag", "  ")
    var historyTag by MMKVDelegate("history_tag", "   ")
    var defaultInputTag by MMKVDelegate("default_search_tag", "")
    var engineTag by MMKVDelegate("engine_tag", "-") // 带出搜索引擎

    var removeRecent by MMKVDelegate("remove_recent", false) // 是否在最近任务列表显示
    var searchByPin by MMKVDelegate("search_by_pin", true) // 是否根据拼音搜索
    var iconPack by MMKVDelegate("icon_pack", "") // 全局图标包
    var defaultShow by MMKVDelegate("default_show", 1) // 首页默认显示项
    var showSideBar by MMKVDelegate("show_side_bar", false) // 悬浮窗侧边栏
    var defaultSideBarLocation by MMKVDelegate("default_side_bar_location", 0) // 0 左边 1 右边
    var defaultSideBarShape by MMKVDelegate("default_side_bar_shape", 1) // 0 条形 1 圆形
    var sideBarColor by MMKVDelegate(
        "side_bar_color",
        ContextCompat.getColor(DirectApp.App, R.color.bar_translucent)
    )
    var sideBarSize by MMKVDelegate("sideBarSize", 50)
    var sideBarY by MMKVDelegate("side_bar_y", 300)
    var sideBarX by MMKVDelegate("side_bar_x", 0)
    var showAppDirect by MMKVDelegate("show_app_direct", true) // 是否搜索应用快捷方式
    var showLicenseDialog by MMKVDelegate("show_license_dialog", true)
    var showSearchEngine by MMKVDelegate("show_search_engine", true)
    var showKeyboard by MMKVDelegate("show_keyboard", true)
    var autoClose by MMKVDelegate("auto_close", true) // 自动关闭直达
    var saveHistory by MMKVDelegate("save_history", true) // 记录搜索历史记录
    var showClipboardContent by MMKVDelegate("show_clipboard_content", false) // 展示剪切板内容
    var lastClipboardContent by MMKVDelegate("last_clipboard_content", "") // 上次处理的剪切板的内容
    var showSearchKeyword by MMKVDelegate("show_search_keyword", false) // 是否展示关键词联想
    var onlyAssociation by MMKVDelegate("only_association", false) // 仅联想，不搜索其他任何内容
    var associationEngineId by MMKVDelegate("association_engine_id", 0) // 联想引擎
    var showGuideNewEngine by MMKVDelegate("show_guide_engine", true) // 搜索引擎向导
    var homeMenuOrderJson by MMKVDelegate("home_menu_order", "")
    var gestureVibrate by MMKVDelegate("gesture_vibrate", true) // 手势震动
    var floatWindowIconBase64 by MMKVDelegate("float_window_icon_base64", "") // 悬浮窗图标
    var autoCopyWhenSearch by MMKVDelegate("autoCopyWhenSearch", false) // 自动复制搜索内容
    var openEngineIfNoSearchResult by MMKVDelegate("open_engine_if_no_search_result", false) // 搜索无结果时打开搜索引擎

    var hasMoveNewDirectTable by MMKVDelegate("has_move_new_direct_table", false) // 是否已经迁移新的 directEntity 表
    var nightMode by MMKVDelegate("night_mode", AppCompatDelegate.MODE_NIGHT_NO) // 夜间模式
}


