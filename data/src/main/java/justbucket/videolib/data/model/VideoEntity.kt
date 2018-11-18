package justbucket.videolib.data.model

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import justbucket.videolib.data.db.DBConstants
import justbucket.videolib.data.db.DBConstants.COLUMN_VIDEO_ID
import justbucket.videolib.data.db.DBConstants.COLUMN_VIDEO_TITLE
import justbucket.videolib.data.db.DBConstants.TABLE_VIDEO_NAME

/**
 * A class that represents a table which stores video details
 */
@Entity(tableName = TABLE_VIDEO_NAME,
        foreignKeys = [ForeignKey(entity = SourceEntity::class,
                parentColumns = [DBConstants.COLUMN_SOURCE_ID],
                childColumns = [DBConstants.COLUMN_SOURCE_TEXT],
                onDelete = CASCADE)],
        indices = [Index("sourceId"), Index("title"), Index("videoPath", unique = true)])
data class VideoEntity(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = COLUMN_VIDEO_ID)
        var id: Long? = null,

        @ColumnInfo
        var sourceId: Int,

        @ColumnInfo(name = COLUMN_VIDEO_TITLE)
        var title: String,

        @ColumnInfo
        var videoPath: String,

        @ColumnInfo
        var thumbPath: String
)