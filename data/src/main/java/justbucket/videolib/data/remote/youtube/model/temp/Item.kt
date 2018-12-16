package justbucket.videolib.data.remote.youtube.model.temp

data class Item(
        val etag: String,
        val id: Id,
        val kind: String,
        val snippet: Snippet
)