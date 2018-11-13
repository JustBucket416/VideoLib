package justbucket.videolib.data.remote.youtube.model.video

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A [Gson] model class
 */
data class Item(

        @SerializedName("id")
        @Expose
        var id: String,
        @SerializedName("snippet")
        @Expose
        var snippet: Snippet

)
