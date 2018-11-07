package justbucket.videolib.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import justbucket.videolib.data.db.DBConstants.COLUMN_SOURCE_ID
import justbucket.videolib.data.db.DBConstants.COLUMN_SOURCE_TEXT
import justbucket.videolib.data.db.DBConstants.TABLE_SOURCE_NAME

/**
 * A class that represents tab;e which stores source details
 */
@Entity(tableName = TABLE_SOURCE_NAME)
data class SourceEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = COLUMN_SOURCE_ID)
        var id: Long? = null,

        @ColumnInfo(name = COLUMN_SOURCE_TEXT)
        var source: Int
)