package justbucket.videolib.domain.model

data class Video(val id: Long,
                 val title: String,
                 val videoPath: String,
                 val thumbPath: String,
                 val source: Int,
                 val tags: List<Tag>)