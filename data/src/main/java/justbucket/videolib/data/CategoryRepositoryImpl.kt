package justbucket.videolib.data

import justbucket.videolib.data.remote.ImageRepository
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor (private val imageRepository: ImageRepository): CategoryRepository{
    override suspend fun getTags(base64Image: ByteArray): Either<Failure, ArrayList<String>> {
        return imageRepository.getTags(base64Image)
    }
}