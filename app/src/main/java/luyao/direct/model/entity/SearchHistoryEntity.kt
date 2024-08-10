package luyao.direct.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * author: luyao
 * date:   2021/10/20 19:48
 */
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int, // 主键
    @ColumnInfo(name = "key_word")
    val keyWord: String, // 关键字
    @ColumnInfo(name = "type")
    val type: Int, // 类型
    @ColumnInfo(name = "time")
    val time: Long, // 时间
    @ColumnInfo(name = "count")
    var count: Int // 次数
) {
    companion object {
        const val APP = 0
        const val DIRECT = 1
        const val CONTACT = 2
        const val ENGINE = 3
    }
}