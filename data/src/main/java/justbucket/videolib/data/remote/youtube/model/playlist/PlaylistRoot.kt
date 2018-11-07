package justbucket.videolib.data.remote.youtube.model.playlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A [Gson] model class
 */
data class PlaylistRoot(
        @SerializedName("nextPageToken")
        @Expose
        var nextPageToken: String?,
        @SerializedName("items")
        @Expose
        var items: List<Item>
)