package justbucket.videolib.data.remote.youtube.model.temp

data class Snippet(
        val channelId: String,
        val channelTitle: String,
        val description: String,
        val liveBroadcastContent: String,
        val publishedAt: String,
        val thumbnails: Thumbnails,
        val title: String
)