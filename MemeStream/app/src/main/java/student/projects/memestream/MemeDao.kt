package student.projects.memestream

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemeDao {
    @Query("SELECT * FROM memes ORDER BY timestamp DESC")
    fun getAllMemes(): Flow<List<Meme>>

    @Query("SELECT * FROM memes WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMemesByUser(userId: String): Flow<List<Meme>>

    @Query("SELECT * FROM memes WHERE synced = 0")
    suspend fun getUnsyncedMemes(): List<Meme>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeme(meme: Meme)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemes(memes: List<Meme>)

    @Update
    suspend fun updateMeme(meme: Meme)

    @Delete
    suspend fun deleteMeme(meme: Meme)

    @Query("DELETE FROM memes")
    suspend fun deleteAllMemes()
}