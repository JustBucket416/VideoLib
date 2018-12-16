package justbucket.videolib.domain.repository

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either

interface CategoryRepository {

    suspend fun getTags(base64Image: ByteArray): Either<Failure, ArrayList<String>>
}