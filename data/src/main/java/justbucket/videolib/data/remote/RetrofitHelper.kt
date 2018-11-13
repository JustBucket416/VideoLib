package justbucket.videolib.data.remote

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

    /**
     * Creates [Gson] instance passes it to [Retrofit.Builder] and creates an instance of [YoutubeAPI] class
     *
     * @return a created [YoutubeAPI] instance
     */
    inline fun <reified T> getNetworkApi(url: String): T = with(Retrofit.Builder()) {
        val gson = GsonBuilder()
                .setLenient()
                .serializeNulls()
                .create()

        baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }.create(T::class.java)
}