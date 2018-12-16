package justbucket.videolib.data.remote.deep_detect

import justbucket.videolib.data.remote.ImageRepository
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import okhttp3.RequestBody
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val ddapi: DDApi): ImageRepository {
    override suspend fun getTags(body: RequestBody): Either<Failure, ArrayList<String>> {
        val response =  ddapi.uploadFileImage(body).execute()
        return if (response.isSuccessful){
            Either.Right(ArrayList(response.body()?.get(0)?.tags!!))
        } else{
            Either.Left(Failure.NetworkFailure)
        }
    }
}