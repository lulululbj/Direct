package luyao.direct.model.dao

import androidx.room.*
import luyao.direct.model.entity.SearchHistoryEntity

/**
 * author: luyao
 * date:   2021/10/20 19:59
 */
@Dao
interface SearchHistoryDao {

    @Query("select * from search_history where key_word = :keyWord")
    fun load(keyWord: String): SearchHistoryEntity?

    @Query("select * from search_history order by time desc")
    fun loadAll(): List<SearchHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistoryEntity: SearchHistoryEntity)

    @Query("update search_history set count =count+1, time = :time where key_word = :word")
    fun updateAndAddCount(word: String, time: Long)

    @Delete
    fun delete(entity: SearchHistoryEntity)

    @Query("delete from search_history where key_word = :keyWord")
    fun deleteKeyWord(keyWord: String)

    @Query("delete from search_history")
    fun deleteAll()
}