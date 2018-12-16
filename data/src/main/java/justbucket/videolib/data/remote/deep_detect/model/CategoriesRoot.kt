package justbucket.videolib.data.remote.deep_detect.model

import com.google.gson.annotations.SerializedName

data class CategoriesRoot (
        @SerializedName("tags")
        var tags: List<String>
)