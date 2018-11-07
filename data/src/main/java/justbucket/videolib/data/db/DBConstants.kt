package justbucket.videolib.data.db

import justbucket.videolib.data.R

/**
 * Some SQL requests and table and column names. I find them self-explanatory, so not gonna comment them
 */
object DBConstants {

    const val TABLE_VIDEO_NAME = "videos"

    const val TABLE_TAG_NAME = "tags"

    const val TABLE_LINK_NAME = "links"

    const val TABLE_SOURCE_NAME = "sources"

    const val COLUMN_VIDEO_ID = "id"

    const val COLUMN_TAG_ID = "id"

    const val COLUMN_SOURCE_ID = "id"

    const val COLUMN_TAG_TEXT = "text"

    const val COLUMN_SOURCE_TEXT = "sourceId"

    const val COLUMN_LINK_VIDEO_ID = "video_id"

    const val COLUMN_LINK_TAG_ID = "tag_id"

    const val COLUMN_VIDEO_TITLE = "title"

    //SourceEntity queries
    const val QUERY_ALL_SOURCES = "SELECT * FROM $TABLE_SOURCE_NAME ORDER BY $COLUMN_SOURCE_TEXT ASC"

    val QUERY_CALLBACK_LOCAL = "INSERT OR IGNORE INTO $TABLE_SOURCE_NAME VALUES(0, ${R.drawable.ic_storage})"

    val QUERY_CALLBACK_YOUTUBE = "INSERT OR IGNORE INTO $TABLE_SOURCE_NAME VALUES(1, ${R.drawable.ic_youtube})"

    const val QUERY_BY_SOURCE_ID = "SELECT $COLUMN_SOURCE_TEXT FROM $TABLE_SOURCE_NAME WHERE $COLUMN_SOURCE_ID = :sourceId"

    const val QUERY_BY_SOURCE_INT = "SELECT $COLUMN_SOURCE_ID FROM $TABLE_SOURCE_NAME WHERE $COLUMN_SOURCE_TEXT = :source"

    //LinkEntity queries
    const val QUERY_GET_ALL_LINKS = "SELECT * FROM $TABLE_LINK_NAME"

    const val QUERY_DELETE_ALL_LINKS = "DELETE FROM $TABLE_LINK_NAME WHERE $COLUMN_LINK_VIDEO_ID = :videoId"

    //TagEntity queries
    const val QUERY_ALL_TAGS = "SELECT * FROM $TABLE_TAG_NAME ORDER BY $COLUMN_TAG_TEXT ASC"

    const val QUERY_TAG_BY_ID = "SELECT $COLUMN_TAG_ID FROM $TABLE_TAG_NAME WHERE $COLUMN_TAG_TEXT = :tagText"

    const val QUERY_TAG_BY_TEXT = "SELECT * FROM $TABLE_TAG_NAME WHERE $COLUMN_TAG_TEXT = :tagText"

    //VideoEntity queries
    const val QUERY_VIDEO_BY_ID = "SELECT * FROM $TABLE_VIDEO_NAME WHERE $COLUMN_VIDEO_ID = :id"

    const val QUERY_VIDEO_BY_PATH = "SELECT * FROM $TABLE_VIDEO_NAME WHERE videoPath = :path"

    const val QUERY_ALL_VIDEOS = "SELECT * FROM $TABLE_VIDEO_NAME ORDER BY $COLUMN_VIDEO_TITLE ASC"
}