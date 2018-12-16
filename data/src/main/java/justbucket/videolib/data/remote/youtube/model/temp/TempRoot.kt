package justbucket.videolib.data.remote.youtube.model.temp

data class TempRoot(
        val etag: String,
        val items: List<Item>,
        val kind: String,
        val nextPageToken: String,
        val pageInfo: PageInfo,
        val regionCode: String
)