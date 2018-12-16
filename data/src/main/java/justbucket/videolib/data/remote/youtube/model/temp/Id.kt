package justbucket.videolib.data.remote.youtube.model.temp

import com.google.gson.annotations.SerializedName

data class Id(
        val kind: String,
        @SerializedName("videoId")
        val videoId: String?
)