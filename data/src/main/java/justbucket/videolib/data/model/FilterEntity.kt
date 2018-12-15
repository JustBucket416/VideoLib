package justbucket.videolib.data.model

data class FilterEntity(val text: String = "",
                        val sources: List<Short> = emptyList(),
                        val allAnyCheck: Boolean = false,
                        val tags: List<TagEntity> = emptyList())