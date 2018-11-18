package justbucket.videolib.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import justbucket.videolib.data.remote.youtube.YoutubeAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * A helper class that creates a [YoutubeAPI] instance
 */
class RetrofitHelper @Inject constructor() {

    /**
     * Creates [Gson] instance passes it to [Retrofit.Builder] and creates an instance of [YoutubeAPI] class
     *
     * @return a created [T] instance
     */
    inline fun <reified T> buildApi(url: String): T {
        val gson = GsonBuilder()
                .setLenient()
                .serializeNulls()
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(T::class.java)
    }

}