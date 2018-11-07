package justbucket.videolib.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import justbucket.videolib.data.db.DBConstants.COLUMN_TAG_ID
import justbucket.videolib.data.db.DBConstants.COLUMN_TAG_TEXT
import justbucket.videolib.data.db.DBConstants.TABLE_TAG_NAME

/**
 * A class that represents a table which stores tag details
 */
@Entity(tableName = TABLE_TAG_NAME)
data class TagEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = COLUMN_TAG_ID)
        var id: Long? = null,

        @ColumnInfo(name = COLUMN_TAG_TEXT, index = true)
        var text: String
)