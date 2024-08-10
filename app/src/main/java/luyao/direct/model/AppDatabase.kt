package luyao.direct.model

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import luyao.direct.model.dao.*
import luyao.direct.model.entity.*

/**
 *  @author: luyao
 * @date: 2021/7/10 上午12:01
 */
@Database(
    entities = [AppEntity::class, ContactEntity::class, DirectEntity::class,
        SearchHistoryEntity::class, NewDirectEntity::class],
    version = 12,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 11, to = 12)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun contactDao(): ContactDao
    abstract fun directDao(): DirectDao
    abstract fun newDirectDao(): NewDirectDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java, "direct"
            )
                .addMigrations(
                    migration1_2, migration2_3, migration3_4, migration4_5, migration5_6,
                    migration6_7, migration7_8, migration8_9, migration9_10, migration10_11
                )
                .build()


        private val migration1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table `search_engine` (`id` INTEGER PRIMARY KEY not null, `name` TEXT not null, `package_name` TEXT not null,`url` TEXT not null,`schema` TEXT not null,`position` INTEGER not null, `res_id` INTEGER not null) ")
            }
        }

        private val migration2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `app_entity` add `pinyin` TEXT not null default ''")
                database.execSQL("alter table `app_entity` add `ex_pinyin` TEXT not null default ''")
            }
        }

        private val migration3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table `direct_entity` (`id` INTEGER PRIMARY KEY not null, `label` TEXT NOT NULL, `app_name` TEXT NOT NULL, `package_name` TEXT NOT NULL, `desc` TEXT NOT NULL, `scheme` TEXT NOT NULL, `pinyin` TEXT NOT NULL,`ex_pinyin` TEXT NOT NULL)")
            }
        }

        private val migration4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `direct_entity` add `is_search` INTEGER not null default 0")
                database.execSQL("alter table `direct_entity` add `search_url` TEXT")
                database.execSQL("alter table `direct_entity` add `engine_order` INTEGER not null default -1")
                database.execSQL("alter table `direct_entity` add `is_star` INTEGER not null default 0")
                database.execSQL("alter table `direct_entity` add `count` INTEGER not null default 0")
                database.execSQL("alter table `direct_entity` add `last_time` INTEGER not null default 0")
                database.execSQL("alter table `direct_entity` add `enabled` INTEGER not null default 1")
                database.execSQL("alter table `app_entity` add `is_star` INTEGER not null default 0")
            }
        }

        private val migration5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `direct_entity` add `icon_url` TEXT")
            }
        }

        private val migration6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `app_entity` add `star_order` INTEGER not null default 0")
                database.execSQL("alter table `app_entity` add `star_time` INTEGER not null default 0")
                database.execSQL("alter table `direct_entity` add `star_order` INTEGER not null default 0")
                database.execSQL("alter table `direct_entity` add `star_time` INTEGER not null default 0")
            }
        }

        private val migration7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.run {
                    execSQL("drop table `search_engine`")
                    execSQL("create table `search_history` (`id` INTEGER PRIMARY KEY not null, `key_word` TEXT NOT NULL, `type` INTEGER NOT NULL, `time` INTEGER NOT NULL, `count` INTEGER NOT NULL)")
                }
            }
        }

        private val migration8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `direct_entity` add `local_icon` TEXT not null default ''")
            }
        }

        private val migration9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `direct_entity` add `tag` TEXT not null default ''")
                database.execSQL("alter table `direct_entity` add `show_panel` INTEGER not null default 1")
            }
        }

        private val migration10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table `direct_entity` add `exec_mode` INTEGER not null default 0")
            }
        }
    }
}