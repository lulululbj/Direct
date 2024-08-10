package luyao.direct.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

/**
 *  @author: luyao
 * @date: 2021/7/9 下午11:25
 */
@JsonClass(generateAdapter = true)
@Entity(tableName = "app_entity")
data class AppEntity(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "app_name")
    val appName: String,
    @ColumnInfo(name = "version_name")
    val versionName: String,
    @ColumnInfo(name = "version_code")
    val versionCode: Int,
    @ColumnInfo(name = "app_icon")
    val appIcon: ByteArray?,
    @ColumnInfo(name = "is_system")
    val isSystem: Boolean, // 是否系统应用
    @ColumnInfo(name = "is_install")
    val isInstalled: Boolean,
    @ColumnInfo(name = "count")
    val count: Int, // 使用次数
    @ColumnInfo(name = "last_time")
    val lastTime: Long, // 上次使用时间
    @ColumnInfo(name = "pinyin")
    val pinyin: String, // 全拼
    @ColumnInfo(name = "ex_pinyin")
    val exPinyin: String, // 首字母

    // 20210930
    @ColumnInfo(name = "is_star")
    var isStar: Int, // 是否星标 0 否 1 是
    @ColumnInfo(name = "star_order")
    val starOrder: Int, // 星标顺序
    @ColumnInfo(name = "star_time")
    val starTime: Long, // 星标状态更新时间，用于排序
    // 20211113
//    @ColumnInfo(name = "ice_state")
//    var iceState : Int // 冰箱冻结状态，
) : Comparable<AppEntity> {

    @Ignore
    var isFirst: Boolean = false

    @Ignore
    var isLast: Boolean = false

    @Ignore
    var isSelected: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppEntity

//        if (isFirst != other.isFirst) return false
        if (packageName != other.packageName) return false
        if (appName != other.appName) return false
        if (versionName != other.versionName) return false
        if (versionCode != other.versionCode) return false
        if (!appIcon.contentEquals(other.appIcon)) return false
        if (isInstalled != other.isInstalled) return false
        if (isStar != other.isStar) return false
        return true
    }

    override fun hashCode(): Int {
        var result = packageName.hashCode()
        result = 31 * result + appName.hashCode()
        result = 31 * result + versionName.hashCode()
        result = 31 * result + versionCode.hashCode()
        result = 31 * result + appIcon.contentHashCode()
        result = 31 * result + isInstalled.hashCode()
        return result
    }

    // 按 count 排序
    override fun compareTo(other: AppEntity) = other.count - this.count
}

