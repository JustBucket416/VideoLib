package justbucket.videolib.data.remote.deep_detect

import justbucket.videolib.data.remote.deep_detect.model.CategoriesRoot
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface DDApi {

    @Multipart
    @POST("products/suggest_products")
    fun uploadFileImage(@Part("file\"; filename=\"img.png\" ") file: RequestBody): Call<List<CategoriesRoot>>

}