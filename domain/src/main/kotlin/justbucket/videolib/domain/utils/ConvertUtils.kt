package justbucket.videolib.domain.utils

import justbucket.videolib.domain.exception.ConvertException

@Suppress("NOTHING_TO_INLINE")
inline fun <T> T?.getOrDie(binding: String): T = this
        ?: throw ConvertException("'$binding' must not be null")