package justbucket.videolib.data.remote.deep_detect

import justbucket.videolib.data.remote.deep_detect.model.CategoriesRoot
import justbucket.videolib.data.remote.deep_detect.model.DDRoot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface DDApi {

    @POST
    fun getTags(
            @Url url: String = DDConstants.CLASSIFICATION_ENDPOINT,
            @Body() body: DDRoot
    ): Call<CategoriesRoot>
}