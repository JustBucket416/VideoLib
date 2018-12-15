package justbucket.videolib.domain.model

data class Filter(val text: String,
                  val sources: List<Short>,
                  val allAnyCheck: Boolean,
                  val tags: List<Tag>)