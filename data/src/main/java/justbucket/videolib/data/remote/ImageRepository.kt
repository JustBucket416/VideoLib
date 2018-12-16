package justbucket.videolib.data.remote

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import okhttp3.RequestBody

interface ImageRepository {

    suspend fun getTags(body: RequestBody): Either<Failure, ArrayList<String>>
}