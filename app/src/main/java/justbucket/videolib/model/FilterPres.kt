package justbucket.videolib.model

data class FilterPres(val text: String,
                      val sources: MutableList<Int>,
                      var isAllAnyCheck: Boolean = false,
                      val tags: MutableList<String>)
