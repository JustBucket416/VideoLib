package justbucket.videolib.domain.model

data class Filter(val text: String,
                  val sources: List<Int>,
                  val allAnyCheck: Boolean,
                  val tags: List<String>)