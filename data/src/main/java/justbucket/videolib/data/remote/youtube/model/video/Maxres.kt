package justbucket.videolib.data.remote.youtube.model.video

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A [Gson] model class
 */
data class Maxres(

    @SerializedName("url")
    @Expose
    var url: String? = null

)
