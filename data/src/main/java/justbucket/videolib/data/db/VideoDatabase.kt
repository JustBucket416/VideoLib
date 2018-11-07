package justbucket.videolib.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import justbucket.videolib.data.db.DBConstants.QUERY_CALLBACK_LOCAL
import justbucket.videolib.data.db.DBConstants.QUERY_CALLBACK_YOUTUBE
import justbucket.videolib.data.db.dao.LinkDao
import justbucket.videolib.data.db.dao.SourceDao
import justbucket.videolib.data.db.dao.TagDao
import justbucket.videolib.data.db.dao.VideoDao
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.model.SourceEntity
import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.data.model.VideoEntity

/**
 * A [RoomDatabase] subclass that acts as database instance
 */
@Database(entities = [(VideoEntity::class), (TagEntity::class), (LinkEntity::class), (SourceEntity::class)], version = 1, exportSchema = false)
abstract class VideoDatabase : RoomDatabase() {

    /**
     * @return generated [VideoDao] class
     */
    abstract fun videoDao(): VideoDao

    /**
     * @return generated [TagDao] class
     */
    abstract fun tagDao(): TagDao

    /**
     * @return generated [LinkDao] class
     */
    abstract fun linkDao(): LinkDao

    /**
     * @return generated [SourceDao] class
     */
    abstract fun sourceDao(): SourceDao

    companion object {
        @Volatile
        private var INSTANCE: VideoDatabase? = null

        /**
         * A database singleton method
         *
         * @return [VideoDatabase] instance
         */
        fun getInstance(context: Context): VideoDatabase {
            if (INSTANCE == null) {
                synchronized(VideoDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                VideoDatabase::class.java, "database.db")
                                .addCallback(object : Callback() {
                                    override fun onCreate(db: SupportSQLiteDatabase) {
                                        db.execSQL(QUERY_CALLBACK_LOCAL)
                                        db.execSQL(QUERY_CALLBACK_YOUTUBE)
                                    }
                                })
                                .build()
                    }
                    return INSTANCE as VideoDatabase
                }
            }
            return INSTANCE as VideoDatabase
        }
    }

}
