package justbucket.videolib.data.remote.youtube.model.playlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A [Gson] model class
 */
data class Thumbnails(

        @SerializedName("default")
    @Expose
        var default: Default? = null,
        @SerializedName("medium")
    @Expose
        var medium: Medium? = null,
        @SerializedName("high")
    @Expose
        var high: High? = null,
        @SerializedName("standard")
    @Expose
        var standard: Standard? = null,
        @SerializedName("maxres")
    @Expose
    var maxres: Maxres? = null

)
