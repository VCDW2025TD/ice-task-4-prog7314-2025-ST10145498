package student.projects.memestream

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memes")
data class Meme(
    @PrimaryKey val id: String,
    val userId: String,
    val imageUrl: String,
    val caption: String,
    val lat: Double,
    val lng: Double,
    val timestamp: String,
    val synced: Boolean = false
)