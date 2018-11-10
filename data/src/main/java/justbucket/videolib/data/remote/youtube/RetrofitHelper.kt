package justbucket.videolib.data.remote.youtube

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * A helper class that creates a [YoutubeAPI] instance
 */
class RetrofitHelper @Inject constructor() {

    companion object {
        private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    }

    /**
     * Creates [Gson] instance passes it to [Retrofit.Builder] and creates an instance of [YoutubeAPI] class
     *
     * @return a created [YoutubeAPI] instance
     */
    fun getYoutubeApi(): YoutubeAPI = with(Retrofit.Builder()) {
        baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }.create(YoutubeAPI::class.java)

    private fun createGson() =
            GsonBuilder()
                    .setLenient()
                    .serializeNulls()
                    .create()

}