package justbucket.videolib.domain.model

data class Video(val id: Long,
                 val title: String,
                 val videoPath: String,
                 val thumbPath: String,
                 val source: Short,
                 val tags: List<Tag>)