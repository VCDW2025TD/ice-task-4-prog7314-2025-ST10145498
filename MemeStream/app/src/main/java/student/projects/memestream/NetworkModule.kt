package student.projects.memestream

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
    private const val GIPHY_BASE_URL = "https://api.giphy.com/v1/gifs/"
    private const val API_BASE_URL = "https://hostmemestream-1.onrender.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val giphyRetrofit = Retrofit.Builder()
        .baseUrl(GIPHY_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiRetrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val giphyService: GiphyService = giphyRetrofit.create(GiphyService::class.java)
    val apiService: ApiService = apiRetrofit.create(ApiService::class.java)
}