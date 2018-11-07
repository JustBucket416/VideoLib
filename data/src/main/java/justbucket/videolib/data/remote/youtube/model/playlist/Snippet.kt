package justbucket.videolib.data.remote.youtube.model.playlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A [Gson] model class
 */
class Snippet {


    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("thumbnails")
    @Expose
    var thumbnails: Thumbnails? = null
    @SerializedName("channelTitle")
    @Expose
    var channelTitle: String? = null
    @SerializedName("resourceId")
    @Expose
    var resourceId: ResourceId? = null

}
