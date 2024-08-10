package luyao.direct.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore

/**
 *  @author: luyao
 * @date: 2021/7/10 下午11:49
 */
@Entity(tableName = "contact_entity", primaryKeys = ["contact_id", "phone_number"])
data class ContactEntity(
    @ColumnInfo(name = "contact_id")
    val contactId: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "count")
    val count: Int, // 使用次数
    @ColumnInfo(name = "last_time")
    val lastTime: Long
) {
    @Ignore
    var isFirst: Boolean = false

    @Ignore
    var isLast: Boolean = false
}