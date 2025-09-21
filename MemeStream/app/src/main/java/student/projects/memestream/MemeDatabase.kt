package student.projects.memestream

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [Meme::class],
    version = 1,
    exportSchema = false
)
abstract class MemeDatabase : RoomDatabase() {
    abstract fun memeDao(): MemeDao

    companion object {
        @Volatile
        private var INSTANCE: MemeDatabase? = null

        fun getDatabase(context: Context): MemeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemeDatabase::class.java,
                    "meme_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
