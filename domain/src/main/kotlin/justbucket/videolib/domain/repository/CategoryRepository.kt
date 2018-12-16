package justbucket.videolib.domain.repository

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import okhttp3.RequestBody

interface CategoryRepository {

    suspend fun getTags(base64Image: RequestBody): Either<Failure, ArrayList<String>>
}