package justbucket.videolib.data.remote.deep_detect

import justbucket.videolib.data.remote.ImageRepository
import justbucket.videolib.data.remote.deep_detect.model.DDRoot
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val ddapi: DDApi): ImageRepository {
    override suspend fun getTags(stringBase64: ByteArray): Either<Failure, ArrayList<String>> {
        val ddRoot = DDRoot(
                data = arrayListOf(),
                parameters = DDRoot.Parameters(
                        DDRoot.Parameters.Input(),
                        DDRoot.Parameters.Mllib(),
                        DDRoot.Parameters.Output()
                )
        )
        val response =  ddapi.getTags(body = ddRoot).execute()
        return if (response.isSuccessful){
            Either.Right(ArrayList(response.body()?.cat!!))
        } else{
            Either.Left(Failure.NetworkFailure)
        }
    }
}