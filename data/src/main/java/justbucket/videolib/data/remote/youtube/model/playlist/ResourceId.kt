package justbucket.videolib.data.remote.youtube.model.playlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A [Gson] model class
 */
data class ResourceId(

        @SerializedName("videoId")
        @Expose
        var videoId: String
)
