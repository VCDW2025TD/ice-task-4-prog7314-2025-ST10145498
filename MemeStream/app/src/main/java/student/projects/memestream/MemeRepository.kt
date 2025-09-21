package student.projects.memestream

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*

class MemeRepository(private val context: Context) {
    private val database = MemeDatabase.getDatabase(context)
    private val memeDao = database.memeDao()
    private val apiService = NetworkModule.apiService

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun getAllMemes(): Flow<List<Meme>> {
        if (isNetworkAvailable()) {
            try {
                syncFromServer()
            } catch (e: Exception) {
                // Continue with local data if sync fails
            }
        }
        return memeDao.getAllMemes()
    }

    suspend fun getMemesByUser(userId: String): Flow<List<Meme>> {
        if (isNetworkAvailable()) {
            try {
                syncFromServer()
            } catch (e: Exception) {
                // Continue with local data if sync fails
            }
        }
        return memeDao.getMemesByUser(userId)
    }

    suspend fun insertMeme(meme: MemePost): Boolean {
        val localMeme = Meme(
            id = meme.id ?: UUID.randomUUID().toString(),
            userId = meme.userId,
            imageUrl = meme.imageUrl,
            caption = meme.caption,
            lat = meme.lat,
            lng = meme.lng,
            timestamp = meme.timestamp,
            synced = false
        )

        // Always save locally first
        memeDao.insertMeme(localMeme)

        // Try to sync to server if online
        if (isNetworkAvailable()) {
            return try {
                val apiMeme = ApiMeme(
                    userId = meme.userId,
                    imageUrl = meme.imageUrl,
                    caption = meme.caption,
                    lat = meme.lat,
                    lng = meme.lng,
                    timestamp = meme.timestamp
                )
                val response = apiService.postMeme(apiMeme)
                if (response.isSuccessful) {
                    // Update local copy as synced
                    memeDao.updateMeme(localMeme.copy(synced = true))
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
        return false // Saved locally but not synced
    }

    suspend fun syncToServer() {
        if (!isNetworkAvailable()) return

        val unsyncedMemes = memeDao.getUnsyncedMemes()
        for (meme in unsyncedMemes) {
            try {
                val apiMeme = ApiMeme(
                    userId = meme.userId,
                    imageUrl = meme.imageUrl,
                    caption = meme.caption,
                    lat = meme.lat,
                    lng = meme.lng,
                    timestamp = meme.timestamp
                )
                val response = apiService.postMeme(apiMeme)
                if (response.isSuccessful) {
                    memeDao.updateMeme(meme.copy(synced = true))
                }
            } catch (e: Exception) {
                // Continue with next meme
            }
        }
    }

    private suspend fun syncFromServer() {
        try {
            val response = apiService.getMemes()
            if (response.isSuccessful) {
                val serverMemes = response.body() ?: emptyList()
                val localMemes = serverMemes.map { apiMeme ->
                    Meme(
                        id = apiMeme._id ?: UUID.randomUUID().toString(),
                        userId = apiMeme.userId,
                        imageUrl = apiMeme.imageUrl,
                        caption = apiMeme.caption,
                        lat = apiMeme.lat,
                        lng = apiMeme.lng,
                        timestamp = apiMeme.timestamp,
                        synced = true
                    )
                }
                memeDao.insertMemes(localMemes)
            }
        } catch (e: Exception) {
            // Sync failed, continue with local data
        }
    }
}