package justbucket.videolib.data.remote.deep_detect.model

import com.google.gson.annotations.SerializedName

data class DDRoot(
        @SerializedName("data")
        var data: List<String>,
        @SerializedName("parameters")
        var parameters: Parameters,
        @SerializedName("service")
        var service: String = "imageserv"
) {
    data class Parameters(
            @SerializedName("input")
            var input: Input,
            @SerializedName("mllib")
            var mllib: Mllib,
            @SerializedName("output")
            var output: Output
    ) {
        data class Input(
                @SerializedName("height")
                var height: Int = 224,
                @SerializedName("width")
                var width: Int = 224
        )

        data class Mllib(
                @SerializedName("gpu")
                var gpu: Boolean = false
        )

        data class Output(
                @SerializedName("best")
                var best: Int = 3
        )
    }
}