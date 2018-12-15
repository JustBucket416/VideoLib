package justbucket.videolib.domain.repository

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either

interface CategoryRepository {

    suspend fun getTags(base64Image: String): Either<Failure, ArrayList<String>>
}