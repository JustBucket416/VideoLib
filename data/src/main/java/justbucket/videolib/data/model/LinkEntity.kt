package justbucket.videolib.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.Index
import justbucket.videolib.data.db.DBConstants.COLUMN_LINK_TAG_ID
import justbucket.videolib.data.db.DBConstants.COLUMN_LINK_VIDEO_ID
import justbucket.videolib.data.db.DBConstants.COLUMN_TAG_ID
import justbucket.videolib.data.db.DBConstants.COLUMN_VIDEO_ID
import justbucket.videolib.data.db.DBConstants.TABLE_LINK_NAME

/**
 * A class that represents helper table used to store many-to-many references
 */
@Entity(tableName = TABLE_LINK_NAME,
        primaryKeys = [COLUMN_LINK_VIDEO_ID, COLUMN_LINK_TAG_ID],
        foreignKeys = [
            ForeignKey(entity = VideoEntity::class,
                    parentColumns = [COLUMN_VIDEO_ID],
                    childColumns = [COLUMN_LINK_VIDEO_ID],
                    onDelete = CASCADE),
            ForeignKey(entity = TagEntity::class,
                    parentColumns = [COLUMN_TAG_ID],
                    childColumns = [COLUMN_LINK_TAG_ID],
                    onDelete = CASCADE)],
        indices = [Index(COLUMN_LINK_TAG_ID)])
data class LinkEntity(

        @ColumnInfo(name = COLUMN_LINK_VIDEO_ID)
        var videoId: Long,

        @ColumnInfo(name = COLUMN_LINK_TAG_ID)
        var tagId: Long
)