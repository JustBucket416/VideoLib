package justbucket.videolib.data.model

data class FilterEntity(val text: String = "",
                        val sources: List<Int> = emptyList(),
                        val allAnyCheck: Boolean = false,
                        val tags: List<String> = emptyList())