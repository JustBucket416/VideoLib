package justbucket.videolib.data.remote.youtube.model.playlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A [Gson] model class
 */
data class Medium(

        @SerializedName("url")
    @Expose
        var url: String? = null,
        @SerializedName("width")
    @Expose
        var width: Int? = null,
        @SerializedName("height")
    @Expose
    var height: Int? = null

)
