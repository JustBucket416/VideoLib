package justbucket.videolib.data.remote.youtube.model.playlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import justbucket.videolib.data.remote.youtube.model.video.Default
import justbucket.videolib.data.remote.youtube.model.video.High
import justbucket.videolib.data.remote.youtube.model.video.Maxres
import justbucket.videolib.data.remote.youtube.model.video.Medium
import justbucket.videolib.data.remote.youtube.model.video.Standard

/**
 * A [Gson] model class
 */
class Thumbnails(

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
