package justbucket.videolib.data.remote.deep_detect

import justbucket.videolib.data.remote.deep_detect.model.CategoriesRoot
import justbucket.videolib.data.remote.deep_detect.model.DDRoot
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface DDApi {

    @Multipart
    @POST
    fun uploadFileImage(@Part("file\"; filename=\"img.png\" ") file: RequestBody): Call<CategoriesRoot>

}