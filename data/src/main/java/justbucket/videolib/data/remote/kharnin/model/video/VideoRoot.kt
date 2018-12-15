package justbucket.videolib.data.remote.kharnin.model.video

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import justbucket.videolib.data.remote.kharnin.model.tag.TagRoot

data class VideoRoot(
        @SerializedName("id")
        @Expose
        val id: Long,
        @SerializedName("source_id")
        @Expose
        val sourceId: Short,
        @SerializedName("title")
        @Expose
        val title: String,
        @SerializedName("video_path")
        @Expose
        val videoPath: String,
        @SerializedName("thumb_path")
        @Expose
        val thumbPath: String,
        @SerializedName("tags")
        @Expose
        val tags: List<TagRoot>
)