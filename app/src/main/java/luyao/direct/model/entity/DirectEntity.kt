package luyao.direct.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import luyao.direct.ext.newUID
import luyao.direct.model.ExecMode

/**
 * 应用快捷方式
 *  @author: luyao
 * @date: 2021/9/24 下午11:50
 */
@Entity(tableName = "direct_entity")
@JsonClass(generateAdapter = true)
data class DirectEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int, // 主键
    @ColumnInfo(name = "label")
    val label: String, // 名称
    @ColumnInfo(name = "app_name")
    var appName: String = "", // 应用名称
    @ColumnInfo(name = "package_name")
    val packageName: String, // 包名
    @ColumnInfo(name = "desc")
    val desc: String, // 描述
    @ColumnInfo(name = "scheme")
    val scheme: String, // urlScheme
    @ColumnInfo(name = "pinyin")
    var pinyin: String = "", // 全拼
    @ColumnInfo(name = "ex_pinyin")
    var exPinyin: String = "", // 首字母

    // 20210929
    @ColumnInfo(name = "icon_url")
    val iconUrl: String?,
    @ColumnInfo(name = "is_search")
    val isSearch: Int = 0, // 是否是搜索引擎
    @ColumnInfo(name = "search_url")
    val searchUrl: String?, // url
    @ColumnInfo(name = "engine_order")
    var order: Int = -1, // 搜索引擎排序
    @ColumnInfo(name = "is_star")
    var isStar: Int = 0, // 是否星标 0 否 1 是
    @ColumnInfo(name = "count")
    var count: Int = 0, // 使用次数
    @ColumnInfo(name = "last_time")
    val lastTime: Long = 0, // 上次使用时间
    @ColumnInfo(name = "enabled")
    var enabled: Int = 0, // 由于历史原因，0 表示启用 1 表示禁用
    @ColumnInfo(name = "star_order")
    var starOrder: Int = 0, // 星标顺序
    @ColumnInfo(name = "star_time")
    val starTime: Long = 0, // 星标状态更新时间，用于排序
    @ColumnInfo(name = "local_icon")
    var localIcon: String = "",
    @ColumnInfo(name = "tag")
    var tag: String = "",
    @ColumnInfo(name = "show_panel")
    var showPanel: Int = 0,// 是否在面板显示
    @ColumnInfo(name = "exec_mode")
    var execMode: Int = ExecMode.SCHEME // 启动模式 0 SCHEME 1 ROOT
) : Comparable<DirectEntity> {

    @Ignore
    var isFirst: Boolean = false

    @Ignore
    var isLast: Boolean = false

    @Ignore
    var isQueryByTag = false

    // 按 count 排序
    override fun compareTo(other: DirectEntity) = other.count - this.count
    fun toNew() = NewDirectEntity(
        newUID(),
        label,
        appName,
        packageName,
        desc,
        scheme,
        pinyin,
        exPinyin,
        iconUrl,
        isSearch,
        searchUrl,
        order,
        isStar,
        count,
        lastTime,
        enabled,
        starOrder,
        starTime,
        localIcon,
        tag,
        showPanel,
        execMode
    )
}