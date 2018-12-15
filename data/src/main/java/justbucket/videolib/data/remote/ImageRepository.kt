package justbucket.videolib.data.remote

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either

interface ImageRepository {

    suspend fun getTags(stringBase64: String): Either<Failure, ArrayList<String>>
}