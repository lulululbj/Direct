package luyao.direct.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import luyao.direct.model.entity.ContactEntity

/**
 *  @author: luyao
 * @date: 2021/7/10 下午11:54
 */
@Dao
interface ContactDao {

    @Insert
    fun insertAll(contactsList: List<ContactEntity>)

    @Query("select * from contact_entity where display_name like :keyword or phone_number like :keyword")
    suspend fun searchContactByKeyword(keyword: String): List<ContactEntity>

    @Query("delete from contact_entity")
    fun deleteAll()
}