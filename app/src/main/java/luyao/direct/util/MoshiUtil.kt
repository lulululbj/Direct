package luyao.direct.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import luyao.direct.model.entity.*

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/20 10:42
 */
object MoshiUtil {

    private val moshi = Moshi.Builder().build()
    val recentEntityAdapter: JsonAdapter<RecentEntity?> = moshi.adapter(RecentEntity::class.java)
    val backupEntityAdapter: JsonAdapter<BackupEntity> = moshi.adapter(BackupEntity::class.java)
    val newBackupEntityAdapter: JsonAdapter<NewBackupEntity> = moshi.adapter(NewBackupEntity::class.java)
    val directEntityListAdapter: JsonAdapter<List<DirectEntity>> = moshi.adapter(
        Types.newParameterizedType(
            List::class.java,
            DirectEntity::class.java
        )
    )
    val recentEntityListAdapter: JsonAdapter<List<RecentEntity>> = moshi.adapter(
        Types.newParameterizedType(
            List::class.java,
            RecentEntity::class.java
        )
    )

    val homeMenuListAdapter: JsonAdapter<List<HomeMenu>> = moshi.adapter(
        Types.newParameterizedType(List::class.java, HomeMenu::class.java)
    )

    val directListEntityAdapter : JsonAdapter<DirectListEntity> = moshi.adapter(DirectListEntity::class.java)
}