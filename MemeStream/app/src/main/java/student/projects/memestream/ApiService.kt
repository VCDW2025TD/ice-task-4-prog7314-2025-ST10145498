package student.projects.memestream

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.*

data class GiphyResponse(
    val data: List<GiphyGif>
)

data class GiphyGif(
    val id: String,
    val title: String,
    val images: GiphyImages
)

data class GiphyImages(
    @Json(name = "fixed_height")
    val fixedHeight: GiphyImageData
)

data class GiphyImageData(
    val url: String
)

data class MemePost(
    val id: String? = null,
    val userId: String,
    val imageUrl: String,
    val caption: String,
    val lat: Double,
    val lng: Double,
    val timestamp: String,
    val synced: Boolean = false
)

data class ApiMeme(
    val _id: String? = null,
    val userId: String,
    val imageUrl: String,
    val caption: String,
    val lat: Double,
    val lng: Double,
    val timestamp: String
)

interface ApiService {
    @GET("search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String = "GlVGYHkr3WSBnllca54iNt0yFbjz7L65",
        @Query("q") query: String = "meme",
        @Query("limit") limit: Int = 25
    ): Response<GiphyResponse>

    @POST("memes")
    suspend fun postMeme(@Body meme: ApiMeme): Response<ApiMeme>

    @GET("memes")
    suspend fun getMemes(): Response<List<ApiMeme>>

    @GET("memes")
    suspend fun getMemesByUser(@Query("userId") userId: String): Response<List<ApiMeme>>
}

interface GiphyService {
    @GET("search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int
    ): Response<GiphyResponse>
}